package moveGeneration;
import board.Position;
public class Main {
	public static void main(String[] args) {
		RookMagicBitboard rmb = new RookMagicBitboard();
		BishopMagicBitboard bmb = new BishopMagicBitboard();
		bmb.initializeAll();
		rmb.initializeAll();
		
		Position position = new Position();
		
		bmb.printBoard(BishopMagicBitboard.moveBoards.get(1).get(10));
		long blockerBoard = 0b0000000000000000000000000000000000000000000000000000000000000000L;
		bmb.printBoard(bmb.generateMoveBoard(blockerBoard, 0));
		//rmb.printBoard(rmb.getMoveBoard(0, position.occupancy));
		//bmb.printBoard(bmb.getMoveBoard(2, position.occupancy));
	}
}