package commands;

import org.junit.jupiter.api.Test;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import userFeatures.commands.uci.SetPosition;
import userFeatures.commands.uci.UCINewGame;

public class testSetPosition {

    @Test
    public void testExecute_WhenNot50MoveRule_DoesNotDraw() {
        ChessEngine chessEngine = new ChessEngine();



        Command newGameCommand = new UCINewGame(chessEngine);
        Command positionCommand = new SetPosition(chessEngine);

        String cmd = "startpos moves e2e4 g8f6 e4e5 f6d5 d2d4 d7d6 c2c4 d5b6 e5d6 e7d6 g1f3 c7c5 b1c3 c5d4 f3d4 f8e7 f1e2 e8g8 e1g1 d6d5 c4d5 b6d5 c3d5 d8d5 e2f3 d5a5 d1e2 e7f6 c1e3 b8c6 d4c6 b7c6 f1c1 c8a6 e2c2 a8b8 a1b1 f8d8 f3e4 g7g6 h2h3 f6d4 e3f4 b8c8 b2b4 a5b6 f4g3 c6c5 b4c5 b6c5 c2d2 c5e7 c1c8 a6c8 d2f4 c8e6 a2a4 g8g7 b1b7 e7f6 a4a5 d8c8 f4f6 g7f6 g3c7 h7h5 h3h4 c8e8 b7b4 d4c3 b4b8 e8b8 c7b8 e6c4 b8a7 f6e5 f2f3 c3a5 e4c2 a5c3 a7f2 c4d5 c2a4 c3b4 f2a7 b4e7 a7f2 d5c4 g2g3 e7d6 f2e3 e5e6 g1g2 d6b4 e3b6 b4c3 g2f2 c3b2 b6e3 e6e5 a4c2 c4e6 c2d3 b2c3 f2e2 e6b3 d3b5 b3d5 e3h6 e5d6 b5a4 c3e5 e2f2 d6c5 h6d2 c5c4 f2g2 c4d4 a4c2 d4c4 c2a4 d5e6 d2h6 c4d4 g2f2 d4d3 a4b5 e6c4 b5c6 c4e6 c6b5 e6c4 b5e8 c4d5 f2g2 d3c4 h6c1 c4d3 g2h3 d3e2";
        String[] cmdArgs = cmd.split(" ");

        newGameCommand.execute(null);
        positionCommand.execute(cmdArgs);

        int hmc = chessEngine.positionState.position.halfMoveCount;

        System.out.println(hmc);
    }

}
