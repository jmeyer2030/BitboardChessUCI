package testing.testMoveGeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moveGeneration.BishopLogic;

public class BishopLogicTest {
	private BishopLogic.TestHook hook;
	
	@BeforeEach
	public void setUp() {
		BishopLogic bmb = new BishopLogic();
		hook = new BishopLogic.TestHook(bmb);
	}
//Method: initialize all
	@Test
	public void testInitializeAll() {
		//bmb.initializeAll();
	}
	
//Method: generateBlockerMask(int square)
	//Corner
	@Test
	public void testGenerateBlockerMaskCorner() {
		long result = hook.testGenerateBlockerMask(0);
		long expected = 0b0000000_01000000_00100000_00010000_00001000_00000100_00000010_00000000L;
		assertEquals(expected, result);
	}
	//Edge
	@Test
	public void testGenerateBlockerMaskEdge() {
		long result = hook.testGenerateBlockerMask(4);
		long expected = 0b0000000_00000000_00000000_00000000_00000010_01000100_00101000_00000000L;
		assertEquals(expected, result);
	}
	//Center
	@Test
	public void testGenerateBlockerMaskCenter() {
		long result = hook.testGenerateBlockerMask(28);
		long expected = 0b0000000_00000010_01000100_00101000_00000000_00101000_01000100_00000000L;
		assertEquals(expected, result);
	}
//Method: generateMoveBoard(long blockerBoard, int square)
	//
}
