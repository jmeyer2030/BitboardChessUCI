package userInterface;

import board.Position;
import moveGeneration.MoveGenerator;
import javax.swing.*;

public class GUIMain {
	public static void main(String[] args) {
		new MoveGenerator();
		Position position = new Position();
		SwingUtilities.invokeLater(() -> new GameGUI(position));
	}
}
