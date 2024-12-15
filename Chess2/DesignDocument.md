## Joshua Meyer 2024
# Bitboard Chess Design Document:
## Overview:
This engine primarily uses bitboards, representations of a board as a 64bit long 
to indicate the presence or absence of something on a board.

This engine uses Little-Endian Rank-File board representation, with the least
significant bit being zero.

![alt text](https://www.chessprogramming.org/images/b/b5/Lerf.JPG)

## Position Representation:

Position requirements differ a little from what is most useful in an engine. 

### Bare Requirements:

A position without context essentially requires:
- Piece location and color
- En passant legality
- Castling rights
- Moves since 50 move rule reset

### Implementation:

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

#### State

- boolean whiteToPlay: if white to move
- byte castleRights: 0b000(WQ)(WK)(BQ)(BK)
- int enPassant: location of pawn if it advanced one square (else 0)
- int gameStatus: -1 Black win, 0 stalemate, 1 white win, 2 ongoing
- int rule50: Moves since last pawn move/capture
- int fullMoveCount: Total number of moves
 
#### Attack Maps:

- whiteAttackMap: BB of squares white attacks
- blackAttackMap: BB of squares black attacks

## Move Generation:

### Overview:

Moves are generated in a pseudo-legal fashion meaning that checks aren't evaluated during move generation.
Instead, checks are detected by applying them to a board and looking for self-checks.

### Moves:

Moves are not stored in the position, and must contain enough information to be reversible.
Thus, they have the following fields:

#### Move details:

- start: square a piece started on
- destination: square that the piece is moving to
- MoveType: QUIET, CAPTURE, ENPASSANT, PROMOTION, CAPMOTION, CASTLE
- promotionType: Piece type that a pawn promoted to, else null
- captureType: Piece type that was captured
 
#### Position details:

- halfMoveCount: halfMoveCount of position before this move is applied
- castleRights: castleRights before this move is applied
