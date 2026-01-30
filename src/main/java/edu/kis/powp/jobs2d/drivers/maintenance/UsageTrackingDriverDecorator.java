package edu.kis.powp.jobs2d.drivers.maintenance;

import edu.kis.powp.jobs2d.features.MonitoringFeature;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * A decorator that wraps any {@link VisitableJob2dDriver} and counts distance travelled
 * (all moves) and distance drawn (ink/filament usage).
 */
public class UsageTrackingDriverDecorator implements VisitableJob2dDriver {

    private final VisitableJob2dDriver delegate;
    private final String label;

    private int lastX = 0;
    private int lastY = 0;
    private double travelDistance = 0.0;
    private double drawingDistance = 0.0;

    private double inkLevel;
    private double maxInkLevel;
    private boolean usageCheckEnabled = false; // for backward compatibility

    private int operationCount = 0;
    private int maxOperations;

    private final List<UsageObserver> observers = new ArrayList<>();

    /**
     * Wraps a driver to track its usage.
     *
     * @param delegate The driver to wrap and track.
     * @param label    A name to identify this driver.
     */
    public UsageTrackingDriverDecorator(VisitableJob2dDriver delegate, String label) {
        this.delegate = delegate;
        this.label = label;
    }

    // second constructor for backward compatibility
    public UsageTrackingDriverDecorator(VisitableJob2dDriver delegate, String label, double maxInk, int maxOps) {
        this.delegate = delegate;
        this.label = label;
        this.maxInkLevel = maxInk;
        this.inkLevel = maxInk;
        this.maxOperations = maxOps;
        this.usageCheckEnabled = true;
    }

    /**
     * Repositions without drawing. Counts as travel distance.
     *
     * @param x The target X coordinate.
     * @param y The target Y coordinate.
     */
    @Override
    public void setPosition(int x, int y) {
        if (usageCheckEnabled && isBroken()) return;

        registerMovement(x, y, false);
        delegate.setPosition(x, y);
        updatePosition(x, y);

        if (usageCheckEnabled) {
            incrementOpCount();
            notifyObservers();
        }
    }

    /**
     * Draws a line to the target position. Counts as both travel and drawing distance.
     *
     * @param x The target X coordinate.
     * @param y The target Y coordinate.
     */
    @Override
    public void operateTo(int x, int y) {
        if (usageCheckEnabled && isBroken()) return;

        if (usageCheckEnabled) {
            double distance = Math.hypot(x - lastX, y - lastY);

            if (distance == 0) {
                delegate.operateTo(x, y);
                return;
            }

            if (inkLevel >= distance) {
                registerMovement(x, y, true);
                delegate.operateTo(x, y);
                this.inkLevel -= distance;
            } else if (inkLevel > 0) {
                // running out of ink during drawing

                // part of line we can draw
                double ratio = inkLevel / distance;

                // coords where the ink will run out
                int stopX = (int) (lastX + (x - lastX) * ratio);
                int stopY = (int) (lastY + (y - lastY) * ratio);

                // draw until ink runs out
                double partialDistance = Math.hypot(stopX - lastX, stopY - lastY);
                travelDistance += partialDistance;
                drawingDistance += partialDistance;

                delegate.operateTo(stopX, stopY);

                updatePosition(stopX, stopY);

                registerMovement(x, y, false);
                delegate.setPosition(x, y);
                this.inkLevel = 0;
            } else {
                // if no ink left - move the head without drawing
                registerMovement(x, y, false);
                delegate.setPosition(x, y);
            }

            incrementOpCount();
            updatePosition(x, y);
            notifyObservers();
        } else {
            registerMovement(x, y, true);
            delegate.operateTo(x, y);
            updatePosition(x, y);
        }
    }

    /**
     * Accumulates distance from the last position.
     *
     * @param x       The target X coordinate.
     * @param y       The target Y coordinate.
     * @param drawing {@code true} if this is a drawing operation; {@code false} for repositioning.
     */
    private void registerMovement(int x, int y, boolean drawing) {
        if (!MonitoringFeature.isMonitoringEnabled()) {
            return;
        }
        double segment = Math.hypot(x - lastX, y - lastY);
        travelDistance += segment;
        if (drawing) {
            drawingDistance += segment;
        }
    }

    /**
     * Updates the current position for the next distance calculation.
     *
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     */
    private void updatePosition(int x, int y) {
        this.lastX = x;
        this.lastY = y;
    }

    /**
     * Returns the total distance travelled (all moves, including draws).
     *
     * @return Travel distance in units.
     */
    public double getTravelDistance() {
        return travelDistance;
    }

    /**
     * Returns the total distance drawn (drawing operations only).
     *
     * @return Drawing distance in units (ink/filament usage).
     */
    public double getDrawingDistance() {
        return drawingDistance;
    }

    /**
     * Returns the label assigned to this decorated driver.
     *
     * @return The label string.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the delegate driver.
     *
     * @return The delegate driver.
     */
    public VisitableJob2dDriver getDelegate() {
        return delegate;
    }

    /**
     * Resets both counters to zero.
     */
    public void reset() {
        travelDistance = 0.0;
        drawingDistance = 0.0;
        lastX = 0;
        lastY = 0;
    }

    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s [tracked]", delegate.toString());
    }

    private void incrementOpCount() {
        operationCount++;
    }

    private boolean isBroken() {
        boolean broken = operationCount >= maxOperations;
        if (broken) notifyObservers();
        return broken;
    }

    public void refillInk() {
        this.inkLevel = maxInkLevel;
        notifyObservers();
    }

    public void performMaintenance() {
        this.operationCount = 0;
        notifyObservers();
    }

    public void addObserver(UsageObserver observer) {
        observers.add(observer);
        observer.updateUsage(inkLevel, maxInkLevel, operationCount, maxOperations);
    }

    private void notifyObservers() {
        for (UsageObserver observer : observers) {
            observer.updateUsage(inkLevel, maxInkLevel, operationCount, maxOperations);
        }
    }
}
