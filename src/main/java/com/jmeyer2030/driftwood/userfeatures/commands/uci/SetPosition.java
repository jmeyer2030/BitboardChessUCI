package com.jmeyer2030.driftwood.userfeatures.commands.uci;

import com.jmeyer2030.driftwood.board.FEN;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.InvalidPositionException;
import com.jmeyer2030.driftwood.movegeneration.MoveGenerator;
import com.jmeyer2030.driftwood.userfeatures.ChessEngine;
import com.jmeyer2030.driftwood.userfeatures.commands.Command;
import com.jmeyer2030.driftwood.board.ThreeFoldTable;


public class SetPosition implements Command {
    public ChessEngine chessEngine;

    public SetPosition(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }


    /**
    * Sets up chessEngine's position as specified by the command.
    * Resets fields threefold and updates it with moves from position command.
    */
    @Override
    public void execute(String[] arguments) {
        if (chessEngine.sharedTables == null) {
            System.out.println("Use the UCINewGame command to start a new game before setting a position.");
            return;
        }

        // First parse the position component
        Position position;
        if ("startpos".equals(arguments[0])) {
            position = new Position();
        } else if ("fen".equals(arguments[0]) && arguments.length >= 7) {
            StringBuilder fenString = new StringBuilder();
            for (int i = 1; i < 7; i++) {
                fenString.append(arguments[i]).append(" ");
            }

            fenString.deleteCharAt(fenString.length() - 1);

            FEN fen = new FEN(fenString.toString());

            position = new Position(fen);
            try  {
                position.validPosition();
            } catch(InvalidPositionException ipe) {
                System.err.println("invalid fen");
                return;
            }
        } else {
            System.out.println("Invalid arguments");
            return;
        }

        // Reset threeply, add new position
        chessEngine.position = position;
        chessEngine.sharedTables.threeFoldTable = new ThreeFoldTable();

        // Parse moves component
        boolean foundMoves = false;
        for (String argument : arguments) {
            if ("moves".equals(argument)) {
                foundMoves = true;
            } else if (foundMoves) {
                try {
                    int move = MoveGenerator.getMoveFromLAN(argument, position, chessEngine.searchContext.moveBuffer);
                    chessEngine.sharedTables.applyMove(move, position);
                } catch (IllegalArgumentException e) {
                    System.out.println("An error has occurred parsing move: " + argument);
                    return;
                }
            }
        }

        if (chessEngine.debugActive) {
            System.err.println("info string Position set up:\n" + chessEngine.position.getDisplayBoard());
        }
    }
}
