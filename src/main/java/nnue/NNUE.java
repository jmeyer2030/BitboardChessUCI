package nnue;

import board.FEN;
import board.Position;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Efficiently Updatable Neural Network
 * - Trained using Stock Fish training data using Bullet Lib
 *
 * - Contains and loads network weights and sizes
 * - Instantiations contain Accumulators, allow incremental updates, and output computation
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

    private int[] whiteAccumulator = new int[HIDDEN_LAYER_SIZE];
    private int[] blackAccumulator = new int[HIDDEN_LAYER_SIZE];

    // Stores added features for lazy updates
    private List<Integer> whiteAccumulatorAdd = new LinkedList<>();
    private List<Integer> blackAccumulatorAdd = new LinkedList<>();
    private List<Integer> whiteAccumulatorRemove = new LinkedList<>();
    private List<Integer> blackAccumulatorRemove = new LinkedList<>();


    private static final int ACCUMULATOR_LAZY_SIZE = 1024;

    private int addIndex = 0;
    private int[] whiteAccumulatorAddIndices = new int[ACCUMULATOR_LAZY_SIZE];
    private int[] blackAccumulatorAddIndices = new int[ACCUMULATOR_LAZY_SIZE];

    private int removeIndex = 0;
    private int[] whiteAccumulatorRemoveIndices = new int[ACCUMULATOR_LAZY_SIZE];
    private int[] blackAccumulatorRemoveIndices = new int[ACCUMULATOR_LAZY_SIZE];

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

    /**
     * Adds a feature to the accumulators
     *
     * @param piece  piece to add
     * @param color  color of the piece
     * @param square square of the piece
     */
    public void addFeature(int piece, int color, int square) {
        int whitePerspectiveVal = piece + (color == 0 ? 0 : 6); // If piece is white, 0-5
        int blackPerspectiveVal = piece + (color == 0 ? 6 : 0); // If piece is white, 6-11

        int whitePerspectiveIndex = 64 * whitePerspectiveVal + square;
        int blackPerspectiveIndex = 64 * blackPerspectiveVal + (square ^ 0b111000);

        whiteAccumulatorAddIndices[addIndex] = whitePerspectiveIndex;
        blackAccumulatorAddIndices[addIndex] = blackPerspectiveIndex;

        addIndex++;

        if (addIndex >= ACCUMULATOR_LAZY_SIZE)
            processAccumulatorChanges();
            //throw new RuntimeException("Add lazy accumulator overflow");
        // Lazily store feature updates
        //whiteAccumulatorAdd.add(whitePerspectiveIndex);
        //blackAccumulatorAdd.add(blackPerspectiveIndex);
        /*
        for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
            whiteAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][whitePerspectiveIndex];
            blackAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][blackPerspectiveIndex];
        }
        */
    }

    /**
     * Removes a feature to the accumulators
     *
     * @param piece  piece to remove
     * @param color  color of the piece
     * @param square square of the piece
     */
    public void removeFeature(int piece, int color, int square) {
        int whitePieceVal = piece + (color == 0 ? 0 : 6); // If piece is white, 0-5
        int blackPieceVal = piece + (color == 0 ? 6 : 0); // If piece is white, 6-11

        int whitePerspectiveIndex = 64 * whitePieceVal + square;
        int blackPerspectiveIndex = 64 * blackPieceVal + (square ^ 0b111000);

        whiteAccumulatorRemoveIndices[removeIndex] = whitePerspectiveIndex;
        blackAccumulatorRemoveIndices[removeIndex] = blackPerspectiveIndex;

        removeIndex++;

        if (removeIndex >= ACCUMULATOR_LAZY_SIZE)
            processAccumulatorChanges();
            //throw new RuntimeException("Remove lazy accumulator overflow");

        // Lazily store feature updates
        //whiteAccumulatorRemove.add(whitePerspectiveIndex);
        //blackAccumulatorRemove.add(blackPerspectiveIndex);

        /*
        for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
            whiteAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][whiteIndex];
            blackAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][blackIndex];
        }
        */
    }

    public void processAccumulatorChanges() {
        // For each piece (note that white and black sizes should always be equal
        /*
        while (!whiteAccumulatorAdd.isEmpty()) {
            int whiteAdd = whiteAccumulatorAdd.removeFirst();
            int blackAdd = blackAccumulatorAdd.removeFirst();
            for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
                whiteAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][whiteAdd];
                blackAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][blackAdd];
            }
        }

        while (!whiteAccumulatorRemove.isEmpty()) {
            int whiteRemove = whiteAccumulatorRemove.removeFirst();
            int blackRemove = blackAccumulatorRemove.removeFirst();
            for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
                whiteAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][whiteRemove];
                blackAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][blackRemove];
            }
        }
        */

        for (int i = 0; i < addIndex; i++) {
            int whiteAdd = whiteAccumulatorAddIndices[i];
            int blackAdd = blackAccumulatorAddIndices[i];
            for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
                whiteAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][whiteAdd];
                blackAccumulator[weightIndex] += hiddenLayerWeights[weightIndex][blackAdd];
            }
        }

        addIndex = 0;

        for (int i = 0; i < removeIndex; i++) {
            int whiteRemove = whiteAccumulatorRemoveIndices[i];
            int blackRemove = blackAccumulatorRemoveIndices[i];
            for (int weightIndex = 0; weightIndex < HIDDEN_LAYER_SIZE; weightIndex++) {
                whiteAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][whiteRemove];
                blackAccumulator[weightIndex] -= hiddenLayerWeights[weightIndex][blackRemove];
            }
        }

        removeIndex = 0;

    }

    /**
     * Fills accumulators by iterating over pieces and adding them as features
     *
     * @param position to fill accumulators from
     */
    public void fillAccumulators(Position position) {
        // First add biases
        for (int i = 0; i < 128; i++) {
            whiteAccumulator[i] = hiddenLayerBias[i];
            blackAccumulator[i] = hiddenLayerBias[i];
        }

        // Add features to the accumulators
        for (int color = 0; color <= 1; color++) {
            for (int piece = 0; piece <= 5; piece++) {
                // Get bitboard corresponding with a piece and color
                long pieceColorBB = position.pieces[piece] & position.pieceColors[color];

                // Iterate over these pieces and add features to accumulator
                while (pieceColorBB != 0) {
                    int square = Long.numberOfTrailingZeros(pieceColorBB);
                    pieceColorBB &= (pieceColorBB - 1);

                    addFeature(piece, color, square);
                }
            }
        }
        processAccumulatorChanges();
    }

    /**
     * Computes the output GIVEN that the accumulator states are already accurate
     */
    public int computeOutput(int activePlayer) {
        processAccumulatorChanges();

        int outputActivation = 0;

        for (int hiddenIndex = 0; hiddenIndex < HIDDEN_LAYER_SIZE; hiddenIndex++) {
            outputActivation += screlu(QA, whiteAccumulator[hiddenIndex]) * (int) outputWeights[(activePlayer == 0 ? 0 : HIDDEN_LAYER_SIZE) + hiddenIndex];
            outputActivation += screlu(QA, blackAccumulator[hiddenIndex]) * (int) outputWeights[(activePlayer == 0 ? HIDDEN_LAYER_SIZE : 0) + hiddenIndex];
        }

        outputActivation /= QA;

        outputActivation += outputBias;

        outputActivation *= SCALE;

        outputActivation /= QB * QA;

        return outputActivation;
    }

    /**
     * squared clipped relu, binds a value between upper and lower bounds and squares it
     *
     * @param upperBound maximum value input value can be
     */
    public int screlu(int upperBound, int value) {
        int clipped = Math.min(Math.max(value, 0), upperBound);
        return clipped * clipped;
    }

    /**
     * Retrieves the binary network data and returns it as a short array
     *
     * @return shortArray of weights and biases of the network
     */
    private static short[] getNetworkBytes() {

        byte[] dataAsByteArr;

        try {
            InputStream inputStream = NNUE.class.getResourceAsStream("/quantised.bin");

            if (inputStream == null) {
                throw new RuntimeException("Resource quanised.bin not found");
            }

            dataAsByteArr = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read quantised.bin", e);
        }

        short[] shortArray = new short[dataAsByteArr.length / 2];
        ByteBuffer buffer = ByteBuffer.wrap(dataAsByteArr).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < shortArray.length; i++) {
            shortArray[i] = buffer.getShort();
        }

        return shortArray;

    }

    /**
     * Initializes network weights and biases
     * <p>
     * Expected Data format:
     * input weights -> input Biases -> hidden weights -> hidden biases -> Output bias -> padding
     */
    private static void loadNetworkFromBinary() {
        short[] nnShorts = getNetworkBytes();

        // Get feature weights
        for (int hiddenLayerIndex = 0; hiddenLayerIndex < HIDDEN_LAYER_SIZE; hiddenLayerIndex++) {
            for (int inputIndex = 0; inputIndex < INPUT_SIZE; inputIndex++) {
                //int shortIndex = (inputIndex * HIDDEN_LAYER_SIZE + hiddenLayerIndex);
                //int shortIndex = hiddenLayerIndex * INPUT_SIZE + inputIndex;
                int shortIndex = inputIndex * HIDDEN_LAYER_SIZE + hiddenLayerIndex;

                hiddenLayerWeights[hiddenLayerIndex][inputIndex] = nnShorts[shortIndex];
            }
        }

        // Get feature biases
        int startShort = 128 * 768;
        for (int hlNeuron = 0; hlNeuron < 128; hlNeuron++) {
            int byteIndex = startShort + hlNeuron;
            hiddenLayerBias[hlNeuron] = nnShorts[byteIndex];
        }

        // Get output weights
        startShort += 128;
        for (int hlNeuron = 0; hlNeuron < 256; hlNeuron++) {
            int byteIndex = startShort + hlNeuron;
            outputWeights[hlNeuron] = nnShorts[byteIndex];
        }

        // Get output bias
        startShort += 256;
        outputBias = nnShorts[startShort];
    }
}