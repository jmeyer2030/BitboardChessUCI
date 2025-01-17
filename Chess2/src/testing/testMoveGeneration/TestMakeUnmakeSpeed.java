package testing.testMoveGeneration;

import java.util.List;

import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;

public class TestMakeUnmakeSpeed {
	public static void main(String[] args) throws InvalidPositionException {
		new MoveGenerator();
		Position position = new Position();
		long start = System.currentTimeMillis();

		List<Move> moveList = MoveGenerator.generateStrictlyLegal(position);
		Move move = moveList.getFirst();
		for (int i = 0; i < 10_000_000; i++) {
			Position copy = new Position(position);
			copy.makeMove(move);
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Total time: " + (end - start));
	}
	
	
}