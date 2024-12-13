## Joshua Meyer 2024
# Bitboard Chess Design Document:
## Overview:
This engine primarily uses bitboards, representations of a board as a 64bit long 
to indicate the presence or absence of something on a board.

This engine uses Little-Endian Rank-File board representation, with the least
significant bit being zero.

![alt text](https://www.chessprogramming.org/images/b/b5/Lerf.JPG)

## Position Representation:
### Bare Requirements:
A position without context essentially requires:
- Piece location and color
- En passant legality
- Castling rights
- Moves since 50 move rule reset

### This Implementation:

For our purposes we represent positions with these fields:
#### Piece Locations:
- long occupancy: BB of occupied squares
- long whitePieces: BB of white pieces
- long blackPieces: BB of black pieces
- long pawns: BB of pawns
- long rooks: BB of rooks
- long knights: BB of knights
- long bishops: BB of bishops
- long queens: BB of queens
- long kings: BB of kings
#### State Information:
- int rule50: Moves since last pawn move/capture
- int moves: Total number of moves
- long checkers: BB of checking pieces
- long kingBlockers: 
- 
## Move Generation:

## 