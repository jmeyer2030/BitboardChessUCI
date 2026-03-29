package com.jmeyer2030.driftwood.staticevaluation;

public interface Evaluator {
    void addFeature(int piece, int color, int square);
    void removeFeature(int piece, int color, int square);
    int computeOutput(int activePlayer);

    /** Copy current accumulator state to the next ply level. Called at the start of makeMove. */
    void pushAccumulator();

    /** Restore the previous ply's accumulator state. Called at the end of unmakeMove. */
    void popAccumulator();
}
