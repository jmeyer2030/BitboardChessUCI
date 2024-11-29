package moveGeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import board.Move;
import board.Position;

public class Testing {
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
			long thisMove = perftRecursion(depth - 1, position.applyMove(move));
			System.out.println(notation(move.start) + notation(move.destination) + ": " + thisMove);
			total += thisMove;
		}
		System.out.println("Total: " + total);
		
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
	
//Private Helper Methods
	private static long perftRecursion(int depth, Position position) {
		if (depth == 0 || position.gameStatus != 2)
			return 1;
		return MoveGenerator.generateStrictlyLegal(position).stream().mapToLong(move -> {
			Position appliedMove = position.applyMove(move);
			return perftRecursion(depth - 1, appliedMove);
		}).sum();
	}
	
	private static String notation(int square) {
		String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
		String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
		int rank = square / 8;
		int file = square % 8;
		return files[file] + ranks[rank] ;
		
	}
}
