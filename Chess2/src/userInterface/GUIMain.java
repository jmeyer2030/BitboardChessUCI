package userInterface;

import board.Position;
import moveGeneration.MoveGenerator;

public class GUIMain {
	public static void main(String[] args) {
		new MoveGenerator();
		Position position = new Position();
		new GUI(position);
	}
}
