package engine.search;

import board.Position;
import board.PositionState;
import system.SearchMonitor;
import zobrist.ThreeFoldTable;
import zobrist.transposition.TranspositionTable;

import java.util.Stack;

/*
A class that stores state information related to the search:
- moveStack from search progress
- Transposition table
- Three fold table
*/
public class SearchState {
    public final TranspositionTable tt;
    public final SearchMonitor searchMonitor;
    public final Stack<Integer> searchStack;
    public final ThreeFoldTable threeFoldTable;

    public final int[] moveBuffer;
    public final int[] moveScores;
    public int firstNonMove;

    public SearchState(int ttNumBits, Position position) {
        if (ttNumBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(ttNumBits);
        }
        this.moveBuffer = new int[2048];
        this.moveScores = new int[2048];
        this.searchMonitor = new SearchMonitor(position);
        this.searchStack = new Stack<>();
        this.threeFoldTable = new ThreeFoldTable();
        this.firstNonMove = 0;
    }

    public SearchState(PositionState positionState) {
        this.tt = positionState.tt;
        this.threeFoldTable = positionState.threeFoldTable;
        this.searchStack = new Stack<>();
        this.searchMonitor = new SearchMonitor(positionState.position);
        this.moveBuffer = positionState.moveBuffer;
        this.moveScores = positionState.moveScores;
        this.firstNonMove = 0;
    }
}
