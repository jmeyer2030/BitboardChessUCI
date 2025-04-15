package main.java.userFeatures.commands.universal;


import main.java.userFeatures.commands.Command;

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
    }
}
