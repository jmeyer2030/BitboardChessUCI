package main.java.userFeatures.commands.uci;

import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

public class Quit implements Command {
    public ChessEngine chessEngine;

    public Quit(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        System.out.println("Exiting...");
        System.exit(0);
    }
}
