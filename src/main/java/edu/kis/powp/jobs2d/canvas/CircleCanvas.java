package edu.kis.powp.jobs2d.canvas;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Circular canvas.
 */
public class CircleCanvas implements ICanvas {
    
    private final int radius;
    private final int segments;
    private final String name;
    private CanvasMargin margin = CanvasMargin.standard();
    
    public CircleCanvas(int radius, int segments) {
        this.radius = radius;
        this.segments = segments;
        this.name = "Circle r=" + radius;
    }
    
    public CircleCanvas(int radius) {
        this(radius, 64);
    }

    @Override
    public boolean containsPointWithMargin(int x, int y, CanvasMargin margin) {
        int maxMargin = Math.max(
            Math.max(margin.getTop(), margin.getBottom()),
            Math.max(margin.getLeft(), margin.getRight())
        );
        int effectiveRadius = radius - maxMargin;

        if (effectiveRadius <= 0) {
            return false;
        }

        return x * x + y * y <= effectiveRadius * effectiveRadius;
    }
    
    @Override
    public DriverCommand getDriverCommand() {
        CompoundCommand.Builder builder = CompoundCommand.builder().setName(name);

        int startX = (int) (radius * Math.cos(0));
        int startY = (int) (radius * Math.sin(0));
        builder.addSetPosition(startX, startY);
        
        for (int i = 1; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            int x = (int) (radius * Math.cos(angle));
            int y = (int) (radius * Math.sin(angle));
            builder.addOperateTo(x, y);
        }
        
        return builder.build();
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        return x * x + y * y <= radius * radius;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public CanvasMargin getMargin() {
        return margin;
    }

    @Override
    public void setMargin(CanvasMargin margin) {
        this.margin = margin;
    }
}

