package com.mankalagameapi.dto;

import com.mankalagameapi.model.Player;
import com.mankalagameapi.model.Status;
import lombok.Data;

import java.util.List;

@Data
public class GameDto {
    private String id;
    private List<PitDto> pits;
    private Player activePlayer;
    private Status status;
    private Player winner;
}
