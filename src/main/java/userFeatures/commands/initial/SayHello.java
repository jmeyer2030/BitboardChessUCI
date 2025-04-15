package main.java.userFeatures.commands.initial;

import main.java.userFeatures.commands.Command;

public class SayHello implements Command {
    @Override
    public void execute(String[] arguments) {
        System.out.println("Hello, World!");
    }
}
