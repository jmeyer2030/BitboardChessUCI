package userFeatures;

import board.PositionState;
import system.Logging;

import java.util.logging.Logger;

/**
* Main class for the chess main.java.engine
* Configures options, and starts accepting commands
*/
public class ChessEngine {
    public Logger logger;

    // Engine Information:
    public final String ENGINE_NAME = "DriftWood 5.0";
    public final String AUTHOR = "Joshua Meyer";

    // Engine Settings:
    public boolean debugActive;
    public int ttSize = 18;

    // Engine state
    public boolean isReady = true; // if ready to search or add a position, this should be true.

    // Position state:
    public PositionState positionState;

    public ChessEngine() {
        this.logger = Logging.getLogger(ChessEngine.class);
    }

    /**
    * Begins chess engine, usable from console
    */
    public static void main(String[] args) {
        ChessEngine chessEngine = new ChessEngine();
        chessEngine.run();
    }

    /**
    * Begins input loop
    */
    private void run() {
        CommandHandler commandHandler = new CommandHandler(this);

        commandHandler.inputLoop();
    }

    /**
    * Returns main.java.engine information
    */
    public String getID() {
        return "id name " + ENGINE_NAME + "\nid author " + AUTHOR + "\n";
    }

}
