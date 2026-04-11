package com.jmeyer2030.driftwood.movegeneration;

import com.jmeyer2030.driftwood.board.FEN;
import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link MoveGenerator#isMoveLegal(Position, int)}.
 * Validates that isMoveLegal agrees with generateAllMoves for a wide range of positions.
 */
public class MoveGeneratorTest {

    /**
     * For a given position, generates all legal moves, then verifies isMoveLegal returns true
     * for each of them.
     */
    private void assertAllLegalMovesPassIsMoveLegal(Position position, String fen) {
        int[] moveBuffer = new int[256];

        // Generate all legal moves
        int numMoves = MoveGenerator.generateAllMoves(position, moveBuffer, 0);

        // Now compute pins for isMoveLegal (generateAllMoves already did, but let's be explicit)
        MoveGenerator.computePins(position);

        for (int i = 0; i < numMoves; i++) {
            int move = moveBuffer[i];
            assertTrue(MoveGenerator.isMoveLegal(position, move),
                    "isMoveLegal returned false for legal move " + MoveEncoding.getLAN(move)
                            + " in position: " + fen);
        }
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in starting position")
    public void testIsMoveLegal_StartingPosition() {
        // Arrange
        Position position = new Position();
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, "startpos");
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in a position with check")
    public void testIsMoveLegal_InCheck() {
        // Arrange
        FEN fen = new FEN("rnbqkbnr/pppp1ppp/8/4p3/7Q/8/PPPPPPP1/RNB1KBNR b KQkq - 1 2");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in positions with castling")
    public void testIsMoveLegal_Castling() {
        // Arrange
        FEN fen = new FEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in an en passant position")
    public void testIsMoveLegal_EnPassant() {
        // Arrange
        FEN fen = new FEN("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in positions with promotions")
    public void testIsMoveLegal_Promotions() {
        // Arrange
        FEN fen = new FEN("8/P5k1/8/8/8/8/1K6/8 w - - 0 1");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in a pinned piece position")
    public void testIsMoveLegal_PinnedPieces() {
        // Arrange: bishop on c1 is pinned by rook on a1 to king on e1 (not standard pin setup)
        // Use a position where several pieces are pinned
        FEN fen = new FEN("r1bqk1nr/pppp1ppp/2n5/4p3/1b1PP3/2N5/PPP2PPP/R1BQKBNR w KQkq - 3 4");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }

    @Test
    @DisplayName("isMoveLegal returns false for move 0")
    public void testIsMoveLegal_ZeroMove() {
        // Arrange
        Position position = new Position();
        MoveGenerator.computePins(position);
        // Act & Assert
        assertFalse(MoveGenerator.isMoveLegal(position, 0));
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves across perft suite positions")
    public void testIsMoveLegal_PerftSuite() throws Exception {
        // Arrange
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("perftSuite.txt"))));

        String line;
        int positionCount = 0;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank() || line.startsWith("#")) continue;

            // Parse the FEN from the perft line (FEN is everything before the first ';')
            String fenStr = line.split(";")[0].trim();
            FEN fen = new FEN(fenStr);
            Position position = new Position(fen);

            // Act & Assert
            assertAllLegalMovesPassIsMoveLegal(position, fenStr);
            positionCount++;
        }
        reader.close();

        assertTrue(positionCount > 100, "Should have tested at least 100 positions from perft suite, tested: " + positionCount);
    }

    @Test
    @DisplayName("isMoveLegal returns true for all legal moves in double check position")
    public void testIsMoveLegal_DoubleCheck() {
        // Arrange: double check position where only king moves are legal
        FEN fen = new FEN("4k3/8/8/8/1b6/8/5n2/4K3 w - - 0 1");
        Position position = new Position(fen);
        // Act & Assert
        assertAllLegalMovesPassIsMoveLegal(position, fen.toString());
    }
}

