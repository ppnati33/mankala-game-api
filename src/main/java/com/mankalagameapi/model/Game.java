package com.mankalagameapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static com.mankalagameapi.model.Status.ACTIVE;

@NoArgsConstructor
@Document(collection = "games")
@Data
public class Game {

    private String id;
    private List<Pit> pits;
    private Player activePlayer;
    private Status status;
    private Player winner;

    public Game(List<Pit> pits) {
        this.status = ACTIVE;
        this.pits = pits;
    }
}
