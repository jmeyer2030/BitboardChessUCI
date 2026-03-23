package com.jmeyer2030.driftwood.staticevaluation;

public class DummyNNUE implements Evaluator {
    @Override
    public void addFeature(int piece, int color, int square) {
    }

    @Override
    public void removeFeature(int piece, int color, int square) {
    }

    @Override
    public int computeOutput(int activePlayer) {
        return 0;
    }
}
