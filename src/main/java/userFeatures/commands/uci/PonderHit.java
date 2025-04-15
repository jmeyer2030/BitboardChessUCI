package main.java.userFeatures.commands.uci;

import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

public class PonderHit implements Command {
    public ChessEngine chessEngine;

    public PonderHit(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
