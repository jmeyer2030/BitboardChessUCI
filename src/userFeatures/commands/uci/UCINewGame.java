package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class UCINewGame implements Command {
    public EngineState engineState;

    public UCINewGame(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {

    }
}
