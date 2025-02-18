package engine.evaluation;

import board.MoveEncoding;
import board.MoveType;
import board.Position;

import static engine.evaluation.KingSafety.kingSafetyEvaluation;

public class StaticEvaluation {

    public static int evaluatePosition(Position position) {
        if (InsufficientMaterial.insufficientMaterial(position)) {
            return 0;
        }

        int mgScore = position.mgScore + kingSafetyEvaluation(position); // PST + kingSafety
        int egScore = position.egScore; // PST
        int gamePhase = position.gamePhase;


        int mgPhase = gamePhase;
        if (mgPhase > 24) {
            mgPhase = 24;
        }
        int egPhase = 24 - mgPhase;


        int evaluation = (mgScore * mgPhase + egScore * egPhase) / 24;

        //evaluation += Mobility.mobility(position, mgPhase, egPhase);

        return evaluation;
    }


    /**
     * Returns the evaluation from the perspective of the active player
     *
     * @param position position
     * @return evaluation from active player's perspective
     */
    public static int negamaxEvaluatePosition(Position position) {
        if (position.activePlayer == 1)
            return -evaluatePosition(position);
        return evaluatePosition(position);
    }
}
