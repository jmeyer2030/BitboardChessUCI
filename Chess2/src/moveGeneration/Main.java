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
		/*
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		String fish = "";
		String generated = "";
		Testing.perftDiff(fish, generated);
		
		new MoveGenerator();
		*/
		new MoveGenerator();
		//Testing.printBoard(AbsolutePins.inBetween[0][7]);
		Position position = new Position();
		long start = System.currentTimeMillis();
		//try {
		//	Thread.sleep(10000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
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

		for (int i = 0; i < 10_000_000; i++) {
			//MoveGenerator.generateMoves(position);
			position.makeMove(new Move(Move.MoveType.QUIET, 12, 28)); //e4
			position.unMakeMove();
		}
		
		/*
		position.makeMove(new Move(Move.MoveType.QUIET, 12, 28)); //e4
		position.makeMove(new Move(Move.MoveType.QUIET, 51, 35)); //d5
		
		System.out.println("1e4, d5, ");
		position.makeMove(new Move(Move.MoveType.CAPTURE, 28, 35)); //exd
		position.printBoard();
		
		System.out.println("unmade move");
		position.unMakeMove();
		position.unMakeMove();
		position.unMakeMove();
		position.printBoard();
		*/
		//Testing.perft(6, position);
		long end = System.currentTimeMillis();
		
		System.out.println("Total time: " + (end - start));
		
		
	}
	
	
}