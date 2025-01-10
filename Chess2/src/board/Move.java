package board;
import testing.testMoveGeneration.Testing;

public class Move {
/*
* 0-5 : start
* 6-11 : end
* 12-14 : moveType
* 15-16 : promotionType
* 17-19 : moved piece
* 20-22 : captured piece
*
*
*
*
*/
//0-5 (start) 6-11 (end) 12-14 (moveType) 15-

/*
* Fields
*/
    //Move details:
    public final int start;
    public final int destination;
    public final MoveType moveType;
    public final PieceType promotionType; // Null for non-promotion moves
    public final PieceType captureType;
    public final PieceType movePiece;

    //Position details:
    public final int halfMoveCount;
    public final byte castleRights;
    public final int enPassant;

    //fields added in move generator:

    public boolean prevWhiteInCheck;
    public boolean prevBlackInCheck;
    public boolean resultWhiteInCheck;
    public boolean resultBlackInCheck;

/*
* Constructors
*/
    /**
    * General Constructor
    */
    public Move(int start, int destination, MoveType moveType, PieceType promotionType,
        PieceType captureType, int halfMoveCount, byte castleRights, PieceType movePiece, int enPassant) {
        this.start = start;
        this.destination = destination;
        this.moveType = moveType;
        this.promotionType = promotionType;
        this.captureType = captureType;
        this.halfMoveCount = halfMoveCount;
        this.castleRights = castleRights;
        this.movePiece = movePiece;
        this.enPassant = enPassant;

        //These are initialized so that the move can be made / unmade, but are updated in MoveGenerator
        prevWhiteInCheck = false;
        prevBlackInCheck = false;
        resultWhiteInCheck = false;
        resultBlackInCheck = false;
    }
/*
* Factory Methods
*/
    /**
    * Returns a new quiet move
    */
    public static Move quietMove(int start, int destination, Position position, PieceType movePiece) {
        return new Move(start, destination, MoveType.QUIET, null, null,
            position.rule50, position.castleRights, movePiece, position.enPassant);
    }

    /**
    * Returns a new capture move
    */
    public static Move captureMove(int start, int destination, Position position, PieceType movePiece) {
        PieceType captureType = position.getPieceType(destination);
        if (captureType == null) {
            System.out.println("CAPTURE WITH NO TARGET");
            position.printBoard();

            System.out.println(start + " -> " + destination);
            System.out.println(movePiece);

        }
        return new Move(start, destination, MoveType.CAPTURE, null, captureType,
            position.rule50, position.castleRights, movePiece, position.enPassant);
    }

    /**
     * Returns a new En Passant move
     */
    public static Move enPassantMove(int start, int destination, Position position) {
        return new Move(start, destination, MoveType.ENPASSANT, null, null,
            position.rule50, position.castleRights, PieceType.PAWN, position.enPassant);
    }

    /**
     * Returns a new promotion move
     */
    public static Move promotionMove(int start, int destination, Position position, PieceType promotionType) {
        PieceType captureType = position.getPieceType(destination);
        return new Move(start, destination, MoveType.PROMOTION, promotionType, captureType,
            position.rule50, position.castleRights, PieceType.PAWN, position.enPassant);
    }

    /**
     * Returns a new castle move
     */
    public static Move castleMove(int start, int destination, Position position) {
        return new Move(start, destination, MoveType.CASTLE, null, null,
            position.rule50, position.castleRights, PieceType.KING, position.enPassant);
    }

/**
* Helper Methods
*/
    /**
    * Returns a string version of the move
    * @return description of the move
    */
    @Override
    public String toString() {
        String moveDescription = "";
        moveDescription += Testing.notation(this.start) + " -> " + Testing.notation(this.destination);

        moveDescription += "Start: " + this.start + " \n";
        moveDescription += "Destination: " + this.destination + " \n";
        moveDescription += "MoveType: " + this.moveType + " \n";
        moveDescription += "PromotionType: " + this.promotionType + " \n";
        moveDescription += "CaptureType: " + this.captureType + " \n";
        moveDescription += "HalfMoveCount: " + this.halfMoveCount + " \n";
        moveDescription += "CastleRights: " + this.castleRights + " \n";

        return moveDescription;
    }

    public boolean equals(Move move) {
        boolean equal = true;
        equal &= this.start == move.start;
        equal &= this.destination == move.destination;
        equal &= this.moveType == move.moveType;
        equal &= this.promotionType == move.promotionType;
        equal &= this.captureType == move.captureType;
        equal &= this.halfMoveCount == move.halfMoveCount;
        equal &= this.movePiece == move.movePiece;

        return equal;
    }
}