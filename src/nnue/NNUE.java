package nnue;

import board.FEN;
import board.Position;

import java.io.IOException;
import java.io.InputStream;

/**
* Represents an Efficiently Updatable Neural Network
*  - Contains and loads network weights and sizes
*  - Instantiations contain Accumulators
*/
public class NNUE {
    private static final int INPUT_SIZE = 768;
    private static final int HIDDEN_LAYER_SIZE = 128;

    private static final int QA = 255;
    private static final int QB = 64;
    private static final int SCALE = 400;

    private static final short[][] hiddenLayerWeights = new short[HIDDEN_LAYER_SIZE][INPUT_SIZE];
    private static final short[] hiddenLayerBias = new short[HIDDEN_LAYER_SIZE];

    private static final short[] outputWeights = new short[HIDDEN_LAYER_SIZE * 2];
    private static short outputBias;

    public int[] ourAccumulator = new int[HIDDEN_LAYER_SIZE]; // Side to move
    public int[] theirAccumulator = new int[HIDDEN_LAYER_SIZE]; // Other player

    /**
    * Initializes weights and biases from quantised.bin on class load
    */
    static {
        loadNetworkFromBinary();
    }

    /**
    * Creates the nn and fills it's accumulators
    */
    public NNUE(Position position) {
        fillAccumulators(position);
    }

    public static void main(String[] args) {
        //FEN fen = new FEN("2Q5/8/8/8/8/6k1/8/4K3 w - - 0 1"); // WHITE WAY WINNING
        //FEN fen = new FEN("2q5/8/8/8/8/6k1/8/4K3 w - - 0 1"); // BLACK WAY WINNING
        //FEN fen = new FEN("8/8/3k4/8/8/3K4/8/8 w - - 0 1"); // ONLY KINGS
        //FEN fen = new FEN("rnb1k2r/1pq1bppp/p2ppn2/6B1/3NPP2/2N2Q2/PPP3PP/2KR1B1R b kq - 4 9"); // Equal opening
        //Position position = new Position(fen);
        Position position = new Position();
        System.out.println(position.nnue.computeOutput());
    }

