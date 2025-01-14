package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.concurrent.*;
import board.Move;
import board.MoveType;
import board.Position;
import moveGeneration.MoveGenerator;
import zobrist.HashTables;
import zobrist.Hashing;

import java.util.List;
import java.util.stream.Collectors;

import static zobrist.HashTables.decrementThreeFold;
import static zobrist.HashTables.incrementThreeFold;

/*
add a stack that stores moves explored in negamax.
add a table that stores zobristkeys, number of times position reached
update the table after making and umaking

in negamax, syncronize making a move, adding to position table, and adding it to the stack, as well as unmaking and popping.

then after negamax is run, pop all from the stack, unmake them, and remove from the position table.


*/

public class Search {

    // Values representing very high scores minimizing risk of over/underflow
    public static final int POSINFINITY = 100_000_000;
    public static final int NEGINFINITY = -100_000_000;

    // A stack that stores moves in case of negamax thread interruption
    public static final Stack<Move> moveStack = new Stack<Move>();

    /**
    * Class representing a move and evaluation
    */
    public static class MoveValue {
        public int value;
        public Move bestMove;

        public MoveValue(int value, Move bestMove) {
            this.value = value;
            this.bestMove = bestMove;
        }
    }

    /**
    *
    */
    public static MoveValue iterativeDeepening(Position position, long limitMillis) {
        // Create a list representing the best moves generated at each depth
        List<MoveValue> searchResults = new ArrayList<>();

        // Store the current time so we know when to stop searching
        long start = System.currentTimeMillis();


        // Initialize the initial search depth
        int depth = 0;

        // Create a new thread to run the search on
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Iteratively deepen the search until time runs out

        while (System.currentTimeMillis() - start < limitMillis) {

            Position copy = new Position(position);

            depth++;
            int finalDepth = depth;

            Callable<MoveValue> task = () -> {
               try {
                   return negamax(NEGINFINITY, POSINFINITY, finalDepth, position);
               } catch (InterruptedException e) {
                   System.out.println("Negamax was interrupted.");
                   throw e;
               }
            };

            Future<MoveValue> future = executor.submit(task);

            try {
                MoveValue result = future.get(limitMillis, TimeUnit.MILLISECONDS); // Timeout in ms, should throw a timeout exception
                searchResults.add(result);
                System.out.println("\nDepth: " + depth + "\nEvaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove.toString());
            } catch (TimeoutException te) {
                System.out.println("Function timed out!");
                future.cancel(true); // Tells the thread that it was interrupted

                try {
                    future.get(); // Waits for the thread to complete or throw an exception
                } catch (CancellationException ce) { // This is expected
                    System.out.println("Task Canceled.");
                } catch (Exception e) {
                    throw new RuntimeException("An unexpected error has occurred");
                }

                System.out.println("ready to remove from stack!");
                while (!moveStack.isEmpty()) { // Unmake moves from copy, and remove items from the position hash table
                    System.out.println("unmaking moves, stack size: " + moveStack.size());
                    decrementThreeFold(Hashing.computeZobrist(copy));
                    copy.unMakeMove(moveStack.pop());
                }

                //System.out.println(position.equals(copy));
                break; //Exit the search loop
            } catch (ExecutionException | InterruptedException e) {
                 // Get the cause of the exception
                Throwable cause = e.getCause();
                if (cause instanceof NullPointerException) {
                    System.out.println("NullPointerException caught: " + cause.getMessage());
                    cause.printStackTrace(); // Print the stack trace of the NullPointerException
                } else {
                    System.out.println("Other exception caught: " + cause);
                }
            }
        }

        executor.shutdown();

        System.out.println("Evaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove.toString() + ", " + searchResults.getLast().bestMove.moveType);

        return searchResults.get(searchResults.size() - 1);
    }

    public static MoveValue negamax(int alpha, int beta, int depthLeft, Position position) throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }
        int alphaOrig = alpha;

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);

        GameStatus status = gameStatus(children.size(), position);

        switch (status) {
            case ONGOING :
                break;
            case WHITE_WIN:
            case BLACK_WIN:
                return new MoveValue(NEGINFINITY - depthLeft, null);// prefer a higher depth
            case STALEMATE:
            case REPETITION:
                System.out.println("repetition detected!");
            case RULE50:
                return new MoveValue(0, null);
            default:
        }

        if (depthLeft == 0) {
            return new MoveValue(quiescenceSearch(alpha, beta, position), null);
        }

        long hash = Hashing.computeZobrist(position);
        HashTables.TTElement ttEntry = HashTables.getTranspositionElement(hash);

