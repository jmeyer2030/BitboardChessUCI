package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moveGeneration.BishopMagicBitboard;

public class BishopMagicBitboardTest {
	private static BishopMagicBitboard bmb;
	private static BishopMagicBitboard.TestHook hook;
	
	@BeforeEach
	public void setUp() {
		bmb = new BishopMagicBitboard();
		hook = new BishopMagicBitboard.TestHook(bmb);
	}
	
//Method: generateBlockerMask(int square)
	//Corner
	@Test
	public void testGenerateBlockermaskCorner() {
		long result = hook.testGenerateBlockerMask(0);
		long expected = 0b0000000_01000000_00100000_00010000_00001000_00000100_00000010_00000000L;
		assertEquals(expected, result);
	}
	//Edge
	@Test
	public void testGenerateBlockermaskEdge() {
		long result = hook.testGenerateBlockerMask(4);
		long expected = 0b0000000_00000000_00000000_00000000_00000010_01000100_00101000_00000000L;
		assertEquals(expected, result);
	}
	//Center
	@Test
	public void testGenerateBlockermaskCenter() {
		long result = hook.testGenerateBlockerMask(28);
		long expected = 0b0000000_00000010_01000100_00101000_00000000_00101000_01000100_00000000L;
		assertEquals(expected, result);
	}
//Method: generateMoveBoard(long blockerBoard, int square)
	//
}
