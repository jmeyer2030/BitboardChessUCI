package userFeatures.commands.initial;

import userFeatures.CommandHandler;
import userFeatures.EngineState;
import userFeatures.commands.Command;

public class UCIMode implements Command {

    public EngineState engineState;
    public CommandHandler handler;

    public UCIMode(EngineState engineState, CommandHandler handler) {
        this.engineState = engineState;
        this.handler = handler;
    }

    /**
    * Sets commandMode to UCI
    * Sets handler to accept UCI commands
    * Prints ID
    * Sends uciok, confirming that the engine is ready for uci
    */
    @Override
    public void execute(String[] arguments) {
        engineState.setCommandMode(EngineState.CommandMode.UCI);
        handler.acceptUCICommands();
        System.out.print(engineState.getID());
        System.out.print("uciok\n");
    }
}
