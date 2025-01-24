package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Quit implements Command {
    public EngineState engineState;

    public Quit(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
