package testing.testMoveGeneration;

import board.FEN;
import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;

import java.util.List;

public class getMagicNumbers {
    public static void main(String[] args) {
        new MoveGenerator();
        Hashing.initializeRandomNumbers();
        FEN fen = new FEN("r1bqk2r/5ppp/p1n1pn2/1pb5/8/P2BPN2/1P2QPPP/RNB2RK1 b kq - 1 10");
        Position position = new Position(fen);

        System.out.println(position.getDisplayBoard());

        List<Move> moveList = null;

        try {
            moveList = MoveGenerator.generateStrictlyLegal(position);
        } catch (InvalidPositionException e) {
            throw new RuntimeException(e);
        }

        for (Move move : moveList) {
            System.out.println(move.toLongAlgebraic());
        }
    }
}
