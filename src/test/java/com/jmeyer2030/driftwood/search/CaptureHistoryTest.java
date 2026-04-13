package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Piece;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CaptureHistory}.
 */
public class CaptureHistoryTest {

    private static int makeCapture(int fromSq, int toSq, int movedPiece, int capturedPiece) {
        int move = 0;
        move = MoveEncoding.setStart(move, fromSq);
        move = MoveEncoding.setDestination(move, toSq);
        move = MoveEncoding.setMovedPiece(move, movedPiece);
        move = MoveEncoding.setCapturedPiece(move, capturedPiece);
        move = MoveEncoding.setIsCapture(move, 1);
        return move;
    }

    @Test
    @DisplayName("getScore returns zero for an unseen capture")
    public void testInitialScoreIsZero() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int move = makeCapture(12, 28, Piece.KNIGHT, Piece.PAWN);

        // Act
        int score = ch.getScore(0, move);

        // Assert
        assertEquals(0, score);
    }

    @Test
    @DisplayName("addBonus increases score for the given capture")
    public void testBonusIncreasesScore() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int move = makeCapture(12, 28, Piece.KNIGHT, Piece.PAWN);

        // Act
        ch.addBonus(0, move, 5);
        int score = ch.getScore(0, move);

        // Assert
        assertTrue(score > 0, "Score should be positive after bonus, was: " + score);
    }

    @Test
    @DisplayName("addMalus decreases score for the given capture")
    public void testMalusDecreasesScore() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int move = makeCapture(12, 28, Piece.KNIGHT, Piece.PAWN);

        // Act
        ch.addMalus(0, move, 5);
        int score = ch.getScore(0, move);

        // Assert
        assertTrue(score < 0, "Score should be negative after malus, was: " + score);
    }

    @Test
    @DisplayName("Gravity keeps scores bounded within MAX_CAPTURE_HISTORY after many updates")
    public void testGravityBoundsScores() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int move = makeCapture(0, 63, Piece.QUEEN, Piece.QUEEN);

        // Act
        for (int i = 0; i < 1000; i++) {
            ch.addBonus(0, move, 20);
        }
        int highScore = ch.getScore(0, move);

        // Assert
        assertTrue(highScore <= 16_384, "Score should be bounded, was: " + highScore);
        assertTrue(highScore > 0, "Score should be positive after many bonuses");

        // Act
        for (int i = 0; i < 2000; i++) {
            ch.addMalus(0, move, 20);
        }
        int lowScore = ch.getScore(0, move);

        // Assert
        assertTrue(lowScore >= -16_384, "Score should be bounded below, was: " + lowScore);
        assertTrue(lowScore < 0, "Score should be negative after many maluses");
    }

    @Test
    @DisplayName("Different colors maintain independent capture history entries")
    public void testColorIndependence() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int move = makeCapture(12, 28, Piece.KNIGHT, Piece.PAWN);

        // Act
        ch.addBonus(0, move, 5);
        int whiteScore = ch.getScore(0, move);
        int blackScore = ch.getScore(1, move);

        // Assert
        assertTrue(whiteScore > 0, "White score should be positive");
        assertEquals(0, blackScore, "Black score should be unaffected");
    }

    @Test
    @DisplayName("Different piece/square/capture combinations maintain independent entries")
    public void testIndexIndependence() {
        // Arrange
        CaptureHistory ch = new CaptureHistory();
        int moveA = makeCapture(12, 28, Piece.KNIGHT, Piece.PAWN);
        int moveB = makeCapture(12, 28, Piece.BISHOP, Piece.PAWN);
        int moveC = makeCapture(12, 20, Piece.KNIGHT, Piece.PAWN);
        int moveD = makeCapture(12, 28, Piece.KNIGHT, Piece.ROOK);

        // Act
        ch.addBonus(0, moveA, 5);

        // Assert
        assertTrue(ch.getScore(0, moveA) > 0, "moveA should have positive score");
        assertEquals(0, ch.getScore(0, moveB), "moveB should be unaffected");
        assertEquals(0, ch.getScore(0, moveC), "moveC should be unaffected");
        assertEquals(0, ch.getScore(0, moveD), "moveD should be unaffected");
    }
}


