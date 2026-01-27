package edu.kis.powp.jobs2d.drivers.strategy;

import java.util.logging.Logger;

public class OnCanvasExceededLogWarning implements OnCanvasExceededStrategy {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void onCanvasExceeded(int x, int y) {
		logger.warning("Point (" + x + ", " + y + ") is outside the canvas boundaries.");
	}

	@Override
	public String toString() {
		return "Canvas exceeded - warning logger";
	}
}
