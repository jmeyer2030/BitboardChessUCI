package userFeatures;

import board.Position;

public class EngineState {
    // Enums
    public enum CommandMode { DEFAULT, UCI }


    // Engine Information:
    public final String ENGINE_NAME = "DriftWood 1.0";
    public final String AUTHOR = "Joshua Meyer";

    // Engine Settings:
    public CommandMode commandMode;
    public boolean debugActive;
    public boolean isReady;

    // Search and Position objects:
    private Position position;

    public void setCommandMode(CommandMode commandMode) {
        this.commandMode = commandMode;
    }

    public String getID() {
        return "id name " + ENGINE_NAME + "\nid author " + AUTHOR + "\n";
    }

}
