package board;

import zobrist.ThreeFoldTable;
import zobrist.transposition.TranspositionTable;

/*
Represents the internally stored position/state of a chess engine:
- Includes things related to the state that we run computations on
    position
    tt
    three-fold
    moveBuffer
*/
public class PositionState {
    public Position position;
    public final TranspositionTable tt;
    public ThreeFoldTable threeFoldTable;
    public int[] moveBuffer;
    public int[] moveScores;

    public PositionState(int numBits, Position position) {
        if (numBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(numBits);
        }

        this.position = new Position(position);
        this.threeFoldTable = new ThreeFoldTable();
        this.moveBuffer = new int[2048];
        this.moveScores = new int[2048];
    }

    public void applyMove(int move) {
        position.makeMove(move);
        threeFoldTable.addPosition(position.zobristHash, move);
    }

}
