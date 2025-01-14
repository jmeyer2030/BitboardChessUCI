package testing.testZobrist;
import board.Position;
import zobrist.*;
public class testThreePly {
    public static void main(String[] args) {
        Hashing.initializeRandomNumbers();
        Position position = new Position();

        long zobristHash = Hashing.computeZobrist(position);

        HashTables.incrementThreeFold(zobristHash);
        HashTables.incrementThreeFold(zobristHash);
        HashTables.incrementThreeFold(zobristHash);

        System.out.println(HashTables.repetitionsExceeded(zobristHash));

        HashTables.decrementThreeFold(zobristHash);

        System.out.println(HashTables.repetitionsExceeded(zobristHash));

    }
}
