package userInterface;

import board.Color;

public class GameSettings {
    public final long millisTime;
    public final Color playerColor;
    public final boolean engineOpponent;

    public GameSettings(long millisTime, Color playerColor, boolean engineOpponent) {
        this.millisTime = millisTime;
        this.playerColor = playerColor;
        this.engineOpponent = engineOpponent;
    }

    public void print() {
        System.out.println("millisTime: " + millisTime);
        System.out.println("playerColor: " + playerColor);
        System.out.println("engineOpponent: " + engineOpponent);
    }
}
