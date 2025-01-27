package userFeatures.commands.uci;

import userFeatures.ChessEngine;
import userFeatures.commands.Command;

public class Register implements Command {
    public ChessEngine chessEngine;

    public Register(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        System.out.print("register later\n");
    }
}
