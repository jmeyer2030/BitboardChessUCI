package userInterface;

import board.Color;
import board.Position;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import testing.testUserInterface.GameGUI;
import zobrist.Hashing;

import javax.swing.*;
import java.util.Objects;

public class PlayInGUI {

	private static final String engineChoiceMsg = "Play against engine";
	private static final String noEngineChoiceMsg = "No engine";

	/**
	* Ask if playing against an engine
	*   if true -> get time control/color -> launch
	*   if false -> get time control/color -> launch
	*/
	public static void main(String[] args) {
		// Retrieve if playing against an engine
		String gameType = gameType();

		if (gameType.equals("no choice")) {
			return;
		}

		boolean playingEngine = gameType.equals(engineChoiceMsg);
		GameSettings gameSettings = getGameSettings(playingEngine);

        try {
			Objects.requireNonNull(gameSettings);
			gameSettings.print();
			new MoveGenerator();
			Hashing.initializeRandomNumbers();
			Position position = new Position();
			SwingUtilities.invokeLater(() -> {
                try {
                    new GameGUI(position, gameSettings);
                } catch (InvalidPositionException e) {
                    throw new RuntimeException(e);
                }
            });
		} catch (Exception e) {
			System.out.println("Game settings must not be null!");
        }
	}

	/**
	* Gets user input about playing against an engine or not
	* @return string of either engineChoiceMsg, noEngineChoiceMsg, or "no choice"
	*/
	public static String gameType() {
		String[] options = {engineChoiceMsg, noEngineChoiceMsg};
		int choice = JOptionPane.showOptionDialog(
				null,
				"Choose to play options:",
				"Play Options",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
		);
		if (choice == JOptionPane.CLOSED_OPTION) {
			return "no choice";
		}
		return options[choice];
	}
	
	/**
	* gets time control and color depending on if they are playing against the engine
	* @param engineOpponent true if playing against an engine
	* @return GameSettings describing gui launch/startup options. If canceled returns null.
	*/
	public static GameSettings getGameSettings(boolean engineOpponent) {
		String[] timeOptions = {};
		if (engineOpponent) {
			timeOptions = new String[] {"3 Min", "5 Min", "10 Min"};
		} else {
			timeOptions = new String[] {"None", "3 Min", "5 Min", "10 Min"};
		}

		String[] colorOptions = {"White", "Black"};

		JComboBox<String> colorDD = new JComboBox<>(colorOptions);
		JComboBox<String> timeDD = new JComboBox<>(timeOptions);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(new JLabel("Select the color you play as"));
		panel.add(colorDD);
		panel.add(new JLabel("Select a time control"));
		panel.add(timeDD);

		int result = JOptionPane.showConfirmDialog(null, panel, "Choose game setup",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			GameSettings chosen = evaluateDropDowns(engineOpponent, colorDD, timeDD);
			return chosen;
		}
		return null;
	}

	/**
	* Converts drop down selections into useful game settings
	* @param engineOpponent if playing vs engine
	* @param colorDD color dropdown
	* @param timeDD time control dropdown
	* @return GameSettings
	*/
	private static GameSettings evaluateDropDowns(boolean engineOpponent, JComboBox<String> colorDD, JComboBox<String> timeDD) {
		String color = (String) colorDD.getSelectedItem();
		String timeControl = (String) timeDD.getSelectedItem();

		Color selectedColor = Color.WHITE;
		long millisTime = 0L;

		switch (color) {
			case "White":
				selectedColor = Color.WHITE;
				break;
			case "Black":
				selectedColor = Color.BLACK;
				break;
		}

		boolean useTimer = true;

		switch (timeControl) {
			case "3 Min":
				millisTime = 180_000;
				break;
			case "5 Min":
				millisTime = 300_000;
				break;
			case "10 Min":
				millisTime = 600_000;
				break;
			case "None":
				millisTime = -1;
				useTimer = false;
				break;
		}

		GameSettings chosen = new GameSettings(useTimer, millisTime, selectedColor, engineOpponent);
		return chosen;
	}

}
