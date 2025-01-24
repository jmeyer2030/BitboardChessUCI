package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Stop implements Command {
    public EngineState engineState;

    public Stop(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
