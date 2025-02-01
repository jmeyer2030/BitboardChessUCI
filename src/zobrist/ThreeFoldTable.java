package zobrist;

import board.Move;
import board.PieceType;

import java.util.ArrayList;

/*
A table of searched positions for the purpose of determining if a position has been reached three times, leading to a draw
*/
public class ThreeFoldTable {

    public final ArrayList<ThreeFoldElement> searchedPositions;

    public ThreeFoldTable() {
        this.searchedPositions = new ArrayList<>();
    }
    /**
    * adds a position to the list of reached positions
    */
    public void addPosition(long zobristHash, Move move) {
        boolean reversible = (move.movePiece != PieceType.PAWN && move.captureType == null);
        searchedPositions.add(new ThreeFoldElement(zobristHash, reversible));
    }

    /**
    * Removes the top position from the list of reached positions
    */
    public void popPosition() {
        if (searchedPositions.size() == 0) {
            throw new RuntimeException("Tried to pop a non existent element");
        }
        searchedPositions.removeLast();
    }

    /**
    * Returns true if a position's repetitions have been exceeded
    * @return true if the
    */
    public boolean positionRepeated(long zobristHash) {
        int repetitions = 0;

        int start = searchedPositions.size() - 1;

        for (int i = start; i >= 0; i--) {
            if (searchedPositions.get(i).zobristHash == zobristHash) {
                repetitions++;
            }
            // If we reach a position that has
            if (!searchedPositions.get(i).reversible) {
                break;
            }
        }

        if (repetitions >= 2)
            return true;
        return false;
    }



    public boolean positionDrawn(long zobristHash) {
        int repetitions = 0;
        int start = searchedPositions.size() - 1;

        for (int i = start; i >= 0; i--) {
            if (searchedPositions.get(i).zobristHash == zobristHash) {
                repetitions++;
            }
            // If we reach a position that has
            if (!searchedPositions.get(i).reversible) {
                break;
            }
        }

        if (repetitions >= 3)
            return true;
        return false;
    }

    /**
    * A class that represents a position that has been previously reached due to a move
    * that is reversible or irreversible
    */
    public class ThreeFoldElement {
        public long zobristHash;
        public boolean reversible;

        public ThreeFoldElement(long zobristHash, boolean reversible) {
            this.zobristHash = zobristHash;
            this.reversible = reversible;
        }
    }
}