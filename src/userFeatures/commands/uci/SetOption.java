package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class SetOption implements Command {
    public EngineState engineState;

    public SetOption(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}

