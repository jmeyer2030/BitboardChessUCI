package userFeatures.commands.uci;

import board.PositionState;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;

import java.util.logging.Level;

public class UCINewGame implements Command {
    public ChessEngine chessEngine;

    public UCINewGame(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        chessEngine.positionState = new PositionState(chessEngine.ttSize);
        chessEngine.logger.log(Level.INFO, "New game created successfully");
    }
}
