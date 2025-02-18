package engine.evaluation;

import board.Position;

public class InsufficientMaterial {

    /**
    * Should evaluate certain classes of positions as draws:
    *  - Two knights vs king
    *  - King and minor vs king and minor
    *  - Minor vs two knights
    *  - two bishops against bishop
    *  - two minor vs minor (unless bishop pair)
    *
    * It is not a draw iff:
    *  - there are any pawns, rooks, or queens or
    *  - there are four or more minor pieces or
    *  - two bishops are against a knight (or less)
    *  - bishop and knight are against nothing
    *  else it is a draw
    */
    public static boolean insufficientMaterial(Position position) {
        if (position.pieceCounts[0][0] + position.pieceCounts[1][0] != 0 || // There are no pawns
            position.pieceCounts[0][3] + position.pieceCounts[1][3] != 0 || // There are no rooks
            position.pieceCounts[0][4] + position.pieceCounts[1][4] != 0 || // There are no queens
            (position.pieceCounts[0][1] + position.pieceCounts[1][1] + position.pieceCounts[0][2] + position.pieceCounts[1][2] >= 4) || // Total minors >= 4
            (position.pieceCounts[0][2] >= 2 && position.pieceCounts[1][1] <= 1 ) || // Two or more white bishops vs one or fewer black knights
            (position.pieceCounts[1][2] >= 2 && position.pieceCounts[0][1] <= 1) || // Two or more black bishops vs one or fewer white knights
            (position.pieceCounts[0][1] == 1 && position.pieceCounts[0][2] == 1 && position.pieceCounts[1][1] == 0 && position.pieceCounts[1][2] == 0) || // white bishop and knight vs king
            (position.pieceCounts[1][1] == 1 && position.pieceCounts[1][2] == 1 && position.pieceCounts[0][1] == 0 && position.pieceCounts[0][2] == 0)) { // black bishop and knight vs king
            return false;
        }
        return true;
    }
}
