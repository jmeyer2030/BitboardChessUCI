package testing.testMoveGeneration;
import board.*;
import moveGeneration.MoveGenerator;

public class TestMakeUnmake {
    public static void main(String[] args) {
        new MoveGenerator();
        TestPositions.initializePositions();
        Position staticCopy = new Position(TestPositions.position1);
        Position copy = new Position(TestPositions.position1);

        System.out.println(MoveGenerator.generateStrictlyLegal(staticCopy).size());
        //System.out.println(MoveGenerator.kingInCheck(copy, Color.BLACK));
        FEN fenP = new FEN("rnbqk1nr/pppp1ppp/8/2b1p3/4P3/3B1N2/PPPP1PPP/RNBQK2R b KQkq - 3 3");
        Position pFen = new Position(fenP);
        //Move move = new Move(28, 35, MoveType.CAPTURE, null, PieceType.PAWN, copy.rule50, copy.castleRights, PieceType.PAWN, copy.enPassant);
        //copy.makeMove(move);
        //System.out.println(MoveGenerator.generateStrictlyLegal(copy).size());
        System.out.println(MoveGenerator.generateStrictlyLegal(pFen).size());
        System.out.println("Position 1 Start");
        pFen.printBoard();
        System.out.println("Position 1 make/unmake");
        copy.printBoard();
        System.out.println(copy.equals(pFen));
    }

}
