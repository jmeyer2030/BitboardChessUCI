package engine;

import board.Move;
import board.Position;
import system.SearchMonitor;
import zobrist.ThreePly;
import zobrist.TranspositionTable;

import java.util.Stack;
/*
A class that stores state information related to the search:
- moveStack from search progress
- Transposition table
- Three ply table

This should not passed to a search, from the position state class.
*/
public class SearchState {
    public final TranspositionTable tt;
    public final SearchMonitor searchMonitor;
    public final Stack<Move> searchStack;
    public final ThreePly threePly;

    public SearchState(int ttNumBits, Position position) {
        if (ttNumBits == 0) {
            this.tt = null;
        } else {
            this.tt = new TranspositionTable(ttNumBits);
        }
        this.searchMonitor = new SearchMonitor(position);
        this.searchStack = new Stack<Move>();
        this.threePly = new ThreePly();
    }
}
