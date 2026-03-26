package com.jmeyer2030.driftwood.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranspositionTableTest {

    // -- Constructor tests --

    @Test
    @DisplayName("Constructor creates table when numBits is within valid range")
    void constructorCreatesTable_whenNumBitsValid() {
        // Arrange
        int numBits = 4;

        // Act
        TranspositionTable tt = new TranspositionTable(numBits);

        // Assert - table exists and accepts operations without error
        assertNotNull(tt);
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when numBits is zero")
    void constructorThrows_whenNumBitsIsZero() {
        // Arrange
        int numBits = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TranspositionTable(numBits));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when numBits is negative")
    void constructorThrows_whenNumBitsIsNegative() {
        // Arrange
        int numBits = -1;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TranspositionTable(numBits));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when numBits exceeds 30")
    void constructorThrows_whenNumBitsExceedsMax() {
        // Arrange
        int numBits = 31;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TranspositionTable(numBits));
    }

    @Test
    @DisplayName("Constructor accepts boundary value numBits = 1")
    void constructorAccepts_whenNumBitsIsOne() {
        // Arrange
        int numBits = 1;

        // Act
        TranspositionTable tt = new TranspositionTable(numBits);

        // Assert
        assertNotNull(tt);
    }

    // -- getIndex tests --

    @Test
    @DisplayName("getIndex returns value within table bounds when given a large hash")
    void getIndexReturnsWithinBounds_whenGivenLargeHash() {
        // Arrange
        int numBits = 4; // table size = 16, indices 0..15
        TranspositionTable tt = new TranspositionTable(numBits);
        long hash = 0xDEAD_BEEF_CAFE_BABEL;

        // Act
        int index = tt.getIndex(hash);

        // Assert
        assertTrue(index >= 0 && index < 16);
    }

    @Test
    @DisplayName("getIndex returns zero when hash is zero")
    void getIndexReturnsZero_whenHashIsZero() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);

        // Act
        int index = tt.getIndex(0L);

        // Assert
        assertEquals(0, index);
    }

    // -- Unpack round-trip tests --

    @Test
    @DisplayName("Unpack returns stored bestMove after addElement and probe")
    void unpackBestMove_returnsCorrectValue_afterStore() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int bestMove = 0x0000_1A2B;
        int depth = 10;
        int score = 500;
        int nodeType = NodeType.EXACT;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(bestMove, TranspositionTable.unpackBestMove(packed));
    }

    @Test
    @DisplayName("Unpack returns stored depth after addElement and probe")
    void unpackDepth_returnsCorrectValue_afterStore() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int bestMove = 42;
        int depth = 15;
        int score = -100;
        int nodeType = NodeType.LOWER_BOUND;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(depth, TranspositionTable.unpackDepth(packed));
    }

    @Test
    @DisplayName("Unpack returns stored nodeType after addElement and probe")
    void unpackNodeType_returnsCorrectValue_afterStore() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 99999L;
        int bestMove = 7;
        int depth = 5;
        int score = 0;
        int nodeType = NodeType.UPPER_BOUND;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(nodeType, TranspositionTable.unpackNodeType(packed));
    }

    @Test
    @DisplayName("Unpack returns stored positive score after addElement and probe")
    void unpackScore_returnsCorrectPositiveValue_afterStore() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 55555L;
        int bestMove = 1;
        int depth = 8;
        int score = 12345;
        int nodeType = NodeType.EXACT;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(score, TranspositionTable.unpackScore(packed));
    }

    @Test
    @DisplayName("Unpack returns stored negative score after addElement and probe")
    void unpackScore_returnsCorrectNegativeValue_afterStore() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 77777L;
        int bestMove = 3;
        int depth = 6;
        int score = -9876;
        int nodeType = NodeType.EXACT;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(score, TranspositionTable.unpackScore(packed));
    }

    @Test
    @DisplayName("Unpack round-trips all fields correctly for a single stored entry")
    void allFieldsRoundTrip_afterStoreAndProbe() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(10);
        long hash = 0xABCD_1234_5678_9ABCL;
        int bestMove = 0b0000_0000_0000_1010_0011_1100_0101_0110; // arbitrary 32-bit move
        int depth = 20;
        int score = -500;
        int nodeType = NodeType.LOWER_BOUND;

        // Act
        tt.addElement(hash, bestMove, depth, score, nodeType);
        long packed = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(bestMove, TranspositionTable.unpackBestMove(packed));
        assertEquals(depth, TranspositionTable.unpackDepth(packed));
        assertEquals(score, TranspositionTable.unpackScore(packed));
        assertEquals(nodeType, TranspositionTable.unpackNodeType(packed));
    }

    // -- Probe miss scenarios --

    @Test
    @DisplayName("Probe returns zero when table is empty")
    void probeReturnsZero_whenTableIsEmpty() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;

        // Act
        long result = tt.probe(hash, 1);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Probe returns zero when hash does not match stored entry")
    void probeReturnsZero_whenHashDoesNotMatch() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long storedHash = 12345L;
        long queryHash = 67890L;
        tt.addElement(storedHash, 1, 10, 100, NodeType.EXACT);

        // Act
        long result = tt.probe(queryHash, 1);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Probe returns zero when stored depth is less than required depth")
    void probeReturnsZero_whenDepthInsufficient() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int storedDepth = 5;
        int requiredDepth = 10;
        tt.addElement(hash, 1, storedDepth, 100, NodeType.EXACT);

        // Act
        long result = tt.probe(hash, requiredDepth);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Probe returns packed data when stored depth equals required depth")
    void probeReturnsPacked_whenDepthEqualsRequired() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int depth = 10;
        tt.addElement(hash, 1, depth, 100, NodeType.EXACT);

        // Act
        long result = tt.probe(hash, depth);

        // Assert
        assertNotEquals(0, result);
    }

    @Test
    @DisplayName("Probe returns packed data when stored depth exceeds required depth")
    void probeReturnsPacked_whenDepthExceedsRequired() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 1, 15, 100, NodeType.EXACT);

        // Act
        long result = tt.probe(hash, 10);

        // Assert
        assertNotEquals(0, result);
    }

    @Test
    @DisplayName("Probe returns zero when bestMove is zero")
    void probeReturnsZero_whenBestMoveIsZero() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 0, 10, 100, NodeType.EXACT);

        // Act
        long result = tt.probe(hash, 1);

        // Assert
        assertEquals(0, result);
    }

    // -- checkedGetBestMove tests --

    @Test
    @DisplayName("checkedGetBestMove returns stored move when hash matches")
    void checkedGetBestMoveReturnsMove_whenHashMatches() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int bestMove = 42;
        tt.addElement(hash, bestMove, 10, 100, NodeType.EXACT);

        // Act
        int result = tt.checkedGetBestMove(hash);

        // Assert
        assertEquals(bestMove, result);
    }

    @Test
    @DisplayName("checkedGetBestMove returns zero when hash does not match")
    void checkedGetBestMoveReturnsZero_whenHashDoesNotMatch() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        tt.addElement(12345L, 42, 10, 100, NodeType.EXACT);

        // Act
        int result = tt.checkedGetBestMove(67890L);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("checkedGetBestMove returns move regardless of depth")
    void checkedGetBestMoveReturnsMove_regardlessOfDepth() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        int bestMove = 99;
        tt.addElement(hash, bestMove, 1, 0, NodeType.EXACT);

        // Act
        int result = tt.checkedGetBestMove(hash);

        // Assert - returns the move even though depth is shallow
        assertEquals(bestMove, result);
    }

    // -- Overwrite / replacement tests --

    @Test
    @DisplayName("addElement overwrites previous entry at the same index")
    void addElementOverwrites_whenSameHash() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 1, 5, 100, NodeType.EXACT);

        // Act - overwrite with a new entry
        int newBestMove = 99;
        int newDepth = 20;
        int newScore = -300;
        int newNodeType = NodeType.UPPER_BOUND;
        tt.addElement(hash, newBestMove, newDepth, newScore, newNodeType);
        long packed = tt.probe(hash, newDepth);

        // Assert - second write is what we read back
        assertNotEquals(0, packed);
        assertEquals(newBestMove, TranspositionTable.unpackBestMove(packed));
        assertEquals(newDepth, TranspositionTable.unpackDepth(packed));
        assertEquals(newScore, TranspositionTable.unpackScore(packed));
        assertEquals(newNodeType, TranspositionTable.unpackNodeType(packed));
    }

    @Test
    @DisplayName("addElement with colliding index invalidates previous entry's hash verification")
    void addElementInvalidatesPrevious_whenIndexCollides() {
        // Arrange - two hashes that map to the same index in a 2-entry table
        TranspositionTable tt = new TranspositionTable(1); // 2 entries: indices 0 and 1
        long hash1 = 0b00L; // index 0
        long hash2 = 0b10L; // also index 0
        tt.addElement(hash1, 1, 10, 100, NodeType.EXACT);

        // Act - overwrite same slot with different hash
        tt.addElement(hash2, 2, 10, 200, NodeType.EXACT);

        // Assert - first entry is no longer retrievable
        assertEquals(0, tt.probe(hash1, 1));
        assertNotEquals(0, tt.probe(hash2, 1));
    }

    // -- Edge case: score sign preservation --

    @Test
    @DisplayName("Unpack preserves zero score after round-trip")
    void unpackScore_preservesZero_afterRoundTrip() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 11111L;
        int score = 0;
        tt.addElement(hash, 1, 5, score, NodeType.EXACT);

        // Act
        long packed = tt.probe(hash, 5);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(0, TranspositionTable.unpackScore(packed));
    }

    // -- Edge case: max depth --

    @Test
    @DisplayName("Unpack returns max depth 255 after round-trip")
    void unpackDepth_returnsMaxDepth_afterRoundTrip() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 22222L;
        int maxDepth = 255;
        tt.addElement(hash, 1, maxDepth, 50, NodeType.EXACT);

        // Act
        long packed = tt.probe(hash, maxDepth);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(maxDepth, TranspositionTable.unpackDepth(packed));
    }

    // -- Edge case: all node types --

    @Test
    @DisplayName("Unpack returns EXACT node type after round-trip")
    void unpackNodeType_returnsExact_afterRoundTrip() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 33333L;
        tt.addElement(hash, 1, 5, 0, NodeType.EXACT);

        // Act
        long packed = tt.probe(hash, 5);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(NodeType.EXACT, TranspositionTable.unpackNodeType(packed));
    }

    @Test
    @DisplayName("Unpack returns LOWER_BOUND node type after round-trip")
    void unpackNodeType_returnsLowerBound_afterRoundTrip() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 44444L;
        tt.addElement(hash, 1, 5, 0, NodeType.LOWER_BOUND);

        // Act
        long packed = tt.probe(hash, 5);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(NodeType.LOWER_BOUND, TranspositionTable.unpackNodeType(packed));
    }

    @Test
    @DisplayName("Unpack returns UPPER_BOUND node type after round-trip")
    void unpackNodeType_returnsUpperBound_afterRoundTrip() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 55555L;
        tt.addElement(hash, 1, 5, 0, NodeType.UPPER_BOUND);

        // Act
        long packed = tt.probe(hash, 5);

        // Assert
        assertNotEquals(0, packed);
        assertEquals(NodeType.UPPER_BOUND, TranspositionTable.unpackNodeType(packed));
    }

    // -- Replacement policy tests --

    @Test
    @DisplayName("addElement replaces entry when new depth is greater than old depth")
    void addElementReplaces_whenNewDepthIsGreater() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 1, 5, 100, NodeType.EXACT);

        // Act - store deeper entry at same hash
        tt.addElement(hash, 2, 10, 200, NodeType.LOWER_BOUND);
        long packed = tt.probe(hash, 10);

        // Assert - deeper entry replaced the shallower one
        assertNotEquals(0, packed);
        assertEquals(2, TranspositionTable.unpackBestMove(packed));
        assertEquals(10, TranspositionTable.unpackDepth(packed));
        assertEquals(200, TranspositionTable.unpackScore(packed));
    }

    @Test
    @DisplayName("addElement replaces entry when new depth equals old depth")
    void addElementReplaces_whenNewDepthEquals() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 1, 10, 100, NodeType.EXACT);

        // Act - store entry at same depth
        tt.addElement(hash, 2, 10, 200, NodeType.LOWER_BOUND);
        long packed = tt.probe(hash, 10);

        // Assert - new entry replaced the old one
        assertNotEquals(0, packed);
        assertEquals(2, TranspositionTable.unpackBestMove(packed));
    }

    @Test
    @DisplayName("addElement always replaces even when new entry is shallower")
    void addElementAlwaysReplaces_whenNewIsShallower() {
        // Arrange
        TranspositionTable tt = new TranspositionTable(8);
        long hash = 12345L;
        tt.addElement(hash, 1, 15, 100, NodeType.EXACT);

        // Act - store shallower entry (always-replace policy)
        tt.addElement(hash, 2, 5, 200, NodeType.LOWER_BOUND);
        long packed = tt.probe(hash, 5);

        // Assert - shallower entry replaced the deeper one
        assertNotEquals(0, packed);
        assertEquals(2, TranspositionTable.unpackBestMove(packed));
        assertEquals(5, TranspositionTable.unpackDepth(packed));
        assertEquals(200, TranspositionTable.unpackScore(packed));
    }
}
