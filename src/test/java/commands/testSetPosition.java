package commands;

import board.MoveEncoding;
import board.Position;
import board.PositionState;
import engine.search.Search;
import moveGeneration.MoveGenerator;
import org.junit.jupiter.api.Test;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import userFeatures.commands.uci.SetPosition;
import userFeatures.commands.uci.UCINewGame;
import zobrist.ThreeFoldTable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


    @Test
    public void testExecute_WhenNot50MoveRule2_DoesNotDraw() {
        ChessEngine chessEngine = new ChessEngine();

        Command newGameCommand = new UCINewGame(chessEngine);
        Command positionCommand = new SetPosition(chessEngine);

        String cmd = "startpos moves d2d4 d7d5 e2e3 c8f5 c2c4 e7e6 b1c3 g8f6 g1f3 f8b4 f3h4 f5g4 d1a4 b8c6 h2h3 f6e4 h3g4 e4c3 a4c2 c3a2 c1d2 b4d2 c2d2 a2b4 h4f3 a7a5 g4g5 d8e7 c4d5 e6d5 f1b5 e8c8 a1c1 c8b8 b5c6 b4c6 c1c6 b7c6 e1g1 e7b4 d2b4 a5b4 f3e5 c6c5 e5f7 c5c4 f2f4 c7c5 g1f2 c5d4 e3d4 b8c7 f7d8 c7d8 f4f5 h7h5 f2f3 h8f8 g2g4 d8d7 f1a1 h5g4 f3g4 d7c6 a1a6 c6b5 a6a7 c4c3 b2c3 b4c3 a7c7 b5b4 g5g6 f8g8 g4f3 b4b3 f3e2 b3c2 c7e7 g8f8 e7e5 c2b2 e5d5 c3c2 d5b5 b2c1 b5c5 f8h8 f5f6 g7f6 g6g7 h8b8 d4d5 c1b1 e2d2 b8b2 d2e3 b2b8 e3d2 b8b2";
        String[] cmdArgs = cmd.split(" ");

        newGameCommand.execute(null);
        positionCommand.execute(cmdArgs);

        int hmc = chessEngine.positionState.position.halfMoveCount;
        boolean isDraw  = chessEngine.positionState.threeFoldTable.positionDrawn(chessEngine.positionState.position.zobristHash);
        boolean isRepeated  = chessEngine.positionState.threeFoldTable.positionRepeated(chessEngine.positionState.position.zobristHash);

        System.out.println(hmc);
        System.out.println(isDraw);
        System.out.println(isRepeated);
    }

    @Test
    public void testExecute_RepeatTest_DoesNotDraw() {
        ChessEngine chessEngine = new ChessEngine();
        Command newGameCommand = new UCINewGame(chessEngine);
        Command positionCommand = new SetPosition(chessEngine);

        newGameCommand.execute(null);

        String cmd = "startpos moves d2d4 g8f6 c2c4 c7c5 d4d5 e7e6 b1c3 e6d5 c4d5 d7d6 e2e4 a7a6 a2a4 g7g6 g1f3 f8g7 f1e2 e8g8 f3d2 f8e8 e1g1 b8d7 h2h3 b7b6 f2f4 c5c4 e2c4 d7c5 d1f3 c8d7 f1e1 f6h5 d2b3 d8h4 c1d2 c5b3 c4b3 g7d4 g1h1 g6g5 c3e2 g5g4 f3d3 d4f2 e1g1 a8c8 d2e1 g4g3 e1f2 g3f2 g1f1 d7g4 e2d4 h5f4 d3e3 g4h3 g2h3 e8e4 e3f3 e4d4 f1f2 h4h3 f3h3 f4h3 f2f6 d4b4 a1a3 c8d8 b3d1 h3g5 a3g3 g8g7 f6f1 f7f6 g3c3 g7g6 d1c2 g5e4 c3g3 g6f7 g3f3 f7g7 c2e4 b4e4 f3f6 e4a4 f6f7 g7g8 f7b7 a4h4 h1g1 h4g4 g1h2 b6b5 f1f7 h7h5 f7f6 b5b4 h2h3 a6a5 f6h6 d8f8 h6d6 f8f3 h3h2 a5a4 d6a6 f3f7 b7b8 f7f8 b8b7 f8f2 h2h3 b4b3 d5d6 f2f3 h3h2 f3f8 h2h3 g4d4 a6a7 d4g4 h3h2 f8e8 h2h3 e8e3 h3h2 e3e2 h2h3 e2e3"; // e2e3 illegal at the end
        String[] cmdArgs = cmd.split(" ");
        positionCommand.execute(cmdArgs);

        PositionState positionState = chessEngine.positionState;
        Position position = chessEngine.positionState.position;


        // Test that the move in this position is being generated correctly
        List<String> moveList = MoveGenerator.debugGenerateMoveList(position);
        assertEquals(1, moveList.size());
        assertTrue(moveList.get(0).equals("h3h2"));

        // Test that iterative deepening gets the only move
        Search.MoveValue result = Search.iterativeDeepening(position, 10_000, positionState);
        String lanBestMove = MoveEncoding.getLAN(result.bestMove);
        assertEquals("h3h2", lanBestMove);
    }


    @Test
    public void testExecute_RepeatTest2_DoesNotDraw() {
        ChessEngine chessEngine = new ChessEngine();
        Command newGameCommand = new UCINewGame(chessEngine);
        Command positionCommand = new SetPosition(chessEngine);

        newGameCommand.execute(null);

        String cmd = "startpos moves d2d4 g8f6 c2c4 c7c5 d4d5 e7e6 b1c3 e6d5 c4d5 d7d6 e2e4 a7a6 a2a4 g7g6 g1f3 f8g7 f1e2 e8g8 f3d2 f8e8 e1g1 b8d7 h2h3 b7b6 f2f4 c5c4 e2c4 d7c5 d1f3 c8d7 f1e1 f6h5 d2b3 d8h4 c1d2 c5b3 c4b3 g7d4 g1h1 g6g5 c3e2 g5g4 f3d3 d4f2 e1g1 a8c8 d2e1 g4g3 e1f2 g3f2 g1f1 d7g4 e2d4 h5f4 d3e3 g4h3 g2h3 e8e4 e3f3 e4d4 f1f2 h4h3 f3h3 f4h3 f2f6 d4b4 a1a3 c8d8 b3d1 h3g5 a3g3 g8g7 f6f1 f7f6 g3c3 g7g6 d1c2 g5e4 c3g3 g6f7 g3f3 f7g7 c2e4 b4e4 f3f6 e4a4 f6f7 g7g8 f7b7 a4h4 h1g1 h4g4 g1h2 b6b5 f1f7 h7h5 f7f6 b5b4 h2h3 a6a5 f6h6 d8f8 h6d6 f8f3 h3h2 a5a4 d6a6 f3f7 b7b8 f7f8 b8b7 f8f2 h2h3 b4b3 d5d6 f2f3 h3h2 f3f8 h2h3 g4d4 a6a7 d4g4 h3h2 f8e8 h2h3 e8e3 h3h2 e3e2 h2h3 e2e3 h3h2 e3e2"; // e2e3 illegal at the end
        String[] cmdArgs = cmd.split(" ");
        positionCommand.execute(cmdArgs);

        PositionState positionState = chessEngine.positionState;
        Position position = chessEngine.positionState.position;


        // Test that the move in this position is being generated correctly
        List<String> moveList = MoveGenerator.debugGenerateMoveList(position);
        assertEquals(2, moveList.size());
        //assertTrue(moveList.get(0).equals("h3h2"));

        // Test that iterative deepening gets the only move
        Search.MoveValue result = Search.iterativeDeepening(position, 1_000, positionState);
        String lanBestMove = MoveEncoding.getLAN(result.bestMove);
        assertTrue("h2h3".equals(lanBestMove) || "h2h1".equals(lanBestMove));
    }

    @Test
    public void testExecute_RepeatTest3_DoesNotDraw() {
        ChessEngine chessEngine = new ChessEngine();
        Command newGameCommand = new UCINewGame(chessEngine);
        Command positionCommand = new SetPosition(chessEngine);

        newGameCommand.execute(null);

        String cmd = "startpos moves e2e4 d7d6 d2d4 g8f6 b1c3 g7g6 f2f4 f8g7 g1f3 e8g8 f1d3 b8c6 e1g1 f6h5 c1e3 c8g4 e4e5 d6e5 d4e5 f7f6 h2h3 g4f3 d1f3 f6e5 f4f5 e7e6 g2g4 c6d4 e3d4 d8d4 g1h1 e6f5 g4h5 e5e4 c3e4 f5e4 f3e4 d4e4 d3e4 g7b2 f1f8 g8f8 a1f1 f8g7 h5g6 h7g6 f1g1 c7c6 g1g6 g7f7 h1g2 a8h8 a2a4 b2f6 g2g3 a7a5 g6g4 f7e7 h3h4 e7d6 e4g6 b7b5 h4h5 f6c3 g3f3 b5a4 g4a4 c3b4 f3e2 d6c5 e2d3 h8d8 d3e2 c5b5 a4a1 a5a4 h5h6 b4c3 a1b1 b5c5 h6h7 a4a3 g6f7 d8d2 e2e3 d2h2 f7g8 c3b2 e3d3 h2h3 d3d2 a3a2 g8a2 b2c3 d2e2 h3h7 b1f1 c3a5 e2d1 c5b6 d1c1 a5c3 c1b1 h7h8 a2b3 c3e5 b3c4 e5b8 c4d3 b8d6 b1b2 h8f8 f1f8 d6f8 c2c3 f8d6 b2c2 b6c7 d3h7 d6e5 h7g6 c7b6 g6e8 e5c3 c2c3 b6c7 c3d4 c7b6 e8g6 b6b5 d4c3 b5b6 c3d3 b6b5 d3c2 b5b4 g6h5 b4a5 c2c3 a5b5 c3d3 b5c5 d3c2 c5b6 h5g6 b6a5 c2b3 a5b5 g6f7 b5c5 b3c3 c5b5 c3d4 b5b4 d4e4 b4c3 e4e5 c6c5 e5e4 c3c2 f7c4 c2c3 c4g8 c3d2 g8a2 d2c2 e4e3 c2c3 a2g8 c3c2 g8c4 c2c3 c4e6 c3b4 e3d2 b4b5 d2c3 b5c6 e6f7 c6b5 c3b3 b5b6 f7g6 b6a6 b3a4 a6b6 g6d3 b6c6 d3c2 c6d5 a4b3 d5c6 b3a3 c6d6 c2b3 d6c7 b3d1 c7b6 d1c2 b6c6 c2g6 c6d6 g6f7 d6c7 f7g6 c7d6 g6f7 d6c7 f7g6";
        String[] cmdArgs = cmd.split(" ");
        positionCommand.execute(cmdArgs);

        PositionState positionState = chessEngine.positionState;
        Position position = chessEngine.positionState.position;

        //List<ThreeFoldTable.ThreeFoldElement> list = positionState.threeFoldTable.searchedPositions;
        /*
        for (int i = 0; i < list.size(); i++) {
            ThreeFoldTable.ThreeFoldElement item = list.get(i);
            //System.out.println(i + " | zobrist: " + item.zobristHash + " | move : " + MoveEncoding.getLAN(item.moveAtPosition));
        }
        */

        // Test that the move in this position is being generated correctly
        List<String> moveList = MoveGenerator.debugGenerateMoveList(position);
        moveList.stream().forEach(item -> System.out.println(item));
        //assertEquals(2, moveList.size());
        //assertTrue(moveList.get(0).equals("h3h2"));

        // Test that iterative deepening gets the only move
        Search.MoveValue result = Search.iterativeDeepening(position, 1_000, positionState);
        String lanBestMove = MoveEncoding.getLAN(result.bestMove);
        System.out.println(lanBestMove);
        System.out.println(position.halfMoveCount);
        //assertTrue("h2h3".equals(lanBestMove) || "h2h1".equals(lanBestMove));

    }

    /*
    * The issue is that naturally (because the next move hasn't been played), the "moveAtPostion" is 0. Then, in search, we see that the position is repeated and just return that.
    *
    *
    */

}
