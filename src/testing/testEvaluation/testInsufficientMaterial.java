package testing.testEvaluation;

import board.FEN;
import board.Position;
import engine.evaluation.InsufficientMaterial;
import engine.evaluation.StaticEvaluation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testInsufficientMaterial {
    @Test
    public void isNotSufficientTest() {
        FEN fen = new FEN("8/3k4/8/2b5/8/6K1/8/8 w - - 0 1");
        Position position = new Position(fen);
        int evaluation = StaticEvaluation.evaluatePosition(position);

        assertEquals(0, evaluation);
    }

    @Test
    public void isBBSufficientTest() {
        FEN fen = new FEN("8/3k4/8/2bb4/8/8/4K3/8 w - - 0 1");
        Position position = new Position(fen);

        boolean isInsufficient = InsufficientMaterial.insufficientMaterial(position);

        assertEquals(false, isInsufficient);
    }

}
