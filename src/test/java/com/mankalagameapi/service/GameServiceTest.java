package com.mankalagameapi.service;

import com.mankalagameapi.dto.GameDto;
import com.mankalagameapi.dto.PitDto;
import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mankalagameapi.consts.GameConstants.DEFAULT_PIT_STONES_COUNT;
import static com.mankalagameapi.model.Player.PLAYER_1;
import static com.mankalagameapi.model.Player.PLAYER_2;
import static com.mankalagameapi.model.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameStorageService gameStorageService;
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private GameService gameService;

    @Test
    public void givenGameIdWhenGetByIdThenGameRepositoryCalledWithCorrectParams() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        House house = new House(1, 6, PLAYER_1);
        Game game = new Game(List.of(house));
        game.setId(gameId);
        when(gameStorageService.getById(any())).thenReturn(game);

        // when
        GameDto result = gameService.getById(gameId);

        // then
        verify(gameStorageService, times(1)).getById(eq(gameId));

        assertEquals(game.getId(), result.getId());
        assertEquals(game.getActivePlayer(), result.getActivePlayer());
        assertEquals(1, result.getPits().size());
        assertEquals(house.getId(), result.getPits().get(0).getId());
        assertEquals(house.getStonesCount(), result.getPits().get(0).getStonesCount());
        assertEquals(house.getOwner(), result.getPits().get(0).getOwner());
    }

    @Test
    public void givenPitStonesCountSpecifiedInParamsWhenCreateGameThenGameWithCorrectParamsCreated() {
        // given
        int pitStonesCount = 1;
        when(gameProperties.getPitStones()).thenReturn(pitStonesCount);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        Game expectedGameToSave = new Game();
        expectedGameToSave.setStatus(Status.ACTIVE);
        expectedGameToSave.setPits(List.of(
                new House(1, pitStonesCount, PLAYER_1),
                new House(2, pitStonesCount, PLAYER_1),
                new House(3, pitStonesCount, PLAYER_1),
                new House(4, pitStonesCount, PLAYER_1),
                new House(5, pitStonesCount, PLAYER_1),
                new House(6, pitStonesCount, PLAYER_1),
                new Store(7, PLAYER_1),
                new House(8, pitStonesCount, PLAYER_2),
                new House(9, pitStonesCount, PLAYER_2),
                new House(10, pitStonesCount, PLAYER_2),
                new House(11, pitStonesCount, PLAYER_2),
                new House(12, pitStonesCount, PLAYER_2),
                new House(13, pitStonesCount, PLAYER_2),
                new Store(14, PLAYER_2)
        ));

        // when
        gameService.createGame();

        // then
        verify(gameProperties, times(1)).getPitStones();
        verify(gameStorageService, times(1)).save(eq(expectedGameToSave));
    }

    @Test
    public void givenPitStonesCountNotSpecifiedInParamsWhenCreateGameThenGameWithDefaultParamsCreated() {
        // given
        when(gameProperties.getPitStones()).thenReturn(null);
        when(gameStorageService.save(any())).then(returnsFirstArg());

        Game expectedGameToSave = new Game();
        expectedGameToSave.setStatus(Status.ACTIVE);
        expectedGameToSave.setPits(List.of(
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

        // when
        gameService.createGame();

        // then
        verify(gameProperties, times(1)).getPitStones();
        verify(gameStorageService, times(1)).save(eq(expectedGameToSave));
    }

    @Test
    public void givenGameWhenCreateGameThenNewlyCreatedContainsSevenPitsOwnedByEachPlayer() {
        // given
        Game game = createGame();
        when(gameStorageService.save(any())).thenReturn(game);

        // when
        GameDto result = gameService.createGame();

        // then
        assertThat(result.getPits().size()).isEqualTo(game.getPits().size());
        assertThat(result.getPits().stream().filter(pit -> pit.getOwner().equals(PLAYER_1)).count()).isEqualTo(7);
        assertThat(result.getPits().stream().filter(pit -> pit.getOwner().equals(PLAYER_2)).count()).isEqualTo(7);
    }

    @Test
    public void givenGameWhenCreateGameThenNewlyCreatedGameContainsTwelveHousesWithSixStones() {
        // given
        Game game = createGame();
        when(gameStorageService.save(any())).thenReturn(game);

        // when
        GameDto result = gameService.createGame();

        // then
        assertThat(result.getPits().size()).isEqualTo(game.getPits().size());
        assertThat(result.getPits().stream().filter(pit -> pit.getStonesCount().equals(DEFAULT_PIT_STONES_COUNT)).count())
                .isEqualTo(12);
    }

    @Test
    public void givenGameWhenCreateGameThenNewlyCreatedContainsTwoStoresWithZeroStonesOneForEachPlayer() {
        // given
        Game game = createGame();
        when(gameStorageService.save(any())).thenReturn(game);

        // when
        GameDto result = gameService.createGame();

        // then
        assertThat(result.getPits().size()).isEqualTo(game.getPits().size());
        assertThat(result.getPits().stream().filter(pit -> pit.getStonesCount().equals(0)).count()).isEqualTo(2);
        assertThat(result.getPits().stream().filter(pit -> pit.getStonesCount().equals(0)).map(PitDto::getOwner).collect(Collectors.toList()))
                .containsExactly(PLAYER_1, PLAYER_2);
    }

    @Test
    public void givenGameWhenCreateGameThenNewlyCreatedContainsCorrectId() {
        // given
        Game game = createGame();
        game.setId("someGameId");
        when(gameStorageService.save(any())).thenReturn(game);

        // when
        GameDto result = gameService.createGame();

        // then
        assertThat(result.getId()).isEqualTo(game.getId());
    }

    @Test
    public void givenGameWhenCreateGameThenNewlyCreatedContainsNoActivePlayer() {
        // given
        Game game = createGame();
        when(gameStorageService.save(any())).thenReturn(game);

        // when
        GameDto result = gameService.createGame();

        // then
        assertThat(result.getActivePlayer()).isNull();
    }

    @Test
    public void givenGameWhenCalculateStatusThenDrawReturns() {
        // given
        Game game = createGame(List.of(0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 24));

        // when
        Status status = gameService.calculateStatus(game);

        // then
        assertThat(status).isEqualTo(DRAW);
    }

    @Test
    public void givenGameWhenCalculateStatusThenWinForFirstPlayerReturns() {
        // given
        Game game = createGame(List.of(0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 10));

        // when
        Status status = gameService.calculateStatus(game);

        // then
        assertThat(status).isEqualTo(WIN);
    }

    @Test
    public void givenGameWhenCalculateStatusThenWinForSecondPlayerReturns() {
        // given
        Game game = createGame(List.of(0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 20));

        // when
        Status status = gameService.calculateStatus(game);

        // then
        assertThat(status).isEqualTo(WIN);
    }

    @Test
    public void givenGameWhenCalculateStatusThenActiveReturns() {
        // given
        Game game = createGame(List.of(0, 1, 0, 0, 0, 0, 10, 0, 1, 0, 0, 0, 0, 20));

        // when
        Status status = gameService.calculateStatus(game);

        // then
        assertThat(status).isEqualTo(ACTIVE);
    }

    @ParameterizedTest
    @MethodSource("provideParametersForGameStatusCalculationTest")
    public void givenGameWhenFinishGameThenMoveStonesToStoresAndSetWinnerCorrectly(int player1StoreStonesCount,
                                                                                   int player2StoreStonesCount,
                                                                                   Player winner) {
        // given
        Game game = createGame(
                List.of(0, 1, 0, 0, 0, 0, player1StoreStonesCount, 0, 1, 0, 0, 0, 0, player2StoreStonesCount));

        // when
        Game res = gameService.finishGame(game);

        // then
        assertThat(res.getPits().get(6).getStonesCount()).isEqualTo(player1StoreStonesCount + 1);
        assertThat(res.getPits().get(13).getStonesCount()).isEqualTo(player2StoreStonesCount + 1);
        assertThat(
                res.getPits().stream()
                        .filter(pit -> pit.getId() != 7 && pit.getId() != 14)
                        .map(Pit::getStonesCount).collect(Collectors.toList())).containsOnly(0);
        assertThat(res.getWinner()).isEqualTo(winner);
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

    private static Stream<Arguments> provideParametersForGameStatusCalculationTest() {
        return Stream.of(
                Arguments.of(10, 20, PLAYER_2),
                Arguments.of(20, 10, PLAYER_1),
                Arguments.of(10, 10, null)
        );
    }
}
