package com.jmeyer2030.driftwood.userfeatures.commands.uci;

import com.jmeyer2030.driftwood.search.Ponder;
import com.jmeyer2030.driftwood.userfeatures.ChessEngine;
import com.jmeyer2030.driftwood.userfeatures.commands.Command;

public class Stop implements Command {
    public ChessEngine chessEngine;

    public Stop(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Override
    public void execute(String[] arguments) {
        if (Ponder.isPondering()) {
            Ponder.stopPondering();
        }
    }
}
