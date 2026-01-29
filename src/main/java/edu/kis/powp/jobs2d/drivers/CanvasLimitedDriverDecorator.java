package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.canvas.CanvasManager;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.drivers.strategy.OnCanvasExceededStrategy;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class CanvasLimitedDriverDecorator implements VisitableJob2dDriver {
	private final VisitableJob2dDriver targetDriver;
	private final OnCanvasExceededStrategy onCanvasExceededStrategy;

	public CanvasLimitedDriverDecorator(VisitableJob2dDriver targetDriver, OnCanvasExceededStrategy onCanvasExceededStrategy) {
		this.targetDriver = targetDriver;
		this.onCanvasExceededStrategy = onCanvasExceededStrategy;
	}

	public VisitableJob2dDriver getTargetDriver() {
		return targetDriver;
	}

	public OnCanvasExceededStrategy getOnCanvasExceededStrategy() {
		return onCanvasExceededStrategy;
	}

	public boolean checkCanvasContainsPoint(int x, int y) {
		CanvasManager canvasManager = CanvasFeature.getCanvasManager();
		ICanvas canvas = canvasManager.getCurrentCanvas();
		if (canvas != null && !canvas.containsPoint(x, y)) {
			onCanvasExceededStrategy.onCanvasExceeded(x, y);
			return false;
		}
		return true;
	}

	@Override
	public void setPosition(int x, int y) {
		if (!checkCanvasContainsPoint(x, y)) {
			return;
		}
		targetDriver.setPosition(x, y);
	}

	@Override
	public void operateTo(int x, int y) {
		if (!checkCanvasContainsPoint(x, y)) {
			return;
		}
		targetDriver.operateTo(x, y);
	}

	@Override
	public void accept(DriverVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Canvas-limited: " + targetDriver.toString();
	}
}
