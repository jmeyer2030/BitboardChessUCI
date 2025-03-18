# DriftWood Chess Engine


## History:

This project is the intersection of my interest in programming and chess. 
I first learned about the minimax algorithm in a class I took in 2022, and it inspired me
to create a chess program for the first time. It was not very strong, and I didn't feel
satisfied with the result. I revisited the project in late 2024, and decided to do a 
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

## Build and Run:

Awaiting Maven implementation but for now the easiest way to build DriftWood is to:
 - Clone this repo
 - Open this project in intellij idea or eclipse using JDK21 (latest LTS release)
 - Configure userFeatures.ChessEngine as the main class
 - Build artifacts 

On windows, use: "java -jar DriftWood.jar". This will open run driftwood from a command line interface.

## Features:

### Board:
- Bitboard position representation
- Make/UnMake move
  - Iterative feature updates

### Evaluation:

- NNUE Static Evaluation
    - Trained with Stock Fish data using the Bullet NNUE Trainer
    - (768 -> 256) x 2 -> 1 Architecture
    - Iterative accumulator updates 

### Search:

- General:
  - Iterative Deepening
  - Fail soft framework
  - Principle variation search
  - Transposition tables with zobrist hashing
  - Three-fold repetition detection
  - Mate Scoring
- Move Ordering:
  - Scoring, then best move grabbed (rather than sorting all) 
  - Criteria: 
    - Principle Variation
    - Promotions 
    - MVV-LVA (for positive MVV-LVA captures)
    - Static Exchange Evaluation (for negative MVV-LVA captures)
    - Killer Moves
    - History Heuristic
- Pruning:
  - Alpha-Beta Pruning
  - Null Move Pruning
  - Futility Pruning (depth <= 3)
  - Reverse Futility Pruning
- Other:
  - Late Move Reduction
- Quiescence Search:
  - Alpha-beta pruning 
  - Delta Pruning (in progress)
  - Futility Pruning (in progress)

### Move Generation:

- Not in check legal moves
- Single check legal moves
- Double check legal moves
- Absolute pin detection
  - "inBetween" table
- int move encoding
- Precomputed move tables
  - Magic bitboards for sliding pieces and magic number generation

## UCI Commands:

- uci
- ucinewgame
- position (startpos, fen, moves)
- go wtime ... btime ...
    - Currently only the active player's time is considered
- quit


## Issues/TODO:

- Add Maven support
- Aspiration windows
  - idea:
    - If no tt information, full window
    - else if mating/mated evaluation, set window of mated scores
    - else set window to tt score +- constant
        - if fails high, bind beta pos inf
        - if fails low, bind alpha neg inf

## Strength:

- ~2400 CCRL blitz based on a large number of games played against a 2400 CCRL engine 

