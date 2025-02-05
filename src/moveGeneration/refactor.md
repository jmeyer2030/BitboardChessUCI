# Move Generator Refactoring

## Goals

Goal is to increase efficiency by:
 - Passing a moveBuffer, an array that stores generated moves to the move generator
to reduce reallocating memory in the form of lists
 - Representing moves as integers to reduce non-primative memory allocation overhead

## Implementation

 - MoveGenerator should be passed an int[] and populate it with generated moves


## Search Implications:
For a search of depth n:
initialize an int[][], where int[ply] is a buffer for moves of that ply.

During search, we only have the moves generated for a ply at that time, so this structure can be reused for the whole search


### MoveShortcuts

 - Seeks to reduce complexity by having template moves 