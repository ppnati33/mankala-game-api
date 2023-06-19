package com.mankalagameapi.controller;

import com.mankalagameapi.dto.GameDto;
import com.mankalagameapi.service.GameService;
import com.mankalagameapi.service.SowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/games", produces = APPLICATION_JSON_VALUE)
public class GameController {
    private final GameService gameService;
    private final SowService sowService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto createGame() {
        return gameService.createGame();
    }

    @GetMapping("{gameId}")
    public GameDto getGame(@PathVariable(value = "gameId")
                           @NotBlank
                           String gameId) throws Exception {
        return gameService.getById(gameId);
    }

    @PutMapping(value = "{gameId}/pits/{pitId}")
    public GameDto sowGame(@PathVariable(value = "gameId")
                           @NotBlank
                           String gameId,

                           @PathVariable(value = "pitId")
                           @NotNull
                           Integer pitId) throws Exception {
        return sowService.sow(gameId, pitId);
    }
}
