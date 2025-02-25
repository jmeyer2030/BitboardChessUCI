package engine.evaluation;

import board.Position;


public class StaticEvaluation {

    public static int evaluatePosition(Position position) {
        if (InsufficientMaterial.insufficientMaterial(position)) {
            return 0;
        } else if (MopUp.shouldUse(position)) {
            return MopUp.eval(position);
        }

        return position.nnue.computeOutput(position.activePlayer);
    }


    /**
     * Returns the evaluation from the perspective of the active player
     *
     * @param position position
     * @return evaluation from active player's perspective
     */
    public static int negamaxEvaluatePosition(Position position) {
        return position.nnue.computeOutput(position.activePlayer);
        /*
        if (position.activePlayer == 1)
            return -evaluatePosition(position);
        return evaluatePosition(position);
        */
    }
}
