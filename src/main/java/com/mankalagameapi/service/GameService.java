package com.mankalagameapi.service;

import com.mankalagameapi.dto.GameDto;
import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.mankalagameapi.consts.GameConstants.DEFAULT_PIT_STONES_COUNT;
import static com.mankalagameapi.consts.GameConstants.STORE_ID_BY_PLAYER;
import static com.mankalagameapi.model.Player.PLAYER_1;
import static com.mankalagameapi.model.Player.PLAYER_2;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@RequiredArgsConstructor
@EnableConfigurationProperties(GameProperties.class)
@Service
public class GameService {

    private final GameStorageService gameStorageService;
    private final ModelMapper modelMapper;
    private final GameProperties gameProperties;

    public GameDto createGame() {
        Integer pitStonesCount = gameProperties.getPitStones();
        int finalPitStonesCount = pitStonesCount == null ? DEFAULT_PIT_STONES_COUNT : pitStonesCount;

        List<Pit> pits = new ArrayList<>();

        int firstPlayerStoreId = STORE_ID_BY_PLAYER.get(PLAYER_1);
        IntStream.range(1, firstPlayerStoreId)
                .forEach(num -> pits.add(new House(num, finalPitStonesCount, PLAYER_1)));

        pits.add(new Store(firstPlayerStoreId, PLAYER_1));

        int secondPlayerStoreId = STORE_ID_BY_PLAYER.get(PLAYER_2);
        IntStream.range(firstPlayerStoreId + 1, secondPlayerStoreId)
                .forEach(num -> pits.add(new House(num, finalPitStonesCount, PLAYER_2)));

        pits.add(new Store(secondPlayerStoreId, PLAYER_2));

        return modelMapper.map(gameStorageService.save(new Game(pits)), GameDto.class);
    }

    public GameDto getById(String gameId) throws ResourceNotFoundException {
        return modelMapper.map(gameStorageService.getById(gameId), GameDto.class);
    }

    public Status calculateStatus(Game game) {
        Map<Player, Integer> houseStonesCountMap = game.getPits().stream()
                .filter(pit -> !pit.getId().equals(STORE_ID_BY_PLAYER.get(PLAYER_1))
                        && !pit.getId().equals(STORE_ID_BY_PLAYER.get(PLAYER_2)))
                .collect(groupingBy(Pit::getOwner, summingInt(Pit::getStonesCount)));

        if (houseStonesCountMap.values().stream().allMatch(houseStonesCount -> houseStonesCount == 0)) {
            return Status.DRAW;
        } else if (houseStonesCountMap.values().stream().anyMatch(houseStonesCount -> houseStonesCount == 0)) {
            return Status.WIN;
        } else {
            return Status.ACTIVE;
        }
    }

    public Game finishGame(Game game) {
        Map<Player, Integer> totalStonesCountMap = game.getPits().stream()
                .collect(groupingBy(Pit::getOwner, summingInt(Pit::getStonesCount)));

        game.getPits().forEach(pit -> {
            if (!pit.getId().equals(STORE_ID_BY_PLAYER.get(PLAYER_1))
                    && !pit.getId().equals(STORE_ID_BY_PLAYER.get(PLAYER_2))) {
                pit.clear();
            } else {
                pit.setStonesCount(totalStonesCountMap.get(pit.getOwner()));
            }
        });

        game.setWinner(
                totalStonesCountMap.get(PLAYER_1) > totalStonesCountMap.get(PLAYER_2)
                        ? PLAYER_1
                        : totalStonesCountMap.get(PLAYER_2) > totalStonesCountMap.get(PLAYER_1) ? PLAYER_2 : null);

        return game;
    }
}
