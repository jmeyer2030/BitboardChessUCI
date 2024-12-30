package userInterface;

import board.Color;

public class GameSettings {
    public final long millisTime;
    public final Color playerColor;
    public final boolean engineOpponent;
    public final boolean useTimer;

    public GameSettings(boolean useTimer, long millisTime, Color playerColor, boolean engineOpponent) {
        this.useTimer = useTimer;
        this.millisTime = millisTime;
        this.playerColor = playerColor;
        this.engineOpponent = engineOpponent;
    }

    public void print() {
        System.out.println("useTimer: " + useTimer);
        System.out.println("millisTime: " + millisTime);
        System.out.println("playerColor: " + playerColor);
        System.out.println("engineOpponent: " + engineOpponent);
    }
}
