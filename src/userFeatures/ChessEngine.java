package userFeatures;

import board.PositionState;
import moveGeneration.MoveGenerator;
import system.Logging;

import java.util.logging.Logger;

public class ChessEngine {
    public Logger logger;

    // Enums
    public enum CommandMode { DEFAULT, UCI }

    // Engine Information:
    public final String ENGINE_NAME = "DriftWood 1.0";
    public final String AUTHOR = "Joshua Meyer";

    // Engine Settings:
    public CommandMode commandMode;
    public boolean debugActive;
    public int ttSize = 18;

    // Engine state
    public boolean isReady = false; // if engine is ready to search or add a position, this should be true.

    // Position state:
    public PositionState positionState;


    public ChessEngine() {
        this.logger = Logging.getLogger(ChessEngine.class);
    }

    public static void main(String[] args) {
        ChessEngine chessEngine = new ChessEngine();
        chessEngine.run();
    }


    private void run() {
        new Thread(this::initialize).start();
        CommandHandler commandHandler = new CommandHandler(this);

        commandHandler.inputLoop();
    }


    private void initialize() {
        this.isReady = true;
    }

    public void setCommandMode(CommandMode commandMode) {
        this.commandMode = commandMode;
    }

    public String getID() {
        return "id name " + ENGINE_NAME + "\nid author " + AUTHOR + "\n";
    }

}
