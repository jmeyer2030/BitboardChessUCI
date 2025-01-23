package testing.testEngine;

import board.FEN;
import board.Position;
import engine.StaticEvaluation;
import moveGeneration.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testStaticEvaluation {

    @BeforeAll
    public static void initAll() {
        new MoveGenerator();
    }

    @Test
    public void testSymmetry1() {
        FEN positionFen1 = new FEN("rnbqkbnr/pppp1ppp/8/4p3/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        FEN positionFen2 = new FEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");

        assertSymmetry(positionFen1, positionFen2);
    }

    public static void assertSymmetry(FEN positionFen1, FEN positionFen2) {
        Position position1 = new Position(positionFen1);
        Position position2 = new Position(positionFen2);

        int evaluation1 = StaticEvaluation.evaluatePosition(position1);
        int evaluation2 = StaticEvaluation.evaluatePosition(position2);

        assertEquals(evaluation1, -evaluation2);
    }
}
