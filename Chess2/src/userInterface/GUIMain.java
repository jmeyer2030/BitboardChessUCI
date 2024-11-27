package userInterface;

import board.Position;
import moveGeneration.MoveGenerator;

public class GUIMain {
	public static void main(String[] args) {
		MoveGenerator mg = new MoveGenerator();
		Position position = new Position();
		new GUI(position);
	}
}
