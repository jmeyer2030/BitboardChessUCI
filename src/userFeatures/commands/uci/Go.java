package userFeatures.commands.uci;

import board.MoveEncoding;
import engine.search.Ponder;
import engine.search.Search;
import engine.TimeManagement;
import moveGeneration.MoveGenerator;
import userFeatures.ChessEngine;
import userFeatures.commands.Command;
import board.Position;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Go implements Command {

    // List of subcommands, e.g. ponder, wtime, btime, etc.
    public HashMap<String, String> subCommands;

    // List of moves if "searchmoves" is used
    public List<String> searchMoves;

    public ChessEngine chessEngine;

    @SuppressWarnings("SpellCheckingInspection")
    public Go(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;

        // initialize starting fields
        this.startingParams();
    }

    /**
     * Executes the command
     */
    @Override
    public void execute(String[] arguments) {
        // Add arguments to fields
        this.fillSearchTerms(arguments);

        this.executeCommand();

        // Reset fields to initial state
        this.startingParams();
    }

    /**
     * Parses arguments so that they can be used for search
     *
     * @param arguments from the input
     */
    public void fillSearchTerms(String[] arguments) {
        String currentSubCommand = null;
        for (String argument : arguments) {
            if (subCommands.containsKey(argument)) { // If we are currently on a subcommand
                currentSubCommand = argument;

                // Handle "infinite" with 0 parameters
                if (argument.equals("infinite")) {
                    subCommands.put("infinite", "true");
                }

                if (argument.equals("ponder")) {
                    subCommands.put("ponder", "true");
                }
            } else if ("searchmoves".equals(currentSubCommand)) {
                searchMoves.add(argument);
            } else {
                subCommands.put(currentSubCommand, argument);
            }
        }
    }

    /**
     * Resets internally stored command parameters
     * For use on initialization and after command is executed
     */
    private void startingParams() {
        this.searchMoves = new LinkedList<String>();
        this.subCommands = new HashMap<String, String>();

        subCommands.put("searchmoves", null);
        subCommands.put("ponder", null);
        subCommands.put("wtime", null);
        subCommands.put("btime", null);
        subCommands.put("winc", null);
        subCommands.put("binc", null);
        subCommands.put("movestogo", null);
        subCommands.put("depth", null);
        subCommands.put("nodes", null);
        subCommands.put("mate", null);
        subCommands.put("movetime", null);
        subCommands.put("infinite", null);
    }

    public void executeCommand() {
        if ("true".equals(subCommands.get("ponder"))) {
            executePonder();
        } else {
            executeSearch();
        }
    }

    /**
     * Executes the search based on stored parameters
     */
    public void executeSearch() {
        //chessEngine.positionState.firstNonMove = 0;

        // Apply move to chessEngine's position state position
        applySearchMoves();
        Position position = chessEngine.positionState.position;

        // Get the time we are allocating to the search
        String activeTimeStr;
        if (position.activePlayer == 0) {
            activeTimeStr = subCommands.get("wtime");
        } else {
            activeTimeStr = subCommands.get("btime");
        }
        long time = Long.parseLong(activeTimeStr);
        long computeTime = TimeManagement.millisForMove(time, 0);

        System.out.println("Beginning search:");
        Search.MoveValue moveValue = Search.iterativeDeepening(position, computeTime, chessEngine.positionState);

        System.out.println("bestmove " + MoveEncoding.getLAN(moveValue.bestMove) + " ponder " + MoveEncoding.getLAN(chessEngine.positionState.pvTable.getBestResponse()));
    }

    public void executePonder() {
        Ponder.startPondering(chessEngine.positionState.position, chessEngine.positionState);
    }

    public void applySearchMoves() {
        Position position = chessEngine.positionState.position;
        for (String lan : searchMoves) {
            int move = MoveGenerator.getMoveFromLAN(lan, position, chessEngine.positionState.moveBuffer);
            chessEngine.positionState.applyMove(move);
        }
    }


    /*public void getBestMove() {
        Position position = chessEngine.positionState.position;
        Position copy = new Position(position);

        TTElement bestElement = chessEngine.positionState.tt.getElement(position.zobristHash);
        if (bestElement == null) {
            System.out.println("Best element not found in the transposition table!");
        }

        Move bestMove = null;
        Move refutationMove = null;

        if (bestElement != null && bestElement.bestMove() != null) {
            bestMove = bestElement.bestMove();

        }
        copy.makeMove(bestMove);

        Move bestResponse = chessEngine.positionState.tt.getElement(copy.zobristHash).bestMove();
    }*/
}
