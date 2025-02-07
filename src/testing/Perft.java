package testing;

import board.Move;
import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import moveGeneration.MoveGenerator2;
import zobrist.perft.PerftElement;
import zobrist.perft.PerftTable;

import java.util.List;
import java.util.Stack;

import static testing.Notation.squareToChessNotation;

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
        int firstNonMove =  MoveGenerator2.generateAllMoves(position, moveBuffer, 0);
        System.out.println(firstNonMove);

        long total = 0;
        for (int i = 0; i < firstNonMove; i++) {
            position.makeMove(moveBuffer[i]);
            long thisMove = perftRecursion(depth - 1, position, moveBuffer, firstNonMove);
            System.out.println(squareToChessNotation(MoveEncoding.getStart(moveBuffer[i])) +
                    squareToChessNotation(MoveEncoding.getDestination(moveBuffer[i])) + ": " + thisMove);
            position.unMakeMove(moveBuffer[i]);

            total += thisMove;
        }
        System.out.println("Total: " + total);
        return total;
    }

    private static long perftRecursion(int depth, Position position, int[] moveBuffer, int firstNonMove) {
        if (depth == 0)
            return 1;
        long result = 0;
        int nextFirstNonMove = MoveGenerator2.generateAllMoves(position, moveBuffer, firstNonMove);
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

    /**
    * Runs a perft test on a position using a perft transposition table
    * @param depth to search
    * @param position to search
    * @return result of perft
    */
    public static long ttPerft(int depth, Position position) {
        if (depth < 1)
            return 1;

        List<Move> initial;
        try {
            initial = MoveGenerator.generateStrictlyLegal(position);
        } catch (InvalidPositionException e) {
            throw new RuntimeException();
        }

        long total = 0;

        PerftTable perftTable = new PerftTable(18);

        for (Move move : initial) {
            //position.makeMove(move);
            long thisMove = ttPerftRecursion(depth - 1, position, perftTable);
            //position.unMakeMove(move);
            System.out.println(squareToChessNotation(move.start) + squareToChessNotation(move.destination) + ": " + thisMove);
            total += thisMove;
        }
        System.out.println("Total: " + total);
        return total;
    }

    private static long ttPerftRecursion(int depth, Position position, PerftTable perftTable) {
        if (depth == 0)
            return 1;
        if (depth == 1) {
            try {
                return MoveGenerator.generateStrictlyLegal(position).size();
            } catch (InvalidPositionException e) {
                throw new RuntimeException();
            }
        }

        PerftElement perftElement = perftTable.getElement(position.zobristHash, depth);

        if (perftElement != null) {
            return perftElement.perftResult();
        }


        List<Move> legalMoves;
        try {
            legalMoves = MoveGenerator.generateStrictlyLegal(position);
        } catch (InvalidPositionException e) {
            throw new RuntimeException();
        }

        long result = 0;
        for (Move move : legalMoves) {
            //position.makeMove(move);
            result += ttPerftRecursion(depth - 1, position, perftTable);
            //position.unMakeMove(move);
        }

        perftTable.addElement(position.zobristHash, depth, result);

        return result;
    }

}
