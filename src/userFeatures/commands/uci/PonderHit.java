package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class PonderHit implements Command {
    public EngineState engineState;

    public PonderHit(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
