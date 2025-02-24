package testing.testEvaluation;

import board.FEN;
import board.Position;
import engine.evaluation.MopUp;
import engine.evaluation.StaticEvaluation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMopUp {
    @Test
    public void testBasicFunctionality() {
        FEN fen = new FEN("8/3k4/8/2bb4/8/6K1/8/8 w - - 0 1");
        Position position = new Position(fen);

        int evaluation = MopUp.eval(position);
        System.out.println(evaluation);
        assertTrue(evaluation < -1000);
    }

    @Test
    public void testSymmetry() {
        FEN fen1 = new FEN("3k4/8/8/3bb3/8/8/8/4K3 w - - 0 1");
        Position position1 = new Position(fen1);

        FEN fen2 = new FEN("3k4/8/8/8/3BB3/8/8/4K3 w - - 0 1");
        Position position2 = new Position(fen2);

        int evaluation1 = MopUp.eval(position1);
        int evaluation2 = MopUp.eval(position2);
        System.out.println(evaluation1);
        System.out.println(evaluation2);
        assertEquals(evaluation1, -evaluation2);
    }

    @Test
    public void testSymmetryMainEval() {
        FEN fen1 = new FEN("3k4/8/8/3bb3/8/8/8/4K3 w - - 0 1");
        Position position1 = new Position(fen1);

        FEN fen2 = new FEN("3k4/8/8/8/3BB3/8/8/4K3 w - - 0 1");
        Position position2 = new Position(fen2);

        int evaluation1 = StaticEvaluation.evaluatePosition(position1);
        int evaluation2 = StaticEvaluation.evaluatePosition(position2);
        System.out.println(evaluation1);
        System.out.println(evaluation2);
        assertEquals(evaluation1, -evaluation2);
    }

    @Test
    public void testCloserDistanceBetter() {
        FEN fen1 = new FEN("3k4/8/8/3bb3/8/8/8/4K3 w - - 0 1");
        Position position1 = new Position(fen1);

        FEN fen2 = new FEN("3k4/8/8/8/3BB3/8/8/4K3 w - - 0 1");
        Position position2 = new Position(fen2);

        int evaluation1 = StaticEvaluation.evaluatePosition(position1);
        int evaluation2 = StaticEvaluation.evaluatePosition(position2);
        System.out.println(evaluation1);
        System.out.println(evaluation2);
        assertEquals(evaluation1, -evaluation2);
    }
}

/*
FEN Positions for testing:
KRk:
    8/8/8/8/8/8/5k2/6RK w - - 0 1
    8/8/8/5k2/8/8/8/6RK w - - 0 1
    R7/8/8/8/3k4/8/8/7K w - - 0 1
    5k2/8/8/8/8/8/8/6RK w - - 0 1
Kkr:
    7r/8/8/8/8/8/8/k6K w - - 0 1
    8/8/8/8/8/8/1r6/k6K w - - 0 1
    8/8/4r3/4k3/8/8/8/4K3 w - - 0 1





*/
