package userFeatures.configuration;

public class GlobalConstants {
    public static final int MAX_PLY = 256;

    /** Maximum number of half-moves (plies) a game + search can accumulate.
     *  Governs the capacity of the fixed-size make/unmake state stacks. */
    public static final int MAX_GAME_MOVES = 1024;

    private GlobalConstants() {}
}
