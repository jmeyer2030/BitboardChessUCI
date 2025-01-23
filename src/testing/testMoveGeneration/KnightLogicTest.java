package testing.testMoveGeneration;

import org.junit.jupiter.api.BeforeEach;

import moveGeneration.KnightLogic;

public class KnightLogicTest {
	
	private static KnightLogic kl;
	
	@BeforeEach
	public void setUp() {
		kl = new KnightLogic();
	}
}
