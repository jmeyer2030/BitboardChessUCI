package testing.testZobrist;
import board.*;
import org.junit.jupiter.api.*;
import zobrist.*;

public class testThreeFoldTable {

    @BeforeAll
    public static void initializeComponents() {
    }

    @Test
    public void testDrawnPosition() {
        FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        Position position = new Position(fen);
        ThreeFoldTable threeFoldTable = new ThreeFoldTable();


        PositionState positionState = new PositionState(18);

        //Search.MoveValue mv = Search.iterativeDeepening(position, 5_000, searchState);

    }

    @Test
    public void testIncrement() {

    }
}