    /**
    * Adds a feature to the accumulators
    * @param piece piece to add
    * @param color color of the piece
    * @param square square of the piece
    * @param position position corresponding to the nn (can prob just pass active player)
    */
    public void addFeature(int piece, int color, int square, Position position) {
        int activePlayer = position.activePlayer;

        // pieceVal is 0-5 in our accumulator if our color
        int ourPieceVal = piece + (color == activePlayer ? 0 : 6);

        // pieceVal is 0-5 in their accumulator if their color
        int theirPieceVal = piece + (color == activePlayer ? 6 : 0);

        int ourIndex = 64 * ourPieceVal + square;
        int theirIndex = 64 * theirPieceVal + square;

        for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
            ourAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][ourIndex];
            theirAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][theirIndex];
        }
    }

    /**
     * Removes a feature to the accumulators
     * @param piece piece to remove
     * @param color color of the piece
     * @param square square of the piece
     * @param position position corresponding to the nn (can prob just pass active player)
     */
    public void removeFeature(int piece, int color, int square, Position position) {
        int activePlayer = position.activePlayer;

        // pieceVal is 0-5 in our accumulator if our color
        int ourPieceVal = piece + (color == activePlayer ? 0 : 6);

        // pieceVal is 0-5 in their accumulator if their color
        int theirPieceVal = piece + (color == activePlayer ? 6 : 0);

        int ourIndex = 64 * ourPieceVal + square;
        int theirIndex = 64 * theirPieceVal + square;

        for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
            ourAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][ourIndex];
            theirAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][theirIndex];
        }
    }

    /**
    * Switch their and our accumulator when we make a move
    */
    public void switchSides() {
        int[] tempOurAccumulator = ourAccumulator;
        ourAccumulator = theirAccumulator;
        theirAccumulator = tempOurAccumulator;
    }

    /**
    * Fills accumulators by iterating over pieces and adding them as features
    * @param position to fill accumulators from
    */
    public void fillAccumulators(Position position) {
        // First add biases
        for (int i = 0; i < 128; i++) {
            ourAccumulator[i] = hiddenLayerBias[i];
            theirAccumulator[i] = hiddenLayerBias[i];
        }

        // Add features to the accumulators
        for (int color = 0; color <= 1; color++) {
            for (int piece = 0; piece <= 5; piece++) {
                long pieceColorBB = position.pieces[piece] & position.pieceColors[color];
                while (pieceColorBB != 0) {
                    int square = Long.numberOfTrailingZeros(pieceColorBB);
                    pieceColorBB &= (pieceColorBB - 1);

                    addFeature(piece, color, square, position);
                }
            }
        }
    }

    /**
    * Computes the output GIVEN that the accumulator states are already accurate
    */
    public int computeOutput() {
        int outputActivation = outputBias;

        for (int hiddenIndex = 0; hiddenIndex < HIDDEN_LAYER_SIZE; hiddenIndex++) {
            outputActivation += crelu(0, QA, ourAccumulator[hiddenIndex]) * (int) outputWeights[hiddenIndex];
            outputActivation += crelu(0, QA, theirAccumulator[hiddenIndex]) * (int) outputWeights[HIDDEN_LAYER_SIZE + hiddenIndex];
        }

        outputActivation *= SCALE;

        outputActivation /= QA * QB;

        return outputActivation;
    }

    /**
    * Clipped relu, binds a value between upper and lower bounds
    * @param lowerBound minimum value input value can be
    * @param upperBound maximum value input value can be
    */
    public int crelu(int lowerBound, int upperBound, int value) {
        if (value < lowerBound)
            return lowerBound;

        if (value > upperBound)
            return upperBound;

        return value;
    }

    /**
    * Retrieves the binary network data and returns it as a byte array
    */
    private static byte[] getNetworkBytes() {
        try (InputStream inputStream = NNUE.class.getResourceAsStream("/nnue/quantised.bin")) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: nnue/quantised.bin");
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read quantised.bin", e);
        }
    }

    /**
    * Initializes network weights and biases
    *
    * Expected Data format:
    * input weights -> input Biases -> hidden weights -> hidden biases -> Output bias -> padding
    */
    private static void loadNetworkFromBinary() {
        byte[] nnBytes = getNetworkBytes();

        // Get feature weights
        int startByte = 0;
        for (int hiddenLayerIndex = 0; hiddenLayerIndex < HIDDEN_LAYER_SIZE; hiddenLayerIndex++) {
            for (int inputIndex = 0; inputIndex < INPUT_SIZE; inputIndex++) {
                int byteIndex = (inputIndex * HIDDEN_LAYER_SIZE + hiddenLayerIndex) * 2;

                byte firstByte = nnBytes[byteIndex];
                byte secondByte = nnBytes[byteIndex + 1];

                hiddenLayerWeights[hiddenLayerIndex][inputIndex] = (short) ((secondByte << 8) | (firstByte & 0xFF));
            }
        }

        // Get feature biases
        startByte = 128 * 768 * 2;
        for (int hlNeuron = 0; hlNeuron < 128; hlNeuron++) {
            int byteIndex = startByte + hlNeuron * 2;
            byte firstByte = nnBytes[byteIndex];
            byte secondByte = nnBytes[byteIndex + 1];
            hiddenLayerBias[hlNeuron] = (short) ((secondByte << 8) | (firstByte & 0xFF));
        }

        // Get output weights
        startByte = (128 * 768 + 128) * 2;
        for (int hlNeuron = 0; hlNeuron < 256; hlNeuron++) {
            int byteIndex = startByte + hlNeuron * 2;
            byte firstByte = nnBytes[byteIndex];
            byte secondByte = nnBytes[byteIndex + 1];
            outputWeights[hlNeuron] = (short) ((secondByte << 8) | (firstByte & 0xFF));
        }

        // Get output bias
        startByte = (128 * 768 + 128 + 256) * 2;
        byte firstByte = nnBytes[startByte];
        byte secondByte = nnBytes[startByte + 1];
        outputBias = (short) ((secondByte << 8) | (firstByte & 0xFF));
    }
}