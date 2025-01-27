package userFeatures.commands.uci;

import userFeatures.ChessEngine;
import userFeatures.commands.Command;

public class PonderHit implements Command {
    public ChessEngine chessEngine;

    public PonderHit(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
