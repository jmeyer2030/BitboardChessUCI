package testing.testMoveGeneration;

import moveGeneration.AbsolutePins;
import moveGeneration.MoveGenerator2;
import zobrist.Hashing;

import static testing.testMoveGeneration.testPERFT.perftStartingPosition;

public class testPerftMain {
    public static void main(String[] args) {
        AbsolutePins.initializeAll();
        MoveGenerator2.initializeAll();
        Hashing.initializeRandomNumbers();
        int depth = 7;
        long result = perftStartingPosition(depth, false);
    }
}
