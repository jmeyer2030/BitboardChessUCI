package com.jmeyer2030.driftwood.staticevaluation;

public interface Evaluator {
    void addFeature(int piece, int color, int square);
    void removeFeature(int piece, int color, int square);
    int computeOutput(int activePlayer);
}
