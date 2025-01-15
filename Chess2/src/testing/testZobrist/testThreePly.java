package testing.testZobrist;
import board.*;
import engine.Search;
import moveGeneration.MoveGenerator;
import org.junit.jupiter.api.*;
import zobrist.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testThreePly {
    /*public static void main(String[] args) {
        Position position = new Position();

        long zobristHash = Hashing.computeZobrist(position);

        HashTables.incrementThreeFold(zobristHash);
        HashTables.incrementThreeFold(zobristHash);
        HashTables.incrementThreeFold(zobristHash);


        System.out.println(HashTables.repetitionsExceeded(zobristHash));

        HashTables.decrementThreeFold(zobristHash);

        System.out.println(HashTables.repetitionsExceeded(zobristHash));

    }*/

    @BeforeAll
    public static void initializeComponents() {
        new MoveGenerator();
        Hashing.initializeRandomNumbers();
    }

    @Test
    public void testDrawnPosition() {
        FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        Position position = new Position(fen);

        long count = 0;
        for (HashTables.ThreeFoldElement element : HashTables.threeFoldTable) {
            if (element != null && element.numRepetitions != 0)
                count++;
        }

        assertEquals(0, count);


        long zobristHash = Hashing.computeZobrist(position);

        HashTables.incrementThreeFold(zobristHash);

        Search.MoveValue mv = Search.iterativeDeepening(position, 5_000);


        //assertEquals(0, mv.value);

        count = 0;
        for (HashTables.ThreeFoldElement element : HashTables.threeFoldTable) {
            if (element != null && element.numRepetitions != 0)
                count++;
        }

        assertEquals(1, count);
    }

    @Test
    public void testIncrement() {

    }
}
