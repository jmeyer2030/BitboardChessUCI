package userFeatures.commands.uci;

import userFeatures.ChessEngine;
import userFeatures.commands.Command;

public class Quit implements Command {
    public ChessEngine chessEngine;

    public Quit(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
