package userFeatures.commands.uci;

import board.FEN;
import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import zobrist.ThreeFoldTable;

import java.util.List;
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
                fenString.append(arguments[i] + " ");
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
        for (int i = 0; i < arguments.length; i++) {
            if ("moves".equals(arguments[i])) {
                foundMoves = true;
            } else if (foundMoves) {
                String commandMove = arguments[i];
                try {
                    List<Move> moves = MoveGenerator.generateStrictlyLegal(position);
                    for (Move generatedMove : moves) {
                        if (generatedMove.toLongAlgebraic().equals(commandMove)) {
                            chessEngine.positionState.applyMove(generatedMove);
                            continue;
                        }
                    }
                } catch (InvalidPositionException e) {
                    System.out.println("An error has occurred parsing move: " + commandMove);
                    return;
                }
            }
        }

        chessEngine.logger.log(Level.INFO, "Position set up:\n" + chessEngine.positionState.position.getDisplayBoard());
    }
}
