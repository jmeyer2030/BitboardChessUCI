package main.java.userFeatures.commands.uci;

import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

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
