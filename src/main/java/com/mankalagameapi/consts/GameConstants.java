package com.mankalagameapi.consts;

import com.mankalagameapi.model.Player;

import java.util.Map;

public class GameConstants {
    public static final int DEFAULT_PIT_STONES_COUNT = 6;
    public static final int LAST_STORE_ID = 14;
    public static final Map<Player, Integer> STORE_ID_BY_PLAYER = Map.of(
            Player.PLAYER_1, 7,
            Player.PLAYER_2, LAST_STORE_ID
    );
}
