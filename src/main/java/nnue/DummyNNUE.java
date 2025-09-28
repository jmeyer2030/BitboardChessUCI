package nnue;

public class DummyNNUE implements NNUEInterface {
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
