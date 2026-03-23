package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.config.GlobalConstants;

/**
 * Per-search scratch space and heuristic tables. Created once per {@code ucinewgame} and
 * reused across iterative-deepening iterations. Contains:
 * <ul>
 *   <li>Move buffer and parallel score array (shared across the search tree via index windows)</li>
 *   <li>Best-move array (one slot per ply)</li>
 *   <li>Killer moves, history heuristic, and triangular PV table</li>
 * </ul>
 *
 * <p>{@code firstNonMove} tracks the next free slot in {@code moveBuffer}. It is saved and
 * restored manually at each search node to implement move-window scoping.</p>
 */
public class SearchContext {
    public final int[] moveBuffer;
    public final int[] moveScores;
    public final int[] bestMoves;
    public int firstNonMove;
    public final KillerMoves killerMoves;
    public final HistoryHeuristic historyHeuristic;
    public final TriangularPVTable pvTable;
    public final SEE see;

    public SearchContext() {
        this.moveBuffer = new int[2048];
        this.moveScores = new int[2048];
        this.bestMoves = new int[GlobalConstants.MAX_PLY];
        this.firstNonMove = 0;
        this.killerMoves = new KillerMoves();
        this.historyHeuristic = new HistoryHeuristic();
        this.pvTable = new TriangularPVTable();
        this.see = new SEE();
    }
}

