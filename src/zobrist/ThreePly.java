package zobrist;

import board.Move;
import board.PieceType;

import java.util.ArrayList;

/*
Should probably just be a list of positions with if that position was a result of an irreversible move.
Then, we can just increment through until the first irreversible position is hit.
*/

public class ThreePly {
    public final ArrayList<ThreePlyElement> threePlyList;



    public ThreePly() {
        this.threePlyList = new ArrayList<>();
    }
    /**
    *
    */
    public void addPosition(long zobristHash, Move move) {
        boolean reversible = (move.movePiece != PieceType.PAWN && move.captureType == null);
        threePlyList.add(new ThreePlyElement(zobristHash, reversible));
    }

    /**
    *
    */
    public void popPosition() {
        if (threePlyList.size() == 0) {
            throw new RuntimeException("Tried to pop a non existent element");
        }
        threePlyList.removeLast();
    }

    /**
    *
    */
    public boolean positionRepeated(long zobristHash) {
        int repetitions = 0;

        int start = threePlyList.size() - 1;

        for (int i = start; i >= 0; i--) {
            if (threePlyList.get(i).zobristHash == zobristHash) {
                repetitions++;
            }
            if (!threePlyList.get(i).reversible) {
                break;
            }
        }

        if (repetitions >= 2)
            return true;
        return false;
    }


    public class ThreePlyElement {
        public long zobristHash;
        public boolean reversible;
        public ThreePlyElement(long zobristHash, boolean reversible) {
            this.zobristHash = zobristHash;
            this.reversible = reversible;
        }
    }

    /*
        Whenever we get to a new node, we add it to the positionList
        Whenever we apply a move that is irreversible, we add it to the irreversibleMoveList

        whenever we unmake a move, we remove that position from the positionList
        whenever we unmake a move, if that move is irreversible, we remove it from the irreversible move list.

    */
}