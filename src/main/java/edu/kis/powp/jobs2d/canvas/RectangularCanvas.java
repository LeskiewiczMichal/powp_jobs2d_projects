package edu.kis.powp.jobs2d.canvas;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;

public class RectangularCanvas implements ICanvas {
    
    private final int width;
    private final int height;
    private final String name;
    private CanvasMargin margin = CanvasMargin.standard();
    
    public RectangularCanvas(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
    }
    
    @Override
    public DriverCommand getDriverCommand() {
        int halfW = width / 2;
        int halfH = height / 2;
        
        return CompoundCommand.builder()
            .setName(name)
            .addSetPosition(-halfW, -halfH)
            .addOperateTo(halfW, -halfH)
            .addOperateTo(halfW, halfH)
            .addOperateTo(-halfW, halfH)
            .addOperateTo(-halfW, -halfH)
            .build();
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        return Math.abs(x) <= width / 2 && Math.abs(y) <= height / 2;
    }

    @Override
    public boolean containsPointWithMargin(int x, int y, CanvasMargin margin) {
        int effectiveHalfWidth = width / 2 - Math.max(margin.getLeft(), margin.getRight());
        int effectiveHalfHeight = height / 2 - Math.max(margin.getTop(), margin.getBottom());

        return Math.abs(x) <= effectiveHalfWidth && Math.abs(y) <= effectiveHalfHeight;
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

