package com.mankalagameapi.service;

import com.mankalagameapi.dto.GameDto;
import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.Game;
import com.mankalagameapi.model.Pit;
import com.mankalagameapi.model.Player;
import com.mankalagameapi.model.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import static com.mankalagameapi.consts.GameConstants.STORE_ID_BY_PLAYER_MAP;
import static com.mankalagameapi.model.Player.PLAYER_1;
import static com.mankalagameapi.model.Player.PLAYER_2;
import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class SowService {

    private final GameStorageService gameStorageService;
    private final GameService gameService;
    private final ModelMapper modelMapper;

    public GameDto sow(String gameId, Integer pitId) throws ResourceNotFoundException {
        Game game = gameStorageService.getById(gameId);

        Pit pitToTake = getPitById(game, pitId);

        game.setActivePlayer(game.getActivePlayer() == null ? pitToTake.getOwner() : game.getActivePlayer());

        checkPitToTake(game, pitToTake);

        int lastPitToSowId = sowInternal(game, pitToTake);
        collectOppositePitIfNotEmpty(game, lastPitToSowId);

        Status status = gameService.calculateStatus(game);
        game.setStatus(status);

        if (status == Status.WIN || status == Status.DRAW) {
            game = gameService.finishGame(game);
        } else {
            setNextTurn(game, lastPitToSowId);
        }

        return modelMapper.map(gameStorageService.save(game), GameDto.class);
    }

    private Pit getPitById(Game game, Integer pitId) {
        return game.getPits().stream()
                .filter(pit -> pitId.equals(pit.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                format("Pit with id=%s was not found for game with id=%s", pitId, game.getId())));
    }

    private int sowInternal(Game game, Pit pitToTake) {
        int currentStonesCount = pitToTake.getStonesCount();
        pitToTake.clear();

        Player activePlayer = game.getActivePlayer();

        int currentPitId = pitToTake.getId();

        while (currentStonesCount > 0) {
            currentPitId = currentPitId % game.getPits().size() + 1;
            Pit pitToSow = getPitById(game, currentPitId);
            if (pitToSow.isSowableForPlayer(activePlayer)) {
                pitToSow.addStones(1);
                currentStonesCount--;
            }
        }

        return currentPitId;
    }

    private void checkPitToTake(Game game, Pit pitToTakeFrom) {
        int pitToTakeFromId = pitToTakeFrom.getId();
        if (pitToTakeFrom.getOwner() != game.getActivePlayer()) {
            throw new IllegalArgumentException(format("Pit pitId=%s can't be used to sow", pitToTakeFromId));
        }

        if (pitToTakeFromId == STORE_ID_BY_PLAYER_MAP.get(game.getActivePlayer())) {
            throw new IllegalArgumentException(format("Pit pitId=%s can't be used to sow because it is a store", pitToTakeFromId));
        }

        if (pitToTakeFrom.isEmpty()) {
            throw new IllegalArgumentException(format("Empty pit pitId=%s can't be used to sow", pitToTakeFromId));
        }
    }

    private void collectOppositePitIfNotEmpty(Game game, int lastPitId) {
        Pit lastPit = getPitById(game, lastPitId);
        int storeId = STORE_ID_BY_PLAYER_MAP.get(game.getActivePlayer());

        if (lastPitId == storeId) {
            return;
        }

        Pit oppositePit = getPitById(game, game.getPits().size() - lastPit.getId());

        if (lastPit.getStonesCount() == 1 && !oppositePit.isEmpty()) {
            Pit currentPlayerStore = getPitById(game, storeId);
            currentPlayerStore.addStones(lastPit.getStonesCount() + oppositePit.getStonesCount());
            oppositePit.clear();
            lastPit.clear();
        }
    }

    private void setNextTurn(Game game, int lastPitToSowId) {
        if (lastPitToSowId != STORE_ID_BY_PLAYER_MAP.get(game.getActivePlayer())) {
            game.setActivePlayer((game.getActivePlayer() == PLAYER_1) ? PLAYER_2 : PLAYER_1);
        }
    }
}
