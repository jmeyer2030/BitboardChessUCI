package zobrist;

import java.util.ArrayList;

/*
Should probably just be a list of positions with if that position was a result of an irreversible move.
Then, we can just increment through until the first irreversible position is hit.
*/

public class ThreePly {
    public static ArrayList<ThreePlyElement> threePlyList = new ArrayList<>();

    /**
    *
    */
    public static void addPosition(long zobristHash, boolean reversible) {
        threePlyList.add(new ThreePlyElement(zobristHash, reversible));
    }

    /**
    *
    */
    public static void popPosition() {
        if (threePlyList.size() == 0) {
            throw new RuntimeException("Tried to pop a non existent element");
        }
        threePlyList.removeLast();
    }

    /**
    *
    */
    public static boolean positionRepeated(long zobristHash) {
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


    public static class ThreePlyElement {
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