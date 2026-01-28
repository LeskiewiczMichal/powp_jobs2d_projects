package edu.kis.powp.jobs2d.visitor;

/**
 * Represents a violation of canvas or margin bounds.
 */
public class BoundsViolation {
    public enum ViolationType {
        CANVAS_EXCEEDED,
        MARGIN_EXCEEDED
    }

    private final ViolationType type;
    private final int x;
    private final int y;
    private final String commandDescription;

    public BoundsViolation(ViolationType type, int x, int y, String commandDescription) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.commandDescription = commandDescription;
    }

    public ViolationType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String toString() {
        return String.format("%s at (%d, %d) - %s", 
            type, x, y, commandDescription);
    }
}
