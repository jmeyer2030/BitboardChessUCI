package engine;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.*;
import board.Move;
import board.MoveType;
import board.Position;
import moveGeneration.MoveGenerator;
import board.Color;

import java.util.List;
import java.util.stream.Collectors;

public class minimax {


    public static final int POSINFINITY = 100_000_000;
    public static final int NEGINFINITY = -100_000_000;

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
               MoveValue result = negaMax(NEGINFINITY, POSINFINITY, finalDepth, copy);
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
            //return new MoveValue(quiesce(alpha, beta, position, isMaximizingPlayer), null);
            return new MoveValue(StaticEvaluation.evaluatePosition(position), null);
        }

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        if (children.size() == 0) {
            if (position.rule50 >= 50)
                return new MoveValue(0, null);
            if (MoveGenerator.kingInCheck(position, Color.WHITE))
                return new MoveValue(NEGINFINITY + 1000 - depth, null); //prefer a higher depth (mate earlier)
            return new MoveValue(POSINFINITY - 1000 + depth, null);// prefer a higher depth
        }
        moveOrder(children);
        MoveValue bestMoveValue;

        if (isMaximizingPlayer) {
            bestMoveValue = new MoveValue(NEGINFINITY, null);
            for (Move move : children) {
                //if (depth == 6)
                //    System.out.println(move);
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

    public static MoveValue negaMax(int alpha, int beta, int depthLeft, Position position) {
        if (depthLeft == 0) {
            return new MoveValue(quiescenceSearch(alpha, beta, position), null);
            //return new MoveValue(StaticEvaluation.negamaxEvaluatePosition(position), null);
        }

        if (position.rule50 >= 50)
            return new MoveValue(0, null);
        Position copy = new Position(position);
        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        List<Move> childrenCopy = MoveGenerator.generateStrictlyLegal(position);
        //System.out.println("position changes?" + position.equals(copy));
        if (!position.equals(copy)) {
           System.out.println("Positions not equal!");
           position.printBoard();
           copy.printBoard();
        }

        if (children.size() == 0) { // Game ended by no moves to make
            if (position.whiteInCheck) // Black wins by checkmate
                return new MoveValue(NEGINFINITY + 1000 - depthLeft, null); //prefer a higher depth (mate earlier)
            if (position.blackInCheck) // White wins by checkmate
                return new MoveValue(POSINFINITY - 1000 + depthLeft, null);// prefer a higher depth
            return new MoveValue(0, null); // Stalemate
        }

        try {
            moveOrder(children);
        } catch (Exception e) {
            System.out.println("valid pos? " + position.validPosition());
            position.printDisplayBoard();
            position.printBoard();
            System.out.println("children copy");
            System.out.println(childrenCopy.size());
            //childrenCopy.stream().filter(move -> move.moveType == MoveType.CAPTURE).forEach(move -> System.out.println(move));
            childrenCopy.stream().filter(move -> move.start == 28).forEach(move -> System.out.println(move));
            System.out.println("children");
            System.out.println(children.size());
            //children.stream().filter(move -> move.moveType == MoveType.CAPTURE).forEach(move -> System.out.println(move));
            children.stream().filter(move -> move.start == 28).forEach(move -> System.out.println(move));
            //MoveGenerator.generateStrictlyLegal(position).stream().filter(move -> move.moveType == MoveType.CAPTURE).forEach(move -> System.out.println(move));
            throw new IllegalStateException();
        }

        MoveValue bestMoveValue = new MoveValue(NEGINFINITY, null);

        for (Move move : children) {
            position.makeMove(move);
            int score = -negaMax(-beta, -alpha, depthLeft - 1, position).value;
            position.unMakeMove(move);

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
        return bestMoveValue;
    }

    public static int quiescenceSearch(int alpha, int beta, Position position) {
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

    public static int quiesce(int alpha, int beta, Position position, boolean isMaximizingPlayer) {
        // Perform static evaluation (stand pat)
        int standPat = StaticEvaluation.negamaxEvaluatePosition(position);

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

/*
Move ordering with selection sort? e.g. choose
*/
    public static void moveOrder(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -moveValue(move)));
    }
    /**
    * Returns an integer value for a move used for sorting.
    */
    public static int moveValue(Move move) {
       int value = 0;

       if (move.moveType == MoveType.PROMOTION) {
           value += 500_000;
       }

       if (move.resultWhiteInCheck || move.resultBlackInCheck) {
           value += 1_000_000;
       }

       if (move.moveType == MoveType.CAPTURE) {
           if (move.captureType == null) {
               System.out.println("null capture type found");
               System.out.println(move);
           }
           value += 100_000 + StaticEvaluation.evaluateExchange(move);
       }

       return value;
    }

    // most valuable victim/ least valuable agressor
    public static void mVVLVA(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -(StaticEvaluation.evaluateExchange(move))));
    }



}