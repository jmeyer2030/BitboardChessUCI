package test.java.testEngine;

import main.java.board.FEN;
import main.java.board.MoveEncoding;
import main.java.board.Position;
import main.java.board.PositionState;
import main.java.engine.search.Search;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testMateSearch {

    /**
     * M1 (1 ply)
     */
    @Test
    public void testMatingPosition1() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/k1K5/8/4Q3/8 w - - 0 1");
        Position position = new Position(fen);

        int score = Search.iterativeDeepening(position, 10000, positionState).value;

        System.out.println(score);

        assertEquals(89_999_999, score);
    }

    /**
     * M2 (3 ply)
     */
    @Test
    public void testMatingPosition2() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/2K5/k7/3Q4/8 w - - 0 1");
        Position position = new Position(fen);

        int score = Search.iterativeDeepening(position, 10000, positionState).value;

        System.out.println(score);

        assertEquals(89_999_997, score);
    }

    /**
     * M3 (5 ply)
     */
    @Test
    public void testMatingPosition3() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/8/k2K1Q2/8/8 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 10000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_995, mv.value);
    }

    /**
     * M4 (7 ply)
     */
    @Test
    public void testMatingPosition4() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/2k5/8/2K5/3Q4/8 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 10000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_993, mv.value);
    }

    /**
     * M5 (9 ply)
     */
    @Test
    public void testMatingPosition5() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/8/k4KQ1/8/8 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 1_000_000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_991, mv.value);
    }


    /**
     * M7 (13 ply)
     */
    @Test
    public void testMatingPosition6() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/4k3/8/4K3/4Q3 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 1_000_000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_987, mv.value);
    }

    /**
     * M12 (23 ply)
     */
    //@Test
    public void testMatingPosition7() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/7k/K7/R7/8/8/8 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 1_000_000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_977, mv.value);
    }

    /**
     * 1 Q
     * M8 (15 ply)
     */
    @Test
    public void testMatingPosition8() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("8/8/8/8/4k3/8/1K6/Q7 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 1_000_000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_985, mv.value);
    }

    /**
     * 2 bishop
     * M10 (19 ply)
     */
    @Test
    public void testMatingPosition9() {
        PositionState positionState = new PositionState(18);
        FEN fen = new FEN("4k3/8/8/3BB3/4K3/8/8/8 w - - 0 1");
        Position position = new Position(fen);

        Search.MoveValue mv = Search.iterativeDeepening(position, 1_000_000, positionState);

        System.out.println(mv.value);
        System.out.println(MoveEncoding.getLAN(mv.bestMove));

        assertEquals(89_999_981, mv.value);
    }
}
