package testing.testZobrist;
import board.*;
import engine.search.Search;
import engine.search.SearchState;
import moveGeneration.MoveGenerator;
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


        SearchState searchState = new SearchState(18);

        //Search.MoveValue mv = Search.iterativeDeepening(position, 5_000, searchState);

    }

    @Test
    public void testIncrement() {

    }
}
