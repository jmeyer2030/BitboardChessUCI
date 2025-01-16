package testing.testEngine;

import board.Move;
import board.Position;
import engine.Search;
import moveGeneration.MoveGenerator;

import java.util.List;

import static engine.Search.*;

/*
* This class includes theoretically worse performing search functions that may be useful
* to keep for use in comparing to newer versions of them.
*/
public class LegacySearch {

    /**
     * negamax without transposition tables
     */
    public static Search.MoveValue negaMax(int alpha, int beta, int depthLeft, Position position) {
        if (depthLeft == 0) {
            return new Search.MoveValue(quiescenceSearch(alpha, beta, position), null);
            //return new MoveValue(StaticEvaluation.negamaxEvaluatePosition(position), null);
        }

        if (position.rule50 >= 50)
            return new Search.MoveValue(0, null);

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);

        if (children.size() == 0) { // Game ended by no moves to make
            if (position.whiteInCheck) // Black wins by checkmate
                return new Search.MoveValue(POS_INFINITY + 1000 - depthLeft, null); //prefer a higher depth (mate earlier)
            if (position.blackInCheck) // White wins by checkmate
                return new Search.MoveValue(POS_INFINITY - 1000 + depthLeft, null);// prefer a higher depth
            return new Search.MoveValue(0, null); // Stalemate
        }

        moveOrder(children, 0);

        Search.MoveValue bestMoveValue = new Search.MoveValue(NEG_INFINITY, null);

        for (Move move : children) {
            position.makeMove(move);

            int score = -negaMax(-beta, -alpha, depthLeft - 1, position).value;
            position.unMakeMove(move);

            // Update best move when score is better
            if (score > bestMoveValue.value) {
                bestMoveValue.value = score;
                bestMoveValue.bestMove = move;
            }

            // Update alpha when score is better
            if (score > alpha) {
                alpha = score;
            }
            // Prune when alpha >= beta
            if (alpha >= beta) {
                break;
            }
        }
        return bestMoveValue;
    }
}
