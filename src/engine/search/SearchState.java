package engine.search;

import board.Move;
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
    public final Stack<Move> searchStack;
    public final ThreeFoldTable threeFoldTable;

    public SearchState(int ttNumBits, Position position) {
        if (ttNumBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(ttNumBits);
        }
        this.searchMonitor = new SearchMonitor(position);
        this.searchStack = new Stack<Move>();
        this.threeFoldTable = new ThreeFoldTable();
    }

    public SearchState(PositionState positionState) {
        this.tt = positionState.tt;
        this.threeFoldTable = positionState.threeFoldTable;
        this.searchStack = new Stack<Move>();
        this.searchMonitor = new SearchMonitor(positionState.position);
    }
}
