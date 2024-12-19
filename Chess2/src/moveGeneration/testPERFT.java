package moveGeneration;

import board.*;

public class testPERFT {
    public static void main(String[] args) {
        new MoveGenerator();

        Position position = new Position();
        //position.makeMove(new Move());
        FEN fenP = new FEN("rnbqk2r/pppp1ppp/7n/2b1p3/4P3/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        position = new Position(fenP);
        position.makeMove(new Move(4, 6, MoveType.CASTLE, null, null, position.rule50, position.castleRights, PieceType.KING, position.enPassant));
        position.printBoard();
        long start = System.currentTimeMillis();
        int depth = 1;
        Testing.perft(depth, position);
        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
    }
}
