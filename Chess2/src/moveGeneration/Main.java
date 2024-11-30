package moveGeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import board.FEN;
import board.Move;
import board.Position;
import system.BBO;
public class Main {
	public static void main(String[] args) {
		String fish = "a7a6: 1\r\n"
				+ "b7b6: 1\r\n"
				+ "e8f8: 1\r\n"
				+ "f7f6: 1\r\n"
				+ "g7g6: 1\r\n"
				+ "h7h6: 1\r\n"
				+ "a7a5: 1\r\n"
				+ "b7b5: 1\r\n"
				+ "e8e7: 1\r\n"
				+ "f7f5: 1\r\n"
				+ "g7g5: 1\r\n"
				+ "h7h5: 1\r\n"
				+ "d7c6: 1\r\n"
				+ "b7c6: 1\r\n"
				+ "b8a6: 1\r\n"
				+ "b8c6: 1\r\n"
				+ "g8f6: 1\r\n"
				+ "g8h6: 1\r\n"
				+ "g8e7: 1\r\n"
				+ "b4d2: 1\r\n"
				+ "b4a3: 1\r\n"
				+ "b4c3: 1\r\n"
				+ "b4a5: 1\r\n"
				+ "b4c5: 1\r\n"
				+ "b4d6: 1\r\n"
				+ "b4e7: 1\r\n"
				+ "b4f8: 1\r\n"
				+ "d8h4: 1\r\n"
				+ "d8g5: 1\r\n"
				+ "d8f6: 1\r\n"
				+ "d8e7: 1";
		String generated = "a7a5: 1\r\n"
				+ "a7a6: 1\r\n"
				+ "b7c6: 1\r\n"
				+ "b7b5: 1\r\n"
				+ "b7b6: 1\r\n"
				+ "f7f5: 1\r\n"
				+ "f7f6: 1\r\n"
				+ "g7g5: 1\r\n"
				+ "g7g6: 1\r\n"
				+ "h7h5: 1\r\n"
				+ "h7h6: 1\r\n"
				+ "b4d2: 1\r\n"
				+ "b4a3: 1\r\n"
				+ "b4c3: 1\r\n"
				+ "b4a5: 1\r\n"
				+ "b4c5: 1\r\n"
				+ "b4d6: 1\r\n"
				+ "b4e7: 1\r\n"
				+ "b4f8: 1\r\n"
				+ "b8c6: 1\r\n"
				+ "b8a6: 1\r\n"
				+ "g8f6: 1\r\n"
				+ "g8h6: 1\r\n"
				+ "g8e7: 1\r\n"
				+ "d8h4: 1\r\n"
				+ "d8g5: 1\r\n"
				+ "d8f6: 1\r\n"
				+ "d8e7: 1\r\n"
				+ "e8e7: 1\r\n"
				+ "e8f8: 1";
		Testing.perftDiff(fish, generated);
		
		new MoveGenerator();
		//Testing.printBoard(AbsolutePins.inBetween[0][7]);
		Position position = new Position();
		long start = System.currentTimeMillis();
		
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 12, 28));
		//Testing.printBoard(position.checkers);
		//System.out.println(MoveGenerator.generateAllMoves(position).size());
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 52, 36));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 5, 33));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 61, 25));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 33, 42));
		
		//FEN fen = new FEN("rnbqkbnr/pppp1ppp/8/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2");
		//position = new Position(fen);
		//System.out.println("moves of 53");
		//Testing.printBoard(position.moveScope[6]);
		
		/*
		//position = position.applyMove(new Move(Move.MoveType.ENPASSANT, 36, 43));
		System.out.println("WAM");
		Testing.printBoard(position.whiteAttackMap);
		System.out.println("occupancy");
		Testing.printBoard(position.occupancy);
		*/
		//System.out.println("BAM");
		//Testing.printBoard(position.blackAttackMap);
		//System.out.println("Occupancy");
		//Testing.printBoard(position.occupancy);
		//System.out.println(position.enPassant);
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 12, 28));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 48, 32));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 5, 33));

		Testing.perft(6, position);
		long end = System.currentTimeMillis();
		
		System.out.println("Perft time: " + (end - start));
		
		
	}
	
	
}