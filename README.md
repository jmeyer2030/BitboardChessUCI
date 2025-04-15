# DriftWood Chess Engine

## Table of Contents

- [Motive](#motive)
- [Overview](#overview)
- [Setup](#setup)
  - [1. Verify requirments](#1-verify-requirments)
  - [2. Clone the repository](#2-clone-the-repository)
  - [3. Build](#3-build)
  - [4. Run](#4-run)
- [Technical Features](#technical-features)
  - [Board](#board)
  - [Evaluation](#evaluation)
  - [Search](#search)
  - [Move Generation](#move-generation)
  - [UCI Commands](#uci-commands)
- [Issues/TODO](#issuestodo)
- [Strength](#strength)

# Motive

This project is the intersection of my interest in programming and chess. 
I first learned about the minimax algorithm in a class I took in 2022, and it inspired me
to create a chess program for the first time. It was not very strong, and I didn't feel
satisfied with the result. I revisited the project in late 2024, and decided to do a 
rewrite with a more performant feature set.

# Overview

This chess engine does not include a GUI, and thus it is strongly recommended to use
an external GUI or application that uses UCI commands to play against or to test.
I personally prefer CuteChess but there are many others that will work. 

 - https://github.com/cutechess/cutechess/releases

If you do not wish to use an external GUI, there are instructions on how to use basic
uci commands to evaluate positions in the "setup" section.

DriftWood NNUE was trained on StockFish evaluations, and thus will not be submitted
to CCRL or any other chess engine rating groups or competitions until it is trained on
data that has been generated independently.

# Setup

## 1. Verify requirments

 - Java 17+
 - Maven 

## 2. Clone the repository

```bash
git clone https://github.com/jmeyer2030/BitboardChessUCI.git

cd BitboardChessUCI
```

## 3. Build

```bash
mvn package
```

At this point, you have built the .jar which can be used in certain GUIs. Others, such as
cutechess, require a .bat. To create a .bat, make a .txt with the following text, adjusting
the path to wherever the .jar is (probably ending with /BitboardChessUci/target/driftwood-5.0.jar):
```bash
@echo off
java -jar C:path/to/file/driftwood-5.0.jar
```
Then you can save it as a .bat.

## 4. Run

You can manually run it with the following command. If you run it this way you
can only interact with it through the terminal, so it is not recommended for playing
games.

```bash
java -jar target/driftwood-5.0.jar
```

If you decide to run through the terminal, here is example usage to analyze a starting position:

```bash
uci
ucinewgame
position startpos
go wtime 100000 btime 100000
quit
```

To analyze from fen:
```bash
uci
ucinewgame
position fen 4rr1k/PQ4p1/8/7p/2p2pqP/p4N2/4B1P1/2B2K1R b - - 0 1
go wtime 100000 btime 100000
quit
```
You can send new position commands without starting a new game, and you can
 append position commands with moves from a fen or starting position:
 ```bash
position fen 4rr1k/PQ4p1/8/7p/2p2pqP/p4N2/4B1P1/2B2K1R b - - 0 1 moves e8e2 f3g5
position startpos moves e2e4 e7e5 g1f3 g8f3 b1c3
 ```
 Then use "go" to analyze. wtime and btime specifications are required.

# Technical Features

## Board
- Bitboard position representation
- Make/UnMake move
  - Iterative nnue feature updates

## Evaluation

- NNUE Static Evaluation
    - Trained with Stock Fish data using the Bullet NNUE Trainer
    - (768 -> 256) x 2 -> 1 Architecture
    - Iterative accumulator updates 

## Search

- General:
  - Iterative Deepening
  - Fail soft framework
  - Principle variation search
  - Transposition tables with main.java.zobrist hashing
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

## Move Generation

- Not in check legal moves
- Single check legal moves
- Double check legal moves
- Absolute pin detection
  - "inBetween" table to restrict piece moves
- Integer move encoding
- Precomputed move tables
  - Magic bitboards for sliding pieces and magic number generation

## UCI Commands

- uci
- ucinewgame
- position (startpos, fen, moves)
- go wtime ... btime ...
    - Currently only the active player's time is considered
- quit


# Issues/TODO
- Fix tests
- HCE and generate training data for NN 
- Aspiration windows
  - idea:
    - If no tt information, full window
    - else if mating/mated evaluation, set window of mated scores
    - else set window to tt score +- constant
        - if fails high, bind beta pos inf
        - if fails low, bind alpha neg inf

# Strength

- ~2400 CCRL blitz based on a large number of games played against a 2400 CCRL chess engine
