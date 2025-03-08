# DriftWood Chess Engine




## History:

This project is the intersection of my interest in programming and chess. 
I first learned about the minimax algorithm in a class I took in 2022, and it inspired me
to create a chess program for the first time. It was not very strong, and I didn't feel
satisifed with the result. I revisited the project in late 2024, and decided to do a 
rewrite with a more performant feature set.

## Overview:

This chess engine does not include a GUI, and thus requires an external GUI or 
application that uses UCI commands to play against or to test.
I personally prefer CuteChess but there are many others that will work.
 - https://github.com/cutechess/cutechess/releases

DriftWood NNUE was trained on StockFish evaluations, and thus will not be submitted
to CCRL or any other engine rating groups or competitions until it is trained on
data that has been generated independently. Generating data independently would
require a lot of time, and the data would be better if my engine were stronger,
so this is low priority. 


## Features:

### Board and Move Generation:
- Bitboard position representation
- Magic bitboards for sliding piece moves
- Move buffer for efficient storage of moves
- 32-bit integer move representation

### Evaluation:
- NNUE Static Evaluation
    - Trained with Stock Fish data using the Bullet NNUE Trainer
    - (768 -> 256) x 2 -> 1 Architecture

### Search:
- General:
  - Iterative Deepening
  - Fail soft framework
  - Principle variation search
  - Transposition tables with zobrist hashing
  - Three-fold repetition detection
- Move Ordering:
  - Principle Variation:
  - MVVLVA
  - Killer Moves
  - History Heuristic
- Pruning:
  - Alpha-Beta Pruning
  - Null Move Pruning
  - Futility Pruning
- Other:
  - Late Move Reduction
- Quiescence Search:
  - SEE (in progress)
  - Delta Pruning (in progress)
  - Futility Pruning (in progress)

## UCI Commands:

- uci
- ucinewgame
- position (startpos, fen, moves)
- go wtime ... btime ...
    - Currently only the active player's time is considered
- quit


## Issues/TODO:
- Struggles with checkmates in the endgame
  - Possibly related to TT overwrites or pruning
- SEE Implementation

## Strength:
- ~2100 CCRL blitz based on a large number of games played against a 2100 CCRL engine 

