package moveGeneration;

import java.util.List;

import board.Move;
import board.Position;
public class Main {
	public static void main(String[] args) {
		
		new MoveGenerator();
		Position position = new Position();
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 15, 31));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 48, 32));
		long start = System.currentTimeMillis();
		//System.out.println("queen on 3" + (0 != (position.queens & (1L << 3))));
		//System.out.println("num d1 queen moves" + MoveGenerator.generateQueenMoves(position).size());
		//System.out.println("piece blocking at 10" + (0 != (position.occupancy & (1L << 10))));
		//perft(1, position);
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
			if (appliedMove.selfInCheck())
				return 0;
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
}