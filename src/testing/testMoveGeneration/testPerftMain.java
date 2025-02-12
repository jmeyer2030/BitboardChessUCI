package testing.testMoveGeneration;

import moveGeneration.AbsolutePins;
import moveGeneration.MoveGenerator2;
import zobrist.Hashing;

import static testing.testMoveGeneration.testPERFT.*;

public class testPerftMain {
    public static void main(String[] args) {
        Hashing.initializeRandomNumbers();
        int depth = 7;
        //perftFromFen("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPPBPPq/RNBQK2R w KQkq - 0 4", 1, false);
        long result = perftStartingPosition(depth, false);
    }
}
