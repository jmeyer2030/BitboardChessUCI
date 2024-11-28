package moveGeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import board.FEN;
import board.Move;
import board.Position;
public class Main {
	public static void main(String[] args) {
		String fish = "a2a3: 8457\r\n"
				+ "b2b3: 9345\r\n"
				+ "c2c3: 9272\r\n"
				+ "d2d3: 11959\r\n"
				+ "e2e3: 13134\r\n"
				+ "f2f3: 8457\r\n"
				+ "g2g3: 9345\r\n"
				+ "h2h3: 8457\r\n"
				+ "a2a4: 9329\r\n"
				+ "b2b4: 9332\r\n"
				+ "c2c4: 9744\r\n"
				+ "d2d4: 12435\r\n"
				+ "e2e4: 13160\r\n"
				+ "f2f4: 8929\r\n"
				+ "g2g4: 9328\r\n"
				+ "h2h4: 9329\r\n"
				+ "b1a3: 8885\r\n"
				+ "b1c3: 9755\r\n"
				+ "g1f3: 9748\r\n"
				+ "g1h3: 8881";
		String generated = "a2a3: 8457\r\n"
				+ "a2a4: 9329\r\n"
				+ "b2b3: 9347\r\n"
				+ "b2b4: 9332\r\n"
				+ "c2c3: 9347\r\n"
				+ "c2c4: 9818\r\n"
				+ "d2d3: 11961\r\n"
				+ "d2d4: 12437\r\n"
				+ "e2e3: 13280\r\n"
				+ "e2e4: 13308\r\n"
				+ "f2f3: 8457\r\n"
				+ "f2f4: 8929\r\n"
				+ "g2g3: 9347\r\n"
				+ "g2g4: 9328\r\n"
				+ "h2h3: 8457\r\n"
				+ "h2h4: 9329\r\n"
				+ "b1a3: 8885\r\n"
				+ "b1c3: 9757\r\n"
				+ "g1f3: 9754\r\n"
				+ "g1h3: 8883";
		perftDiff(fish, generated);
		
		new MoveGenerator();
		Position position = new Position();
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 15, 31));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 48, 32));
		long start = System.currentTimeMillis();
		//System.out.println("queen on 3" + (0 != (position.queens & (1L << 3))));
		//System.out.println("num d1 queen moves" + MoveGenerator.generateQueenMoves(position).size());
		//System.out.println("piece blocking at 10" + (0 != (position.occupancy & (1L << 10))));
		//perft(1, position);
		//String fenString = "rnbqkbnr/1pppp1pp/8/p3Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
		//FEN fen = new FEN(fenString);
		//Position position = new Position(fen);
		//System.out.println(position.enPassant);
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 52, 36));
		//position.printBoard();
		perft(4, position);
		long end = System.currentTimeMillis();
		
		System.out.println("Perft time: " + (end - start));
		
		
	}
	
	public static void perft(int depth, Position position) {
		if (depth < 1)
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
	
	public static long perftRecursion(int depth, Position position) {
		if (position.selfInCheck())
			return 0;
		if (depth == 0)
			return 1;
		return MoveGenerator.generateStrictlyLegal(position).stream().mapToLong(move -> {
			Position appliedMove = position.applyMove(move);
			if (appliedMove.gameStatus != 2)
				return 1;
			if (MoveGenerator.generateStrictlyLegal(appliedMove).size() == 0)
				return 1;
			return perftRecursion(depth - 1, appliedMove);
		}).sum();
		
	}
	
	public static String notation(int square) {
		String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
		String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
		int rank = square / 8;
		int file = square % 8;
		return files[file] + ranks[rank] ;
		
	}
	
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
}