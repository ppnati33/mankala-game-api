package com.mankalagameapi.service;

import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static com.mankalagameapi.consts.GameConstants.DEFAULT_PIT_STONES_COUNT;
import static com.mankalagameapi.model.Player.PLAYER_1;
import static com.mankalagameapi.model.Player.PLAYER_2;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SowServiceTest {

    @Mock
    private GameStorageService gameStorageService;
    @Mock
    private GameService gameService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private SowService sowService;

    @Test
    public void givenGameIdAndPitIdWhenSowAndPitIsNotFoundForTheGameThenException() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 2;
        Game game = new Game(List.of(new House(1, DEFAULT_PIT_STONES_COUNT, PLAYER_1)));
        game.setId(gameId);
        when(gameStorageService.getById(any())).thenReturn(game);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> sowService.sow(gameId, pitId),
                format("Pit with id=%s was not found for game with id=%s", pitId, gameId));
    }

    @Test
    public void givenGameIdAndPitIdWhenSowAndPitOwnerIsNotActivePlayerThenException() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 1;
        Game game = new Game(List.of(new House(pitId, DEFAULT_PIT_STONES_COUNT, PLAYER_1)));
        game.setId(gameId);
        game.setActivePlayer(PLAYER_2);
        when(gameStorageService.getById(any())).thenReturn(game);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> sowService.sow(gameId, pitId),
                format("Pit pitId=%s can't be used to sow", pitId));

        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(0)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(0)).save(any());
    }

    @Test
    public void givenGameIdAndPitIdWhenSowAndThePitIsEmptyThenException() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 1;
        ;
        Game game = new Game(List.of(new House(pitId, 0, PLAYER_1)));
        game.setId(gameId);
        game.setActivePlayer(PLAYER_2);
        when(gameStorageService.getById(any())).thenReturn(game);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> sowService.sow(gameId, pitId),
                format("Empty pit pitId=%s can't be used to sow", pitId));

        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(0)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(0)).save(any());
    }

    @Test
    public void givenGameIdAndPitIdWhenSowThenActivePlayerChangedAfterSow() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 2;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getActivePlayer()).isEqualTo(PLAYER_2);
    }

    @Test
    public void givenGameIdAndPitIdWhenLastPitToSowIsStoreThenActivePlayerIsNotChangedAfterSow() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 1;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getActivePlayer()).isEqualTo(PLAYER_1);
    }

    @Test
    public void givenGameIdAndPitIdWhenSowToFirstPitThenPitStonesCountAreCorrect() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 1;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getPits().toString())
                .isEqualTo("[[1:0], [2:7], [3:7], [4:7], [5:7], [6:7], [7:1], [8:6], [9:6], [10:6], [11:6], [12:6], [13:6], [14:0]]");
    }

    @Test
    public void givenGameIdAndPitIdWhenSowToSecondPitThenPitStonesCountAreCorrect() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 2;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getPits().toString())
                .isEqualTo("[[1:6], [2:0], [3:7], [4:7], [5:7], [6:7], [7:1], [8:7], [9:6], [10:6], [11:6], [12:6], [13:6], [14:0]]");
    }

    @Test
    public void givenGameIdAndPitIdWhenSowThenOpponentStoreIsSkipped() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 6;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        game.getPits().get(5).setStonesCount(8);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getPits().toString())
                .isEqualTo("[[1:7], [2:6], [3:6], [4:6], [5:6], [6:0], [7:1], [8:7], [9:7], [10:7], [11:7], [12:7], [13:7], [14:0]]");
    }

    @Test
    public void givenGameIdAndPitIdWhenSowLastStoneToEmptyHouseThenOppositePitIsCollected() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 6;
        Game game = createGame();
        game.setId(gameId);
        game.setActivePlayer(PLAYER_1);
        game.getPits().get(0).setStonesCount(0);
        game.getPits().get(5).setStonesCount(8);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));
        verify(gameService, times(1)).calculateStatus(any());
        verify(gameService, times(0)).finishGame(any());
        verify(gameStorageService, times(1)).save(captor.capture());

        Game actual = captor.getValue();
        assertThat(actual.getPits().toString())
                .isEqualTo("[[1:0], [2:6], [3:6], [4:6], [5:6], [6:0], [7:9], [8:7], [9:7], [10:7], [11:7], [12:7], [13:0], [14:0]]");
    }

    @Test
    public void givenGameIdAndPitIdWhenSowLastStoneToStoreThenOppositePitIsNotCollected() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        int pitId = 13;
        Game game = createGame(List.of(0, 1, 0, 0, 1, 4, 28, 0, 0, 0, 0, 0, 1, 37));
        game.setId(gameId);
        game.setActivePlayer(PLAYER_2);
        when(gameStorageService.getById(any())).thenReturn(game);
        when(gameService.calculateStatus(any())).thenReturn(Status.ACTIVE);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        sowService.sow(gameId, pitId);

        // then
        verify(gameStorageService, times(1)).save(captor.capture());
        Game actual = captor.getValue();
        assertThat(actual.getPits().toString())
                .isEqualTo("[[1:0], [2:1], [3:0], [4:0], [5:1], [6:4], [7:28], [8:0], [9:0], [10:0], [11:0], [12:0], [13:0], [14:38]]");
    }

    private Game createGame() {
        return new Game(List.of(
                new House(1, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new House(2, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new House(3, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new House(4, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new House(5, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new House(6, DEFAULT_PIT_STONES_COUNT, PLAYER_1),
                new Store(7, PLAYER_1),
                new House(8, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new House(9, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new House(10, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new House(11, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new House(12, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new House(13, DEFAULT_PIT_STONES_COUNT, PLAYER_2),
                new Store(14, PLAYER_2)
        ));
    }

    private Game createGame(List<Integer> stonesCounts) {
        Pit store1 = new Store(7, PLAYER_1);
        store1.addStones(stonesCounts.get(6));
        Pit store2 = new Store(14, PLAYER_2);
        store2.addStones(stonesCounts.get(13));
        return new Game(List.of(
                new House(1, stonesCounts.get(0), PLAYER_1),
                new House(2, stonesCounts.get(1), PLAYER_1),
                new House(3, stonesCounts.get(2), PLAYER_1),
                new House(4, stonesCounts.get(3), PLAYER_1),
                new House(5, stonesCounts.get(4), PLAYER_1),
                new House(6, stonesCounts.get(5), PLAYER_1),
                store1,
                new House(8, stonesCounts.get(7), PLAYER_2),
                new House(9, stonesCounts.get(8), PLAYER_2),
                new House(10, stonesCounts.get(9), PLAYER_2),
                new House(11, stonesCounts.get(10), PLAYER_2),
                new House(12, stonesCounts.get(11), PLAYER_2),
                new House(13, stonesCounts.get(12), PLAYER_2),
                store2
        ));
    }
}
