package board;

public class Move {
    // Enum for move types
    public enum MoveType {
        QUIET,
        CAPTURE,
        ENPASSANT,
        PROMOTION,
        CHECK,
        CASTLE
    }

    // Enum for piece types (used for promotions and other specific moves)
    public enum PieceType {
        ROOK,
        BISHOP,
        KNIGHT,
        QUEEN
    }

    // Fields
    public final MoveType moveType;
    public final PieceType pieceType; // Null for non-promotion moves
    public final int start;
    public final int destination;

    // Constructor
    public Move(MoveType moveType, PieceType pieceType, int start, int destination) {
        this.moveType = moveType;
        this.pieceType = pieceType; // Can be null for moves without promotions
        this.start = start;
        this.destination = destination;
    }

    // Convenience Constructor (for non-promotion moves)
    public Move(MoveType moveType, int start, int destination) {
        this(moveType, null, start, destination);
    }

    // Print the move in a readable format
    public void printMove() {
        String promotion = (pieceType != null) ? " Promotion to: " + pieceType : "";
        System.out.println("Move start: " + this.start 
            + " Destination: " + this.destination 
            + " Type: " + this.moveType + promotion);
    }

    // Convert move to chess notation (e.g., e2e4)
    public String toAlgebraic() {
        return squareToAlgebraic(start) + squareToAlgebraic(destination)
            + (pieceType != null ? pieceTypeToChar(pieceType) : "");
    }

    // Helper to convert a square index (0-63) to algebraic notation (e.g., 0 -> a1)
    private static String squareToAlgebraic(int square) {
        int file = square % 8;
        int rank = square / 8;
        return "" + (char) ('a' + file) + (rank + 1);
    }

    // Helper to get the promotion piece character
    private static char pieceTypeToChar(PieceType pieceType) {
        switch (pieceType) {
            case ROOK: return 'r';
            case BISHOP: return 'b';
            case KNIGHT: return 'n';
            case QUEEN: return 'q';
            default: throw new IllegalArgumentException("Unknown PieceType");
        }
    }
}
