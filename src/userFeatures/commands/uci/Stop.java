package userFeatures.commands.uci;

import engine.search.Ponder;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;

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
