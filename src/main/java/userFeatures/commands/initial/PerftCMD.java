package userFeatures.commands.initial;

import board.Position;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import userFeatures.perft.Perft;

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

        if (chessEngine.positionState == null) {
            Perft.perft(depth, new Position());
        } else {
            Perft.perft(depth, chessEngine.positionState.position);
        }

    }
}
