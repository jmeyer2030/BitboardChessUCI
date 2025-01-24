package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Debug implements Command {
    public EngineState engineState;

    public Debug(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length != 1) {
            // Throw new IllegalArgumentException("Expected one argument but got " + arguments.length + " arguments");
        }

        String argument = arguments[0];
        if (argument == null) {
            // Throw new IllegalArgumentException("Argument must not be null");
        }


        if (argument.equals("on")) {
            engineState.debugActive = true;
        } else if (argument.equals("off")) {
            engineState.debugActive = false;
        } else {
            //Throw new IllegalArgumentException("Expected 'on' or 'off' but got: " + arguments[0]);
        }
    }
}
