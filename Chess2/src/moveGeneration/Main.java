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
		String fish = "a2a3: 21\r\n"
				+ "a2a4: 21\r\n"
				+ "b2b3: 21\r\n"
				+ "b2b4: 21\r\n"
				+ "c2c3: 21\r\n"
				+ "c2c4: 21\r\n"
				+ "e2e3: 21\r\n"
				+ "e2e4: 21\r\n"
				+ "f2f3: 21\r\n"
				+ "f2f4: 21\r\n"
				+ "g2g3: 21\r\n"
				+ "g2g4: 21\r\n"
				+ "h2h3: 21\r\n"
				+ "h2h4: 21\r\n"
				+ "e5e6: 24\r\n"
				+ "c1d2: 21\r\n"
				+ "c1e3: 21\r\n"
				+ "c1f4: 21\r\n"
				+ "c1g5: 4\r\n"
				+ "c1h6: 20\r\n"
				+ "b1d2: 21\r\n"
				+ "b1a3: 21\r\n"
				+ "b1c3: 21\r\n"
				+ "g1f3: 21\r\n"
				+ "g1h3: 21\r\n"
				+ "d1d7: 4\r\n"
				+ "d1d2: 21\r\n"
				+ "d1d3: 21\r\n"
				+ "d1d4: 21\r\n"
				+ "d1d5: 19\r\n"
				+ "d1d6: 3\r\n"
				+ "e1d2: 21";
		String generated = "a2a3: 21\r\n"
				+ "b2b3: 21\r\n"
				+ "c2c3: 21\r\n"
				+ "e2e3: 21\r\n"
				+ "f2f3: 21\r\n"
				+ "g2g3: 21\r\n"
				+ "h2h3: 21\r\n"
				+ "e5e6: 24\r\n"
				+ "a2a4: 21\r\n"
				+ "b2b4: 21\r\n"
				+ "c2c4: 21\r\n"
				+ "e2e4: 21\r\n"
				+ "f2f4: 21\r\n"
				+ "g2g4: 21\r\n"
				+ "h2h4: 21\r\n"
				+ "b1d2: 21\r\n"
				+ "b1a3: 21\r\n"
				+ "b1c3: 21\r\n"
				+ "g1f3: 21\r\n"
				+ "g1h3: 21\r\n"
				+ "c1d2: 21\r\n"
				+ "c1e3: 21\r\n"
				+ "c1f4: 21\r\n"
				+ "c1g5: 4\r\n"
				+ "c1h6: 20\r\n"
				+ "d1d2: 21\r\n"
				+ "d1d3: 21\r\n"
				+ "d1d4: 21\r\n"
				+ "d1d5: 19\r\n"
				+ "d1d6: 2\r\n"
				+ "d1d7: 4\r\n"
				+ "e1d2: 21";
		Testing.perftDiff(fish, generated);
		
		new MoveGenerator();
		Position position = new Position();
		long start = System.currentTimeMillis();
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 15, 31));
		//position = position.applyMove(new Move(Move.MoveType.QUIET, 48, 32));
		/*
		
		FEN fen = new FEN("rnbq1bnr/ppppkppp/3Q4/4P3/8/8/PPP1PPPP/RNB1KBNR b KQ - 2 3");
		position = new Position(fen);
		Position position2 = position.applyMove(new Move(Move.MoveType.CAPTURE, 52, 43));
		System.out.println("p2 self in check = " + position2.selfInCheck());
		Testing.printBoard(position.kings);
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