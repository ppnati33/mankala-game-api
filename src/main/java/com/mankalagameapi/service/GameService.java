package com.mankalagameapi.service;

import com.mankalagameapi.dto.GameDto;
import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.mankalagameapi.consts.GameConstants.DEFAULT_PIT_STONES_COUNT;
import static com.mankalagameapi.consts.GameConstants.STORE_ID_BY_PLAYER_MAP;
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
        AtomicInteger prevPlayerStoreId = new AtomicInteger();

        EnumSet.allOf(Player.class).forEach(player -> {
            int currentPlayerStoreId = STORE_ID_BY_PLAYER_MAP.get(player);

            IntStream.range(prevPlayerStoreId.incrementAndGet(), currentPlayerStoreId)
                    .forEach(num -> pits.add(new House(num, finalPitStonesCount, player)));

            pits.add(new Store(currentPlayerStoreId, player));

            prevPlayerStoreId.set(currentPlayerStoreId);
        });

        return modelMapper.map(gameStorageService.save(new Game(pits)), GameDto.class);
    }

    public GameDto getById(String gameId) throws ResourceNotFoundException {
        return modelMapper.map(gameStorageService.getById(gameId), GameDto.class);
    }

    public Status calculateStatus(Game game) {
        Map<Player, Integer> houseStonesCountMap = game.getPits().stream()
                .filter(pit -> !STORE_ID_BY_PLAYER_MAP.containsValue(pit.getId()))
                .collect(groupingBy(Pit::getOwner, summingInt(Pit::getStonesCount)));
        Map<Player, Integer> storeStonesCountMap = game.getPits().stream()
                .filter(pit -> STORE_ID_BY_PLAYER_MAP.containsValue(pit.getId()))
                .collect(groupingBy(Pit::getOwner, summingInt(Pit::getStonesCount)));

        boolean isOutOfStonesForAnyPlayer = houseStonesCountMap.values().stream()
                .anyMatch(houseStonesCount -> houseStonesCount == 0);
        boolean isEqualStonesCountInStores = new HashSet<>(storeStonesCountMap.values()).size() == 1;

        if (isOutOfStonesForAnyPlayer && isEqualStonesCountInStores) {
            return Status.DRAW;
        } else if (isOutOfStonesForAnyPlayer) {
            return Status.WIN;
        } else {
            return Status.ACTIVE;
        }
    }

    public Game finishGame(Game game) {
        Map<Player, Integer> totalStonesCountMap = game.getPits().stream()
                .collect(groupingBy(Pit::getOwner, summingInt(Pit::getStonesCount)));

        game.getPits().forEach(pit -> {
            if (!STORE_ID_BY_PLAYER_MAP.containsValue(pit.getId())) {
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
