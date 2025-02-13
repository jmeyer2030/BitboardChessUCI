package userFeatures.commands.uci;

import board.FEN;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import zobrist.ThreeFoldTable;

import java.util.logging.Level;

public class SetPosition implements Command {
    public ChessEngine chessEngine;

    public SetPosition(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        if (chessEngine.positionState == null) {
            System.out.println("Use the UCINewGame command to start a new game before setting a position.");
            return;
        }

        // First parse the position component
        Position position = null;
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
        chessEngine.positionState.position = position;
        chessEngine.positionState.threeFoldTable = new ThreeFoldTable();

        // Parse moves component
        boolean foundMoves = false;
        for (String argument : arguments) {
            if ("moves".equals(argument)) {
                foundMoves = true;
            } else if (foundMoves) {
                try {
                    int move = MoveGenerator.getMoveFromLAN(argument, position, chessEngine.positionState.moveBuffer);
                    chessEngine.positionState.applyMove(move);
                } catch (IllegalArgumentException e) {
                    System.out.println("An error has occurred parsing move: " + argument);
                    return;
                }
            }
        }

        chessEngine.logger.log(Level.INFO, "Position set up:\n" + chessEngine.positionState.position.getDisplayBoard());
    }
}
