package board;

import zobrist.ThreePly;
import zobrist.TranspositionTable;

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
    public ThreePly threePly;

    public PositionState(int numBits, Position position) {
        if (numBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(numBits);
        }

        this.position = new Position(position);
        this.threePly = new ThreePly();
    }

    public void applyMove(Move move) {
        position.makeMove(move);
        threePly.addPosition(position.zobristHash, move);
    }

}
