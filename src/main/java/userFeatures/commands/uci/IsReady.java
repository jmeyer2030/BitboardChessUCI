package main.java.userFeatures.commands.uci;

import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

public class IsReady implements Command {
    public ChessEngine chessEngine;

    public IsReady(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        while (!chessEngine.isReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted while waiting for initialization.");
                return;
            }
        }
        System.out.println("readyok");
    }
}
