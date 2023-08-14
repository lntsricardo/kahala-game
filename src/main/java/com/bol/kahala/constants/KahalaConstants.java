package com.bol.kahala.constants;

public interface KahalaConstants {

    /* ERROR MESSAGE */
    static final String ERROR_MESSAGE_PIT_ID_INVALID = "Pit id is invalid.";
    static final String ERROR_MESSAGE_PIT_ID_NULL = "Pit id can't be null";
    static final String ERROR_MESSAGE_PIT_NOT_FOUND = "Pit not found.";
    static final String ERROR_MESSAGE_PLAYER_NAME_EMPTY = "Player's name can't be null or empty.";
    static final String ERROR_MESSAGE_PLAYERS_TURN = "Wrong player tried to move stones!";

    /* PITS */
    static final int BIG_PIT_DEFAULT_VALUE = 0;
    static final int BIG_PIT_ORDER = 7;
    static final int SMALL_PIT_DEFAULT_VALUE = 6;
    static final int SMALL_PIT_MAX_ORDER = 6;
}