        if (ttEntry != null && ttEntry.zobristHash == hash && ttEntry.depth >= depthLeft) {
            if (ttEntry.nodeType == NodeType.EXACT) {
                return new MoveValue(ttEntry.score, ttEntry.bestMove);
            } else if (ttEntry.nodeType == NodeType.LOWER_BOUND) {
                alpha = Math.max(alpha, ttEntry.score);
            } else {
                beta = Math.min(beta, ttEntry.score);
            }
            if (alpha > beta) {
                return new MoveValue(ttEntry.score, ttEntry.bestMove);
            }
        }

        if (position.rule50 >= 50)
            return new MoveValue(0, null);

        moveOrder(children, hash);

        MoveValue bestMoveValue = new MoveValue(Integer.MIN_VALUE, null);

        for (Move move : children) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            position.makeMove(move);
            moveStack.push(move);
            incrementThreeFold(Hashing.computeZobrist(position));

            int score = -negamax(-beta, -alpha, depthLeft - 1, position).value;

            decrementThreeFold(Hashing.computeZobrist(position));
            position.unMakeMove(move);
            moveStack.pop();

            // Update best move when score is better
            if (score > bestMoveValue.value) {
                bestMoveValue.value = score;
                bestMoveValue.bestMove = move;
            }

            // Update alpha when score is better
            if (score > alpha) {
                alpha = score;
            }
            // Prune when alpha >= beta
            if (alpha >= beta) {
                break;
            }
        }

        ttEntry = new HashTables.TTElement();
        ttEntry.score = bestMoveValue.value;
        ttEntry.depth = depthLeft;
        ttEntry.age = position.rule50;
        ttEntry.zobristHash = hash;
        ttEntry.bestMove = bestMoveValue.bestMove;

        if (bestMoveValue.value <= alphaOrig) {
            ttEntry.nodeType = NodeType.UPPER_BOUND;
        } else if (bestMoveValue.value >= beta) {
            ttEntry.nodeType = NodeType.LOWER_BOUND;
        } else {
            ttEntry.nodeType = NodeType.EXACT;
        }

        HashTables.transpositionTable[HashTables.getTranspositionIndex(hash)] = ttEntry;

        return bestMoveValue;
    }

    public static GameStatus gameStatus(int moveListSize, Position position) {
        if (moveListSize == 0) {
            if (position.whiteInCheck) {
                return GameStatus.BLACK_WIN;
            } else if (position.blackInCheck) {
                return GameStatus.WHITE_WIN;
            } else {
                return GameStatus.STALEMATE;
            }
        } else if (position.rule50 >= 50) {
            return GameStatus.RULE50;
        } else if (HashTables.repetitionsExceeded(Hashing.computeZobrist(position))) {
            return GameStatus.REPETITION;
        } else {
            return GameStatus.ONGOING;
        }
    }


    public static synchronized int quiescenceSearch(int alpha, int beta, Position position) {
        int standPat = StaticEvaluation.negamaxEvaluatePosition(position);
        int bestValue = standPat;
        if (standPat >= beta)
            return standPat;
        if (alpha < standPat)
            alpha = standPat;

        // Generate loud moves (captures, promotions, checks, etc.)
        List<Move> loudMoves = MoveGenerator.generateStrictlyLegal(position).stream()
                .filter(move -> move.captureType != null)
                .collect(Collectors.toList());

        for (Move move : loudMoves) {
            position.makeMove(move);
            int score = -quiescenceSearch(-beta, -alpha, position);
            position.unMakeMove(move);

            if (score >= beta)
                return score;
            if (score > bestValue)
                bestValue = score;
            if (score > alpha)
                alpha = score;
        }
        return bestValue;
    }


/*
 * Move ordering with selection sort? e.g. choose
 */
    public static void moveOrder(List<Move> list, long zobristHash) {
        list.sort(Comparator.comparingInt(move -> -moveValue(move, zobristHash)));
    }
    /**
    * Returns an integer value for a move used for sorting.
    */
    public static int moveValue(Move move, long zobristHash) {
       int value = 0;

       HashTables.TTElement element = HashTables.getTranspositionElement(zobristHash);
       if (element != null) {
           if (move.equals(element.bestMove)) {
               value+= 10_000_000;
           }
       }

       if (move.moveType == MoveType.PROMOTION) {
           value += 500_000;
       }

       if (move.resultWhiteInCheck || move.resultBlackInCheck) {
           value += 1_000_000;
       }

       if (move.moveType == MoveType.CAPTURE) {
           assert move.captureType != null;
           value += 100_000 + StaticEvaluation.evaluateExchange(move);
       }

       return value;
    }

    // most valuable victim/ least valuable agressor
    public static void mVVLVA(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -(StaticEvaluation.evaluateExchange(move))));
    }

}