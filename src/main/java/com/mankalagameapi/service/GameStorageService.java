package com.mankalagameapi.service;

import com.mankalagameapi.exception.ResourceNotFoundException;
import com.mankalagameapi.model.Game;
import com.mankalagameapi.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameStorageService {
    private final GameRepository gameRepository;

    public Game getById(String gameId) throws ResourceNotFoundException {
        return gameRepository
                .findById(gameId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Game with id=%s was not found!", gameId)));
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
