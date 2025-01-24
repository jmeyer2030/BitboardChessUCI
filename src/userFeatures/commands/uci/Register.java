package userFeatures.commands.uci;

import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Register implements Command {
    public EngineState engineState;

    public Register(EngineState engineState) {
        this.engineState = engineState;
    }

    @Override
    public void execute(String[] arguments) {
        System.out.print("register later\n");
    }
}
