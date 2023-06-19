package com.mankalagameapi.dto;

import com.mankalagameapi.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PitDto {
    private Integer id;
    private Integer stonesCount;
    private Player owner;
}
