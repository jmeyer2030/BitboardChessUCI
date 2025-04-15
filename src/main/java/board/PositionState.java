package main.java.board;

import main.java.engine.search.HistoryHeuristic;
import main.java.engine.search.KillerMoves;
import main.java.engine.search.TriangularPVTable;
import main.java.zobrist.ThreeFoldTable;
import main.java.zobrist.transposition.TranspositionTable;

/*
Represents the internally stored position/state of a chess main.java.engine:
- Includes things related to the state that we run computations on
    position
    tt
    three-fold
    moveBuffer
    killer moves
*/
public class PositionState {
    public Position position;

    public final TranspositionTable tt;
    public ThreeFoldTable threeFoldTable;
    public HistoryHeuristic historyHeuristic;
    public KillerMoves killerMoves;
    public TriangularPVTable pvTable;

    public int[] moveBuffer;
    public int[] moveScores;
    public int firstNonMove;

    public PositionState(int numBits) {
        if (numBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(numBits);
        }

        this.pvTable = new TriangularPVTable();
        this.historyHeuristic = new HistoryHeuristic();
        this.position = new Position();
        this.threeFoldTable = new ThreeFoldTable();
        this.firstNonMove = 0;
        this.moveBuffer = new int[2048];
        this.moveScores = new int[2048];
        this.killerMoves = new KillerMoves();
    }

    public void applyMove(int move) {
        position.makeMove(move);
        threeFoldTable.addPosition(position.zobristHash, move);
    }

}
