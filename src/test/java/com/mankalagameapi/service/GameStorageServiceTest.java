package com.mankalagameapi.service;

import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.Game;
import com.mankalagameapi.model.House;
import com.mankalagameapi.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.mankalagameapi.model.Player.PLAYER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameStorageServiceTest {

    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameStorageService gameStorageService;

    @Test
    public void givenGameWhenSaveThenGameRepositoryCalledWithCorrectParams() {
        // given
        Game game = new Game(List.of(new House(1, 6, PLAYER_1)));

        // when
        gameStorageService.save(game);

        // then
        verify(gameRepository, times(1)).save(eq(game));
    }

    @Test
    public void givenGameIdWhenGetByIdThenGameRepositoryCalledAndCorrectResultReturned() throws ResourceNotFoundException {
        // given
        String gameId = "someGameId";
        Game game = new Game(List.of(new House(1, 6, PLAYER_1)));
        game.setId(gameId);
        when(gameRepository.findById(any())).thenReturn(Optional.of(game));

        // when
        Game result = gameStorageService.getById(gameId);

        // then
        verify(gameRepository, times(1)).findById(eq(gameId));
        assertEquals(result, game);
    }

    @Test
    public void givenGameIdWhenGetByIdAndNoDataReturnedFromGameRepositoryThenExceptionThrown() {
        // given
        String gameId = "someGameId";
        when(gameRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(ResourceNotFoundException.class, () -> gameStorageService.getById(gameId));
        verify(gameRepository, times(1)).findById(eq(gameId));
    }
}
