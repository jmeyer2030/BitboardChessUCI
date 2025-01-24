package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Go implements Command {
    public EngineState engineState;

    public Go(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
