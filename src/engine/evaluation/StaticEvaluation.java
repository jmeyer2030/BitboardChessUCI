package engine.evaluation;

import board.MoveEncoding;
import board.MoveType;
import board.Position;
import nnue.NNUE;

import static engine.evaluation.KingSafety.kingSafetyEvaluation;

public class StaticEvaluation {

    public static int evaluatePosition(Position position) {
        if (InsufficientMaterial.insufficientMaterial(position)) {
            return 0;
        } else if (MopUp.shouldUse(position)) {
            return MopUp.eval(position);
        }

        return position.nnue.computeOutput();
    }


    /**
     * Returns the evaluation from the perspective of the active player
     *
     * @param position position
     * @return evaluation from active player's perspective
     */
    public static int negamaxEvaluatePosition(Position position) {
        return position.nnue.computeOutput();
        /*
        if (position.activePlayer == 1)
            return -evaluatePosition(position);
        return evaluatePosition(position);
        */
    }
}
