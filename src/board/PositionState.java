package board;

import zobrist.ThreeFoldTable;
import zobrist.transposition.TranspositionTable;

/*
Represents the internally stored position/state of a chess engine:
- Includes things related to the state that we run computations on
    position
    tt
    three-fold
*/
public class PositionState {
    public Position position;
    public final TranspositionTable tt;
    public ThreeFoldTable threeFoldTable;

    public PositionState(int numBits, Position position) {
        if (numBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(numBits);
        }

        this.position = new Position(position);
        this.threeFoldTable = new ThreeFoldTable();
    }

    public void applyMove(int move) {
        position.makeMove(move);
        threeFoldTable.addPosition(position.zobristHash, move);
    }

}
