package testing.testZobrist;

import board.Position;
import moveGeneration.MoveGenerator;
import testing.testMoveGeneration.Testing;
import zobrist.HashTables;
import zobrist.Hashing;

import java.util.Arrays;

public class TestRandomQuality {
    public static void main(String[] args) {
        new MoveGenerator();

        findZobristSeed();
    }


    /**
    * finds a zobrist seed that sets positions uniquely enough to get correct perft 6 and 7 results.
    */
    public static void findZobristSeed() {
        long seed = 0;
        Position position = new Position();
        boolean worksForPerft = false;
        while (!worksForPerft) {
            Hashing.seed = ++seed;
            Hashing.initializeRandomNumbers();
            Arrays.fill(HashTables.perftTable, null);

            long perft6 = Testing.ttPerftRecursion(6, position);
            if (perft6 == 119_060_324L) {
                System.out.println("Seed: " + seed + " passed perft 6");
            } else {
                System.out.println("Seed: " + seed + " failed perft 6");
                continue;
            }

            long perft7 = Testing.ttPerftRecursion(7, position);
            if (perft7 == 3_195_901_860L) {
                System.out.println("Seed: " + seed + " passed perft 7");
                worksForPerft = true;
            } else {
                System.out.println("Seed: " + seed + " failed perft 7");
            }
        }
    }
}
