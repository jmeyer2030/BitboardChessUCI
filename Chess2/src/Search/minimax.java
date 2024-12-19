package Search;



import java.util.ArrayList;
import java.util.concurrent.*;
import board.Move;
import board.Position;
import moveGeneration.MoveGenerator;
import board.Color;

import java.util.List;

public class minimax {

    public static class MoveValue {
        public int value;
        public Move bestMove;

        public MoveValue(int value, Move bestMove) {
            this.value = value;
            this.bestMove = bestMove;
        }
    }


    public static MoveValue iterativeDeepening(Position position, long limitMillis) {
        boolean isMaximizingPlayer = position.activePlayer == Color.WHITE;
        List<MoveValue> searchResults = new ArrayList<MoveValue>();
        long start = System.currentTimeMillis();

        int depth = 0;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        while (System.currentTimeMillis() - start < limitMillis) {
            depth++;
            int finalDepth = depth;

            Position copy = new Position(position);

            Callable<MoveValue> task = () -> {
               MoveValue result = minimax(copy, isMaximizingPlayer, finalDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
               return result;
            };

            Future<MoveValue> future = executor.submit(task);

            try {
                MoveValue result = future.get(limitMillis, TimeUnit.MILLISECONDS); // Timeout in seconds
                searchResults.add(result);
            } catch (TimeoutException e) {
                System.out.println("Function timed out!");
                future.cancel(true); // Attempts to terminate the thread
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Depth: " + depth + "\nEvaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove.toString());
        }
        executor.shutdown();

        return searchResults.getLast();
    }


    public static MoveValue minimax(Position position, boolean isMaximizingPlayer, int depth, int alpha, int beta) {
        if (depth == 0) {
            return new MoveValue(StaticEvaluation.evaluatePosition(position), null);
        }

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        if (children.size() == 0) {
            if (position.rule50 >= 50)
                return new MoveValue(0, null);
            if (MoveGenerator.kingInCheck(position, Color.WHITE))
                return new MoveValue(Integer.MIN_VALUE, null);
            return new MoveValue(Integer.MAX_VALUE, null);
        }

        MoveValue bestMoveValue;

        if (isMaximizingPlayer) {
            bestMoveValue = new MoveValue(Integer.MIN_VALUE, null);
            for (Move move : children) {
                position.makeMove(move);
                MoveValue value = minimax(position, !isMaximizingPlayer, depth - 1, alpha, beta);
                if (value.value > bestMoveValue.value) {
                    bestMoveValue.value = value.value;
                    bestMoveValue.bestMove = move;
                }
                alpha = Math.max(alpha, bestMoveValue.value);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        } else {
            bestMoveValue = new MoveValue(Integer.MAX_VALUE, null);
            for (Move move : children) {
                position.makeMove(move);
                MoveValue value = minimax(position, !isMaximizingPlayer, depth - 1, alpha, beta);
                if (value.value < bestMoveValue.value) {
                    bestMoveValue.value = value.value;
                    bestMoveValue.bestMove = move;
                }
                beta = Math.min(beta, bestMoveValue.value);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        }

        return bestMoveValue;
    }



}
/*
*
    public static MoveValue minimaxRecursion(Position position, boolean isMaximizingPlayer, int depth, int alpha, int beta) {
        if (depth == 0) {
            return new MoveValue(StaticEvaluation.evaluatePosition(position), null);
        }

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        if (children.size() == 0) {
            if (position.rule50 >= 50)
                return new MoveValue(0, null);
            if (MoveGenerator.kingInCheck(position, Color.WHITE))
                return new MoveValue(Integer.MIN_VALUE, null);
            return new MoveValue(Integer.MAX_VALUE, null);
        }

        MoveValue bestMoveValue;

        if (isMaximizingPlayer) {
            bestMoveValue = new MoveValue(Integer.MIN_VALUE, null);
            for (Move move : children) {
                position.makeMove(move);
                MoveValue value = minimaxRecursion(position, !isMaximizingPlayer, depth - 1, alpha, beta);
                if (value.value > bestMoveValue.value) {
                    bestMoveValue.value = value.value;
                    bestMoveValue.bestMove = move;
                }
                alpha = Math.max(alpha, bestMoveValue.value);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        } else {
            bestMoveValue = new MoveValue(Integer.MAX_VALUE, null);
            for (Move move : children) {
                position.makeMove(move);
                MoveValue value = minimaxRecursion(position, !isMaximizingPlayer, depth - 1, alpha, beta);
                if (value.value < bestMoveValue.value) {
                    bestMoveValue.value = value.value;
                    bestMoveValue.bestMove = move;
                }
                beta = Math.min(beta, bestMoveValue.value);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        }

        return bestMoveValue;
    }
*
*
*
* */

/*
    public static Move minimax(Position position, int depth) {
       List<Move> startingMoves = MoveGenerator.generateStrictlyLegal(position);
       boolean isMaximizingPlayer = position.activePlayer == Color.WHITE ? true : false;
       int bestMoveEval = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
       Move bestMove = null;
       for (Move move : startingMoves) {
            position.makeMove(move);
            int value = minimaxRecursion(position, isMaximizingPlayer, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (isMaximizingPlayer) {
                if (value > bestMoveEval) {
                    bestMove = move;
                    bestMoveEval = value;
                }
            } else {
                if (value < bestMoveEval) {
                    bestMove = move;
                    bestMoveEval = value;
                }
            }
            position.unMakeMove(move);
       }
       return bestMove;
    }

    public static int minimaxRecursion(Position position, boolean isMaximizingPlayer, int depth, int alpha, int beta) {
        if (depth == 0) {
            return StaticEvaluation.evaluatePosition(position);
        }

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        if (children.size() == 0) {
            if (position.rule50 >= 50)
                return 0;
            if (MoveGenerator.kingInCheck(position, Color.WHITE))
                return Integer.MIN_VALUE;
            return Integer.MAX_VALUE;
        }

        int bestVal;

        if (isMaximizingPlayer) {
            bestVal = Integer.MIN_VALUE;
            for (Move move : children) {
                position.makeMove(move);
                int value = minimaxRecursion(position, !isMaximizingPlayer, depth--, alpha, beta);
                bestVal = Math.max(alpha, bestVal);
                alpha = Math.max(value, bestVal);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        } else {
            bestVal = Integer.MAX_VALUE;
            for (Move move : children) {
                position.makeMove(move);
                int value = minimaxRecursion(position, !isMaximizingPlayer, depth--, alpha, beta);
                bestVal = Math.min(bestVal, value);
                beta = Math.min(bestVal, beta);
                position.unMakeMove(move);
                if (beta <= alpha)
                    break;
            }
        }
        return bestVal;
    }
*/