package nnue;

public interface NNUEInterface {
    void addFeature(int piece, int color, int square);
    void removeFeature(int piece, int color, int square);
    int computeOutput(int activePlayer);
}
