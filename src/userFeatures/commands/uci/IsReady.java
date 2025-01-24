package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class IsReady implements Command {
    public EngineState engineState;

    public IsReady(EngineState engineState) {
        this.engineState = engineState;

    }

    @Override
    public void execute(String[] arguments) {

    }
}
