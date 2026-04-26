package com.jmeyer2030.driftwood.userfeatures.commands.uci;

import com.jmeyer2030.driftwood.userfeatures.ChessEngine;
import com.jmeyer2030.driftwood.userfeatures.commands.Command;

public class Debug implements Command {
    public ChessEngine chessEngine;

    public Debug(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
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


        if ("on".equals(argument)) {
            chessEngine.debugActive = true;
        } else if (argument.equals("off")) {
            chessEngine.debugActive = false;
        } else {
            //Throw new IllegalArgumentException("Expected 'on' or 'off' but got: " + arguments[0]);
        }
    }
}
