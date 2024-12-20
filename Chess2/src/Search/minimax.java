package Search;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.*;
import board.Move;
import board.MoveType;
import board.Position;
import moveGeneration.MoveGenerator;
import board.Color;

import java.util.List;
import java.util.stream.Collectors;

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

        System.out.println("Evaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove.toString() + ", " + searchResults.getLast().bestMove.moveType);

        return searchResults.get(searchResults.size() - 1);
    }


    public static MoveValue minimax(Position position, boolean isMaximizingPlayer, int depth, int alpha, int beta) {
        if (depth == 0) {
            return new MoveValue(quiesce(alpha, beta, position, isMaximizingPlayer), null);
            //return new MoveValue(StaticEvaluation.evaluatePosition(position), null);
        }

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        if (children.size() == 0) {
            if (position.rule50 >= 50)
                return new MoveValue(0, null);
            if (MoveGenerator.kingInCheck(position, Color.WHITE))
                return new MoveValue(Integer.MIN_VALUE + 1000 - depth, null); //prefer a higher depth (mate earlier)
            return new MoveValue(Integer.MAX_VALUE - 1000 + depth, null);// prefer a higher depth
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



    public static int quiesce(int alpha, int beta, Position position, boolean isMaximizingPlayer) {
        // Perform static evaluation (stand pat)
        int standPat = StaticEvaluation.evaluatePosition(position);

        if (isMaximizingPlayer) {
            if (standPat >= beta) {
                return beta;
            }
            if (standPat > alpha) {
                alpha = standPat;
            }
        } else {
            if (standPat <= alpha) {
                return alpha;
            }
            if (standPat < beta) {
                beta = standPat;
            }
        }

        // Generate loud moves (captures, promotions, checks, etc.)
        List<Move> loudMoves = MoveGenerator.generateStrictlyLegal(position).stream()
                .filter(move -> move.captureType != null)
                .collect(Collectors.toList());

        // Sort moves to improve efficiency
        mVVLVA(loudMoves);

        // Evaluate each loud move
        for (Move move : loudMoves) {
            position.makeMove(move);

            // Recursive call with flipped player role
            int score = quiesce(alpha, beta, position, !isMaximizingPlayer);

            position.unMakeMove(move);

            if (isMaximizingPlayer) {
                if (score >= beta) {
                    return beta; // Fail-hard beta cutoff
                }
                if (score > alpha) {
                    alpha = score; // Update alpha
                }
            } else {
                if (score <= alpha) {
                    return alpha; // Fail-hard alpha cutoff
                }
                if (score < beta) {
                    beta = score; // Update beta
                }
            }
        }

        return isMaximizingPlayer ? alpha : beta; // Return best score for the current player
    }


    public void moveOrder(List<Move> list) {
   //     list.sort();
    }

    // movst valuable victim/ least valuable agressor
    public static void mVVLVA(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -(StaticEvaluation.evaluateExchange(move))));
    }



}