package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moveGeneration.RookMagicBitboard;

public class RookMagicBitboardTest {
	
	private static RookMagicBitboard rmb;
	private static RookMagicBitboard.TestHook hook;
	
	@BeforeEach
	public void setUp() {
		rmb = new RookMagicBitboard();
		hook = new RookMagicBitboard.TestHook(rmb);
	}
	
	
//Method: GenerateBlockerMask
	//Corner Blocker
	@Test
	public void testGenerateBlockerMaskCorner() {
		long result = hook.testGenerateBlockerMask(0);
		long expected = 0b0000000_000000001_00000001_00000001_00000001_00000001_00000001_01111110L;
		assertEquals(expected, result);
	}
	
	//Edge Blocker
	@Test
	public void testGenerateBlockerMaskEdge() {
		long result = hook.testGenerateBlockerMask(3);
		long expected = 0b0000000_000001000_00001000_00001000_00001000_00001000_00001000_01110110L;
		assertEquals(expected, result);
	}
	
	//Center Blocker
	@Test
	public void testGenerateBlockerMaskCenter() {
		long result = hook.testGenerateBlockerMask(9);
		long expected = 0b0000000_000000010_00000010_00000010_00000010_00000010_01111100_00000000L;
		assertEquals(expected, result);
	}
	
	
//Method: generateBlockerBoards(long blockerMask)
	//Test on binary 7
    @Test
    public void testGenerateBlockerBoardsAllOnes() {
        long number = 7; // Binary: 111
        List<Long> permutations = hook.testGenerateBlockerBoards(number);
        
        // Expected permutations: [7, 6, 5, 4, 3, 2, 1, 0]
        assertEquals(8, permutations.size());  // There should be 8 permutations
        assertTrue(permutations.contains(7L));  // Binary: 111
        assertTrue(permutations.contains(6L));  // Binary: 110
        assertTrue(permutations.contains(5L));  // Binary: 101
        assertTrue(permutations.contains(4L));  // Binary: 100
        assertTrue(permutations.contains(3L));  // Binary: 011
        assertTrue(permutations.contains(2L));  // Binary: 010
        assertTrue(permutations.contains(1L));  // Binary: 001
        assertTrue(permutations.contains(0L));  // Binary: 000
    }
    
    @Test
    public void testGeneratePermutations_SingleOne() {
    
        long number = 8; // Binary: 1000
        List<Long> permutations = hook.testGenerateBlockerBoards(number);
        
        // Expected permutations: [8, 0]
        assertEquals(2, permutations.size());  // Only 2 permutations
        assertTrue(permutations.contains(8L));  // Binary: 1000
        assertTrue(permutations.contains(0L));  // Binary: 0000
    }
}
