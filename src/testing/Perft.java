package testing;

import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import zobrist.perft.PerftElement;
import zobrist.perft.PerftTable;

import java.util.List;

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
        List<Move> initial;
        try {
            initial = MoveGenerator.generateStrictlyLegal(position);
        } catch (InvalidPositionException e) {
            throw new RuntimeException();
        }
        long total = 0;
        for (Move move : initial) {
            Position copy = new Position(position);
            copy.makeMove(move);
            long thisMove = perftRecursion(depth - 1, copy);
            System.out.println(squareToChessNotation(move.start) + squareToChessNotation(move.destination) + ": " + thisMove);
            total += thisMove;
        }
        System.out.println("Total: " + total);
        return total;
    }

    private static long perftRecursion(int depth, Position position) {
        if (depth == 0)
            return 1;

        long result = 0;
        List<Move> legalMoves;
        try {
            legalMoves = MoveGenerator.generateStrictlyLegal(position);
        } catch(InvalidPositionException e) {
            throw new RuntimeException();
        }
        for (Move move : legalMoves) {
            position.makeMove(move);
            result += perftRecursion(depth - 1, position);
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
            position.makeMove(move);
            long thisMove = ttPerftRecursion(depth - 1, position, perftTable);
            position.unMakeMove(move);
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
            position.makeMove(move);
            result += ttPerftRecursion(depth - 1, position, perftTable);
            position.unMakeMove(move);
        }

        perftTable.addElement(position.zobristHash, depth, result);

        return result;
    }

}
