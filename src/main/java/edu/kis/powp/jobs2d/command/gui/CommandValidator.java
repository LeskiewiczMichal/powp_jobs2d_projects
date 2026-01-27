package edu.kis.powp.jobs2d.command.gui;

import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.visitor.CanvasBoundsCheckVisitor;

/**
 * Helper class responsible for validating commands against canvas bounds.
 */
public class CommandValidator {

    /**
     * Validates a command against canvas bounds.
     * 
     * @param command the command to validate
     * @param canvas the canvas to validate against
     * @return validation result containing violation information
     */
    public CanvasBoundsCheckVisitor.BoundsCheckResult validate(DriverCommand command, ICanvas canvas) {
        if (command == null || canvas == null) {
            return null;
        }
        
        return CanvasBoundsCheckVisitor.checkCanvasAndMargins(command, canvas, canvas.getMargin());
    }
}
