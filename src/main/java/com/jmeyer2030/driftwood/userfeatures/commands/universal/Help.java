package com.jmeyer2030.driftwood.userfeatures.commands.universal;


import com.jmeyer2030.driftwood.userfeatures.commands.Command;

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
