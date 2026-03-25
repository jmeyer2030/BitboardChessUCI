package com.jmeyer2030.driftwood.config;

public class GlobalConstants {
    public static final int MAX_PLY = 256;

    /** Sentinel value for "no en passant square". Stored in Position.enPassant. */
    public static final int NO_EP = 0;

    /** Maximum number of half-moves (plies) a game + search can accumulate.
     *  Governs the capacity of the fixed-size make/unmake state stacks. */
    public static final int MAX_GAME_MOVES = 1024;

    private GlobalConstants() {}
}
