package moveGeneration;

import Search.StaticEvaluation;
import board.*;

public class testPERFT {
    public static void main(String[] args) {
        new MoveGenerator();

        Position position = new Position();
        /*
        FEN fenP = new FEN("rnbqk2r/pppp1ppp/7n/2b1p3/4P3/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        position = new Position(fenP);
        position.makeMove(new Move(4, 6, MoveType.CASTLE, null, null, position.rule50, position.castleRights, PieceType.KING, position.enPassant));
        position.printBoard();
        FEN fenP2 = new FEN("rnbqk2r/pppp1ppp/7n/2b1p3/4P3/3B1N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4");
        Position position1 = new Position(fenP2);
        position1.printBoard();
        */
        long start = System.currentTimeMillis();
        int depth = 6;

        System.out.println(StaticEvaluation.evaluatePosition(position));
        Testing.perft(depth, position);
        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
    }
}
