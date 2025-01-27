package userFeatures.commands.uci;

import userFeatures.ChessEngine;
import userFeatures.commands.Command;

public class Stop implements Command {
    public ChessEngine chessEngine;

    public Stop(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
