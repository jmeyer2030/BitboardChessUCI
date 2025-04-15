package main.java.userFeatures.commands.uci;

import main.java.board.PositionState;
import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

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
