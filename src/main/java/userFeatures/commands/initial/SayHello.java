package userFeatures.commands.initial;

import userFeatures.commands.Command;

public class SayHello implements Command {
    @Override
    public void execute(String[] arguments) {
        System.out.println("Hello, World!");
    }
}
