package userFeatures;

import userFeatures.commands.*;
import userFeatures.commands.initial.*;
import userFeatures.commands.uci.Debug;
import userFeatures.commands.uci.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandHandler {
    private final Map<String, Command> initialCommands;
    private final Map<String, Command> uciCommands;
    private Map<String, Command> currentCommands;


    private final EngineState engineState;

    /**
    * Initializes lists of commands
    */
    public CommandHandler() {

        engineState = new EngineState();

        uciCommands = new HashMap<String, Command>();
        uciCommands.put("debug", new Debug(engineState));
        uciCommands.put("go", new Go(engineState));
        uciCommands.put("isready", new IsReady(engineState));
        uciCommands.put("ponderhit", new PonderHit(engineState));
        uciCommands.put("quit", new Quit(engineState));
        uciCommands.put("register", new Register(engineState));
        uciCommands.put("setoption", new SetOption(engineState));
        uciCommands.put("setposition", new SetPosition(engineState));
        uciCommands.put("stop", new Stop(engineState));
        uciCommands.put("ucinewgame", new UCINewGame(engineState));

        initialCommands = new HashMap<String, Command>();
        initialCommands.put("sayhello", new SayHello());
        initialCommands.put("uci", new UCIMode(engineState, this));

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
