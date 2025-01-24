package userFeatures.commands.universal;


import userFeatures.EngineState;
import userFeatures.commands.Command;

public class Help implements Command {

    public Help() {
    }

    @Override
    public void execute(String[] arguments) {
        String helpInstructions =
            "Command Usage and results:\n" +
            "Universal Commands:\n" +
            "Initial Commands:\n" +
            "UCI Commands:\n"
            ;
        System.out.print(helpInstructions);
    }
}
