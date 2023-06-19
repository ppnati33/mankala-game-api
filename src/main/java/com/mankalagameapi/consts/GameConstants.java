package com.mankalagameapi.consts;

import com.mankalagameapi.model.Player;

import java.util.Map;

public class GameConstants {
    public static final int DEFAULT_PIT_STONES_COUNT = 6;
    public static final Map<Player, Integer> STORE_ID_BY_PLAYER_MAP = Map.of(
            Player.PLAYER_1, 7,
            Player.PLAYER_2, 14
    );
}
