package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class SetPosition implements Command {
    public EngineState engineState;

    public SetPosition(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
