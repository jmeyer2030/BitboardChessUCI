### UCI (Universal Chess Interface) compliant Chess engine WIP

## Features:
 - Bitboard position representation
 - Transposition tables with zobrist hashing
 - Three-fold repetition detection
 - Negamax with alpha beta
 - Iterative deepening (with thread interruption and position restoration based on a timer)
 - Quiessence search
 - Move ordering (Principle Variation -> checks -> capture with MVVLVA -> other)
 - Piece square table position heuristic with tapered eval and game stage detection

## Issues:
 - Not all UCI features implmented

## Status:
 - In progress, working to add UCI features
