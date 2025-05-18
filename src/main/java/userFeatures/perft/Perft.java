package userFeatures.perft;

import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;

public class Perft {

    /**
     * Runs a perft test on a position and prints results for each legal move
     * @Param depth
     * @Param position
     */
    public static long perft(int depth, Position position) {
        if (depth < 1)
            return 1;

        int[] moveBuffer = new int[2048];
        int firstNonMove =  MoveGenerator.generateAllMoves(position, moveBuffer, 0);

        System.out.println("Perft begin: Depth: " + depth);

        long total = 0;
        for (int i = 0; i < firstNonMove; i++) {
            position.makeMove(moveBuffer[i]);
            long thisMove = perftRecursion(depth - 1, position, moveBuffer, firstNonMove);
            System.out.println(MoveEncoding.getLAN(moveBuffer[i]) + ": " + thisMove);
            position.unMakeMove(moveBuffer[i]);

            total += thisMove;
        }
        System.out.println("Total: " + total);
        return total;
    }

    private static long perftRecursion(int depth, Position position, int[] moveBuffer, int firstNonMove) {
        try {
            position.validPosition();
        } catch (InvalidPositionException ipe) {
            throw new RuntimeException();
        }
        if (depth == 0)
            return 1;
        long result = 0;
        int nextFirstNonMove = MoveGenerator.generateAllMoves(position, moveBuffer, firstNonMove);
        if (depth == 1) {
            return nextFirstNonMove - firstNonMove;
        }

        for (int i = firstNonMove; i < nextFirstNonMove; i++) {
            int move = moveBuffer[i];

            position.makeMove(move);
            result += perftRecursion(depth - 1, position, moveBuffer, nextFirstNonMove);
            position.unMakeMove(move);
        }

        return result;
    }
}
