package testing.testMoveGeneration;

import moveGeneration.MoveGenerator2;
import zobrist.Hashing;

import static testing.testMoveGeneration.testPERFT.perftStartingPosition;

public class testPerftMain {
    public static void main(String[] args) {
        MoveGenerator2.initializeAll();
        Hashing.initializeRandomNumbers();
        int depth = 6;
        long result = perftStartingPosition(depth, false);
    }
}
