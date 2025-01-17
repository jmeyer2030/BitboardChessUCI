package system;

import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;

import java.util.Objects;
import java.util.Stack;
import java.util.logging.Logger;

/*
* This class aims to monitor search to provide detailed insights about search results.
*
*/
public class SearchMonitor {

    public Stack<BoardMovePair> searchStack;
    public Logger logger;
    public Position initialPosition;

    public SearchMonitor(Position initialPosition) {
        try {
            initialPosition.validPosition();
        } catch(InvalidPositionException ipe) {
            throw new RuntimeException("Search monitor must only be called on a valid Position!");
        }
        this.initialPosition = new Position(initialPosition);
        this.logger = Logging.getLogger(SearchMonitor.class);
    }

    public void addPair(Move move, Position position) {
        searchStack.push(new BoardMovePair(move, position));
    }

    public String searchToString() {
        String str = "";
        for (BoardMovePair pair : searchStack) {
            str += pair;
        }
        return str;
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
            String str = this.move + "\n" + position.getDisplayBoard();
            return str;
        }
    }


}
