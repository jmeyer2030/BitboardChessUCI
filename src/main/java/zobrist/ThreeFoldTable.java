package zobrist;

import board.MoveEncoding;

/*
A table of searched positions for the purpose of determining if a position has been reached three times, leading to a draw
*/
public class ThreeFoldTable {

    private long[] zobristHashes = new long[1024];
    private int[] moves = new int[1024];
    private boolean[] isReversible = new boolean[1024];
    private int firstEmptySpace;

    public ThreeFoldTable() {
        firstEmptySpace = 0;
    }
    /**
    * adds a position to the list of reached positions
    */
    public void addPosition(long zobristHash, int move) {
        zobristHashes[firstEmptySpace] = zobristHash;
        moves[firstEmptySpace] = move;
        isReversible[firstEmptySpace] = MoveEncoding.getIsReversible(move);


        firstEmptySpace++;
    }

    /**
    * Removes the top position from the list of reached positions
    */
    public void popPosition() {
        firstEmptySpace--;
    }

    /**
    * Returns true if a position has been repeated other than the current state
    * We can't skip the first position. If it isn't reversible, we want to short circuit.
    * @return move if repeated, else -1
    */
    public boolean positionRepeated(long zobristHash) {
        int repetitions = 0;
        int index = firstEmptySpace - 1; // Include current position
        while (index >= 0) {
            if (zobristHashes[index] == zobristHash) {
                repetitions++;
                if (repetitions >= 2) {
                    return true;
                }
            }
            if (!isReversible[index]) {
                break;
            }
            index--;
        }
        return false;
    }

    public boolean positionDrawn(long zobristHash) {
        int repetitions = 0;
        int index = firstEmptySpace - 1; // Include current position
        while (index >= 0) {
            if (zobristHashes[index] == zobristHash) {
                repetitions++;
                if (repetitions >= 3) {
                    return true;
                }
            }
            if (!isReversible[index]) {
                break;
            }
            index--;
        }
        return false;
    }
}