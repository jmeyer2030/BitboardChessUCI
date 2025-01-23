package system;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {

	//Centralized console handler
	private static final ConsoleHandler consoleHandler = new ConsoleHandler();
	
	static {
		//consoleHandler.setFormatter(format);
		consoleHandler.setLevel(Level.INFO);
	}
	
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz.getName());
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		return logger;
	}
}
