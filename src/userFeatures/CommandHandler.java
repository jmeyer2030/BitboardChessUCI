package userFeatures;

import userFeatures.commands.*;
import userFeatures.commands.initial.*;
import userFeatures.commands.uci.Debug;
import userFeatures.commands.uci.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/*
 UCI is designed to ignore bad commands, so don't worry about debug statements
*/
public class CommandHandler {
    private final Map<String, Command> initialCommands;
    private final Map<String, Command> uciCommands;
    private Map<String, Command> currentCommands;

    private final ChessEngine chessEngine;

    /**
    * Initializes lists of commands
    */
    @SuppressWarnings("SpellCheckingInspection")
    public CommandHandler(ChessEngine chessEngine) {

        this.chessEngine = chessEngine;

        uciCommands = new HashMap<String, Command>();
        uciCommands.put("debug", new Debug(chessEngine));
        uciCommands.put("go", new Go(chessEngine));
        uciCommands.put("isready", new IsReady(chessEngine));
        uciCommands.put("ponderhit", new PonderHit(chessEngine));
        uciCommands.put("quit", new Quit(chessEngine));
        uciCommands.put("register", new Register(chessEngine));
        uciCommands.put("setoption", new SetOption(chessEngine));
        uciCommands.put("setposition", new SetPosition(chessEngine));
        uciCommands.put("stop", new Stop(chessEngine));
        uciCommands.put("ucinewgame", new UCINewGame(chessEngine));

        initialCommands = new HashMap<String, Command>();
        initialCommands.put("sayhello", new SayHello());
        initialCommands.put("uci", new UCIMode(chessEngine, this));

        currentCommands = initialCommands;
    }


    /**
    * Loops infinitely reading from console input and executing these commands
    */
    public void inputLoop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Read input and separate by spaces
            String input = scanner.nextLine().toLowerCase();
            String[] parts = input.split("\\s+");


            String[] arguments = null;

            // Handle different sizes of inputs
            if (parts.length == 0 ) {
                System.out.println("Invalid command");
                continue;
            } else if (parts.length == 1) {
                arguments = new String[0];
            } else {
                arguments = Arrays.copyOfRange(parts, 1, parts.length);
            }

            executeCommand(parts[0], arguments);
        }
    }

    public void executeCommand(String command, String[] arguments) {
        Command action = currentCommands.get(command);
        if (action != null) {
            action.execute(arguments);
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    public void acceptUCICommands() {
        currentCommands = uciCommands;
    }
}
