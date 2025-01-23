package system;

import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;

import java.util.Objects;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.logging.Level;

/*
* This class aims to monitor search to provide detailed insights about search results.
* Conceptually, this class should be more responsible for logging error issues than the exception itself.
*/
public class SearchMonitor {


    public Stack<BoardMovePair> searchStack; // Stores the stack of searched positions and moves
    public Logger logger; // Displays information to the user
    public Position initialPosition; // The starting position of the search

    public SearchMonitor(Position initialPosition) {
        try {
            initialPosition.validPosition();
        } catch(InvalidPositionException ipe) {
            throw new RuntimeException("Search monitor must only be called on a valid Position!");
        }
        this.initialPosition = new Position(initialPosition);
        this.logger = Logging.getLogger(SearchMonitor.class);
        this.searchStack = new Stack<BoardMovePair>();
    }

    public void addPair(Move move, Position position) {
        searchStack.push(new BoardMovePair(move, new Position(position)));
    }

    private String searchToString() {
        String str = "\n";
        str += initialPosition.getDisplayBoard() + "\n";
        for (BoardMovePair pair : searchStack) {
            str += pair;
        }
        return str;
    }

    public void logSearchStack() {
        logger.log(Level.SEVERE, searchToString() + "end log");
    }

    public BoardMovePair popStack() {
        return searchStack.pop();
    }

    /**
    * Stores a move and the position resulting from that move
    */
    public class BoardMovePair {
        public Move move;
        public Position position;

        public BoardMovePair(Move move, Position position) {
            this.move = move;
            this.position = Objects.requireNonNull(position);
        }

        public String toString() {
            String str = this.move + "\n" + this.position.getDisplayBoard();
            return str;
        }
    }


}
