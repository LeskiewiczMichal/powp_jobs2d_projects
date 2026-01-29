package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.visitor.BoundsViolation;
import edu.kis.powp.jobs2d.visitor.CanvasBoundsCheckVisitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Listener for validating current command against canvas bounds.
 * Logs all violations found.
 */
public class SelectValidateCanvasBoundsOptionListener implements ActionListener {

    private final CommandManager commandManager;
    private final Logger logger;

    public SelectValidateCanvasBoundsOptionListener(CommandManager commandManager, Logger logger) {
        this.commandManager = commandManager;
        this.logger = logger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand currentCommand = commandManager.getCurrentCommand();
        
        if (currentCommand == null) {
            logger.warning("No command loaded to validate!");
            return;
        }

        ICanvas canvas = CanvasFeature.getCanvasManager().getCurrentCanvas();
        
        if (canvas == null) {
            logger.warning("No canvas selected! Please select a canvas to validate against.");
            return;
        }

        // Perform validation
        CanvasBoundsCheckVisitor.BoundsCheckResult result = 
            CanvasBoundsCheckVisitor.checkCanvasAndMargins(currentCommand, canvas, canvas.getMargin());

        if (!result.hasAnyViolations()) {
            logger.info("VALIDATION PASSED: All command points are within canvas bounds and margins.");
        } else {
            logger.warning("VALIDATION FAILED: Found " + result.getViolationCount() + " violation(s)");
            
            if (result.hasCanvasViolations()) {
                logger.severe("CANVAS BOUNDARY VIOLATIONS:");
                result.getViolations().stream()
                    .filter(v -> v.getType() == BoundsViolation.ViolationType.CANVAS_EXCEEDED)
                    .forEach(v -> logger.severe("  - " + v.toString()));
            }
            
            if (result.hasMarginViolations()) {
                logger.warning("MARGIN VIOLATIONS:");
                result.getViolations().stream()
                    .filter(v -> v.getType() == BoundsViolation.ViolationType.MARGIN_EXCEEDED)
                    .forEach(v -> logger.warning("  - " + v.toString()));
            }
        }
    }
}
