package main.java.userFeatures.commands.uci;

import main.java.engine.search.Ponder;
import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

public class Stop implements Command {
    public ChessEngine chessEngine;

    public Stop(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        if (Ponder.isPondering()) {
            Ponder.stopPondering();
        }
    }
}
