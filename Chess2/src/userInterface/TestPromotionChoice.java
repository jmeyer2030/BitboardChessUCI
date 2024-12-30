package userInterface;

import board.Color;

import javax.swing.*;

public class TestPromotionChoice {
    public static void main(String[] args) {
         System.out.println(getPromotionPiece());
         System.out.println(getGameSettings(true).playerColor);
    }
    public static String getPromotionPiece() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                null, //Parent Component
                "Choose your promotion piece:",
                "Promotion Dialog",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.CLOSED_OPTION) {
            return "Queen"; // Default to Queen if the user closes the dialog
        }
        return options[choice];
    }


    public static String gameType() {
        String[] options = {"Play against Engine", "Play against Self"};
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

    public static GameSettings getGameSettings(boolean engineOpponent) {
        String[] colorOptions = {"White", "Black"};
        String[] timeOptions = {"3 Min", "5 Min", "10 Min"};

        JComboBox<String> colorDD = new JComboBox<>(colorOptions);
        JComboBox<String> timeDD = new JComboBox<>(timeOptions);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Select the color you play as"));
        panel.add(colorDD);
        panel.add(new JLabel("Select a time control"));
        panel.add(timeDD);

        int result = JOptionPane.showConfirmDialog(null, panel, "Against Engine Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
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
            }


            GameSettings chosen = new GameSettings(true, millisTime, selectedColor, engineOpponent);

            return chosen;
        }

        return null;

    }
}
