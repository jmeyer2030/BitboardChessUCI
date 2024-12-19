package testing;

import board.FEN;
import board.Position;

public class TestPositions {
    public static Position position1;
    public static Position position2;

    public static void initializePositions() {
        FEN p1FEN = new FEN("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2");
        position1 = new Position(p1FEN);

        FEN p2FEN = new FEN("rnbqkbnr/1pp1pppp/8/p2pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3");
        position2 = new Position(p2FEN);
    }

}
