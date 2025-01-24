import userFeatures.CommandHandler;
import userFeatures.EngineState;

public class Main {
    public static void main(String[] args) {
        EngineState engineState = new EngineState();
        CommandHandler handler = new CommandHandler();
        handler.inputLoop();
    }
}
