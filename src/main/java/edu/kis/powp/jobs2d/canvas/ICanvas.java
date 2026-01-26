package edu.kis.powp.jobs2d.canvas;

import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Interface defining a canvas with boundaries.
 */
public interface ICanvas {
    
    /**
     * Gets the command that draws the canvas boundary.
     */
    DriverCommand getDriverCommand();
    
    /**
     * Checks if a point is within the canvas.
     */
    boolean containsPoint(int x, int y);

     /**
     * Checks if a point is within the canvas considering margins
     */
    boolean containsPointWithMargin(int x, int y, CanvasMargin margin);
    
    /**
     * Gets canvas name.
     */
    String getName();

    /**
     * Gets current canvas margin.
     */
    CanvasMargin getMargin();

    /**
     * Sets canvas margin.
     */
    void setMargin(CanvasMargin margin);
}
