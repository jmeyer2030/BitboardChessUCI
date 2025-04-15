package main.java.userFeatures.commands.initial;

import main.java.userFeatures.CommandHandler;
import main.java.userFeatures.ChessEngine;
import main.java.userFeatures.commands.Command;

public class UCIMode implements Command {

    public ChessEngine chessEngine;
    public CommandHandler handler;

    public UCIMode(ChessEngine chessEngine, CommandHandler handler) {
        this.chessEngine = chessEngine;
        this.handler = handler;
    }

    /**
    * Sets commandMode to UCI
    * Sets handler to accept UCI commands
    * Prints ID
    * Sends uciok, confirming that the main.java.engine is ready for uci
    */
    @Override
    public void execute(String[] arguments) {
        handler.acceptUCICommands();
        System.out.print(chessEngine.getID());
        System.out.print("uciok\n");
    }
}
