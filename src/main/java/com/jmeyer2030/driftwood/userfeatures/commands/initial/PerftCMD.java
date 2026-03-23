package com.jmeyer2030.driftwood.userfeatures.commands.initial;

import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.userfeatures.ChessEngine;
import com.jmeyer2030.driftwood.userfeatures.commands.Command;
import com.jmeyer2030.driftwood.userfeatures.perft.Perft;

public class PerftCMD implements Command {
    public ChessEngine chessEngine;

    public PerftCMD(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length == 0) {
            System.out.println("Perft requires depth argument");
        }

        int depth = 1;
        try {
             depth = Integer.parseInt(arguments[0]);
        } catch(NumberFormatException e) {
            System.out.println("Perft requires an integer argument");
            return;
        }

        if (depth < 1) {
            System.out.println("Perft argument must be greater than zero.");
            return;
        }

        if (chessEngine.position == null) {
            Perft.perft(depth, new Position());
        } else {
            Perft.perft(depth, chessEngine.position);
        }

    }
}
