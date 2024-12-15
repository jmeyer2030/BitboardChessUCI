package moveGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import board.Move;
import board.Position;

public class Testing {

	public static int perft1(int depth, Position position) {
		// Base case: if depth is 0 or the game is over
		if (depth == 0 || position.gameStatus != 2) {
			return 1;
		}

		int nodes = 0;

		// Generate strictly legal moves for the current position
		List<Move> legalMoves = MoveGenerator.generateStrictlyLegal(position);

		// For each legal move, apply it, recursively calculate perft, and undo the move
		for (Move move : legalMoves) {
			position.makeMove(move);           // Apply the move
			nodes += perft1(depth - 1, position); // Recursively count the moves at the next depth
			position.unMakeMove(move);         // Undo the move
		}
		return nodes;
	}
	/**
	 * Runs a perft test on a position
	 * @Param depth
	 * @Param position
	 */

	public static void perft(int depth, Position position) {
		if (depth < 1 || position.gameStatus != 2)
			return;
		List<Move> initial = MoveGenerator.generateStrictlyLegal(position);
		int total = 0;
		for (Move move : initial) {
			Position copy = new Position(position);
			copy.makeMove(move);
			long thisMove = perftRecursion(depth - 1, copy);
			System.out.println(notation(move.start) + notation(move.destination) + ": " + thisMove);
			total += thisMove;
		}
		System.out.println("Total: " + total);
	}
//Private Helper Methods
	private static long perftRecursion(int depth, Position position) {
		if (depth == 0 || position.gameStatus != 2)
			return 1;
		return MoveGenerator.generateStrictlyLegal(position).stream().mapToLong(move -> {
			Position appliedMove = new Position(position);
			appliedMove.makeMove(move);
			return perftRecursion(depth - 1, appliedMove);
		}).sum();
	}

	/**
	 * Prints the differences between perft results
	 * @Param stockfish perft result
	 * @Param chess2 perft result
	 */
	public static void perftDiff(String stockFish, String generated) {
		Set<String> stockFishParse = new HashSet<String> (Arrays.asList(stockFish.split("\\r\\n")));
		Set<String> generatedParse = new HashSet<String> (Arrays.asList(generated.split("\\r\\n")));
		
		Set<String> diffs = new HashSet<String>();
		
		diffs.addAll(stockFishParse);
		diffs.addAll(generatedParse);
		stockFishParse.retainAll(generatedParse);//stockfish becomes AND
		diffs.removeAll(stockFishParse);
		System.out.println("Differences: ");
		diffs.stream().forEach(diff -> System.out.println(diff));
	}
	
	/**
	 * prints the board in Little-endian rank-file representation
	 * @Param bitboard
	 */
	public static void printBoard(long bitBoard) {
	    for (int rank = 7; rank >= 0; rank--) {
	        for (int file = 0; file < 8; file++) {
	            System.out.print(((bitBoard & (1L << (rank * 8 + file))) != 0) ? "1 " : "0 ");
	        }
	        System.out.println();
	    }
	}

	private static String notation(int square) {
		String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
		String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
		int rank = square / 8;
		int file = square % 8;
		return files[file] + ranks[rank] ;
		
	}
}

/*
 * MultiThreaded perft
public static void perft(int depth, Position position) {
    if (depth < 1 || position.gameStatus != 2) return;

    List<Move> initialMoves = MoveGenerator.generateMoves(position);
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<Long>> futures = new ArrayList<>();

    for (Move move : initialMoves) {
        Position newPosition = position.applyMove(move);
        Callable<Long> task = () -> perftRecursion(depth - 1, newPosition);
        futures.add(executor.submit(task));
    }

    long total = 0;
    try {
        for (int i = 0; i < initialMoves.size(); i++) {
            Move move = initialMoves.get(i);
            long thisMove = futures.get(i).get();
            System.out.println(notation(move.start) + notation(move.destination) + ": " + thisMove);
            total += thisMove;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        executor.shutdown();
    }

    System.out.println("Total: " + total);
}

// Private Helper Methods
private static long perftRecursion(int depth, Position position) {
    if (depth == 0 || position.gameStatus != 2) return 1;

    return MoveGenerator.generateMoves(position)
            .parallelStream()
            .mapToLong(move -> perftRecursion(depth - 1, position.applyMove(move)))
            .sum();
}
*/
