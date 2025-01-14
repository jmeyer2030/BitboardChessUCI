package testing.testMoveGeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import board.Move;
import board.Position;
import moveGeneration.MoveGenerator;
import zobrist.HashTables;
import zobrist.Hashing;

public class Testing {
	public static long numTranspositions = 0;
/*
 * Public methods
 */
	/**
	 * Prints the differences between perft results
	 * @Param stockfish perft result
	 * @Param chess2 perft result
	 */
	public static void perftDiff(String stockFish, String generated) {
		Set<String> stockFishParse = new HashSet<String> (Arrays.asList(stockFish.split("\\n")));
		Set<String> generatedParse = new HashSet<String> (Arrays.asList(generated.split("\\n")));

		Set<String> diffs = new HashSet<String>();

		diffs.addAll(stockFishParse);
		diffs.addAll(generatedParse);
		stockFishParse.retainAll(generatedParse);//stockfish becomes AND
		diffs.removeAll(stockFishParse);
		System.out.println("Differences: ");
		diffs.stream().forEach(diff -> System.out.println(diff));
	}

	/**
	 * Runs a perft test on a position and prints results for each legal move
	 * @Param depth
	 * @Param position
	 */
	public static void perft(int depth, Position position) {
		if (depth < 1)
			return;
		List<Move> initial = MoveGenerator.generateStrictlyLegal(position);
		long total = 0;
		for (Move move : initial) {
			Position copy = new Position(position);
			copy.makeMove(move);
			long thisMove = perftRecursion(depth - 1, copy);
			System.out.println(notation(move.start) + notation(move.destination) + ": " + thisMove);
			total += thisMove;
		}
		System.out.println("Total: " + total);
	}

	public static void ttPerft(int depth, Position position) {
		if (depth < 1)
			return;
		List<Move> initial = MoveGenerator.generateStrictlyLegal(position);
		long total = 0;
		for (Move move : initial) {
			Position copy = new Position(position);
			copy.makeMove(move);
			long thisMove = ttPerftRecursion(depth - 1, copy);
			System.out.println(notation(move.start) + notation(move.destination) + ": " + thisMove);
			total += thisMove;
		}
		System.out.println("Total: " + total);
		System.out.println("Transpositions: " + numTranspositions);
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

	/**
	 * returns a standard notation string for a square in little endian
	 * @param square square
	 * @return standard notation square
	 */
	public static String notation(int square) {
		String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
		String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
		int rank = square / 8;
		int file = square % 8;
		return files[file] + ranks[rank] ;

	}
/*
 * Private Helper Methods
 */
 	/**
 	* Recursive component of perft
 	* @param depth remaining depth
 	* @param position position to perft
 	* @return moves at that depth for the position
 	*/
	private static long perftRecursion(int depth, Position position) {
		if (depth == 0)
			return 1;
		return MoveGenerator.generateStrictlyLegal(position).stream().mapToLong(move -> {
			position.makeMove(move);
			long result = perftRecursion(depth - 1, position);
			position.unMakeMove(move);
			return result;
		}).sum();
	}

	private static long ttPerftRecursion(int depth, Position position) {
		if (depth == 0)
			return 1;
		if (depth == 1)
			return MoveGenerator.generateStrictlyLegal(position).size();
		return MoveGenerator.generateStrictlyLegal(position).stream().mapToLong(move -> {
			position.makeMove(move);
			long hash = Hashing.computeZobrist(position);
			long result;
			if (HashTables.getPerftElement(hash) != null &&
				HashTables.getPerftElement(hash).zobristHash == hash &&
				HashTables.getPerftElement(hash).depth == depth) {
				numTranspositions++;
				result = HashTables.getPerftElement(hash).perftResult;
			} else {
				result = ttPerftRecursion(depth - 1, position);
				HashTables.addPerftElement(hash, depth, result);
			}
			position.unMakeMove(move);
			return result;
		}).sum();
	}

}