package testing.testMoveGeneration;

import static testing.testMoveGeneration.testPERFT.*;

public class testPerftMain {
    public static void main(String[] args) {
        int depth = 7;
        //perftFromFen("rnbqk2r/pBpp1ppp/5n2/4p3/4P3/b7/PPPP1PPP/RNBQK1NR b KQkq - 0 4", depth, false);
        long result = perftStartingPosition(depth, false);
    }
}
