package edu.kis.powp.jobs2d.command.gui;

import edu.kis.powp.jobs2d.visitor.CanvasBoundsCheckVisitor;

import javax.swing.*;
import java.awt.*;

/**
 * Helper class responsible for updating status labels based on validation results.
 */
public class ValidationStatusUpdater {

    /**
     * Updates the status label based on validation results.
     * 
     * @param statusLabel the label to update
     * @param result the validation result
     */
    public void updateStatus(JLabel statusLabel, CanvasBoundsCheckVisitor.BoundsCheckResult result) {
        if (result == null) {
            statusLabel.setText("");
            statusLabel.setForeground(Color.BLACK);
            return;
        }

        if (result.hasCanvasViolations()) {
            statusLabel.setText("Warning: Command exceeds canvas boundaries!");
            statusLabel.setForeground(Color.RED);
        } else if (result.hasMarginViolations()) {
            statusLabel.setText("Warning: Command exceeds canvas margin!");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("");
            statusLabel.setForeground(Color.BLACK);
        }
    }
}
