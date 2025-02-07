package testing.testMoveGeneration;

import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import zobrist.Hashing;
import moveGeneration.MoveShortcuts;
import board.FEN;

import static org.junit.jupiter.api.Assertions.fail;

public class MakeUnmakeTest {

    @BeforeAll
    public static void initAll() {
        //MoveGenerator.initializeAll();
        Hashing.initializeRandomNumbers();
    }


    @Test
    public void testMakeMove() {
        Position position = new Position();
        int move = MoveShortcuts.generatePawnDoublePush(12, 28, position);
        System.out.println(Integer.toBinaryString(move));

        position.makeMove(move);
        try {
            position.validPosition();
            System.out.println(position.getDisplayBoard());
        } catch (InvalidPositionException ipe) {
            fail();
        }

        position.unMakeMove(move);

        try {
            position.validPosition();
            System.out.println(position.getDisplayBoard());
        } catch (InvalidPositionException ipe) {
            fail();
        }

    }

    @Test
    public void testMakeMove2() {
        FEN fen = new FEN("rn1qkbnr/p1pppppp/bp6/8/8/P3P3/1PPPKPPP/RNBQ1BNR w kq - 3 4");
        Position position = new Position(fen);
        int[] moveBuffer = new int[256];
        int move = MoveShortcuts.generatePawnSinglePush(9, 17, position);
        move = 1049673;
        MoveEncoding.getDetails(move);
        position.makeMove(move);


        System.out.println(position.getDisplayBoard());


    }
}
