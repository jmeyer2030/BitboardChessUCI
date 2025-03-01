# DriftWoodV3.10 Chess Engine

## Features:

- NNUE Static Evaluation
    - Trained with Stock Fish data using the Bullet NNUE Trainer
    - (768 -> 256) x 2 -> 1 Architecture
- Bitboard position representation and magic bitboards
- Transposition tables with zobrist hashing
- Three-fold repetition detection
- PV Search
- History Heuristic
- Futility Pruning
- Iterative deepening (with thread interruption and position restoration based on a timer)
- Quiessence search
- Move ordering (Principle Variation -> checks -> capture with MVVLVA -> History Heuristic)

## UCI Commands:

- uci
- ucinewgame
- position (startpos, fen, moves)
- go wtime ... btime ...
    - Currently only the active player's time is considered
- quit


## Issues/TODO:
- Delta pruning should be imlemented. Exists as comments but did not improve elo
- PV Lines returned while searching should be verified
- Ensure that TT stores best move for root node correctly


## Status:

- Functional
- Strength is unknown.
    - Definitely stronger than top player bots on chess com.
    - Have tested vs Magnus/Hikaru bots a few time on 300s + 8s winning consistently
    - Statistically significant tests have only been run against itself

