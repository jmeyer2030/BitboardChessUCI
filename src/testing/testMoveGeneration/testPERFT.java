package testing.testMoveGeneration;

import board.*;
import moveGeneration.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testing.Perft;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Non-tt perft should work. It seems that there are collisions after depth 6 in the perft tt
This causes results to be slightly off

*/
public class testPERFT {

    public static long[] perftResults = {1L, 20L, 400L, 8_902L, 197_281L, 4_865_609L, 119_060_324L, 3_195_901_860L};
    // f2f4
    public static String fen = "rnbqkbnr/pppp1ppp/8/8/5p2/8/PPPPPKPP/RNBQ1BNR w kq - 0 3";
    @BeforeAll
    public static void beforeAll() {

    }

    @Test
    public void testPerftDepth0NoTT() {
        int depth = 0;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth1NoTT() {
        int depth = 1;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth2NoTT() {
        int depth = 2;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth3NoTT() {
        int depth = 3;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth4NoTT() {
        int depth = 4;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth5NoTT() {
        int depth = 5;
        //long result = perftFromFen(fen, depth, false);
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth6NoTT() {
        int depth = 7;
        long result = perftStartingPosition(depth, false);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth0TT() {
        int depth = 0;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth1TT() {
        int depth = 1;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth2TT() {
        int depth = 2;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth3TT() {
        int depth = 3;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth4TT() {
        int depth = 4;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth5TT() {
        int depth = 5;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth6TT() {
        int depth = 6;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    @Test
    public void testPerftDepth7TT() {
        int depth = 7;
        long result = perftStartingPosition(depth, true);
        assertEquals(perftResults[depth], result);
    }

    public static long perftStartingPosition(int depth, boolean useTTs) {
        Position position = new Position();
        long start = System.currentTimeMillis();

        long result;
        if (useTTs) {
            result = 0;
            //result =  Perft.ttPerft(depth, position);
        } else {
            result = Perft.perft(depth, position);
        }
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
        return result;
    }

    public static long perftFromFen(String fen, int depth, boolean useTTs) {
        FEN fenP = new FEN(fen);
        Position position = new Position(fenP);

        long start = System.currentTimeMillis();

        long result;
        if (useTTs) {
            //result =  Perft.ttPerft(depth, position);
            result = 0;
        } else {
            result = Perft.perft(depth, position);
        }
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
        return result;
    }
}
