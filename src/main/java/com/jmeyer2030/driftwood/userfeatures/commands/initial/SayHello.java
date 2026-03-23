package com.jmeyer2030.driftwood.userfeatures.commands.initial;

import com.jmeyer2030.driftwood.userfeatures.commands.Command;

public class SayHello implements Command {
    @Override
    public void execute(String[] arguments) {
        System.out.println("Hello, World!");
    }
}
