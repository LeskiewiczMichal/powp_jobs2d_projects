package edu.kis.powp.jobs2d.command.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.transformation.ScaleStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.TransformerDriverDecorator;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.visitor.CanvasBoundsCheckVisitor;

public class CommandPreviewWindow extends JFrame implements WindowComponent {
    
    private static final long serialVersionUID = 1L;
    
    private final JPanel previewContainerPanel;
    private final JPanel drawingPanel;
    private final DrawPanelController drawPanelController;
    private final ScaleStrategy previewScaleStrategy;

    private final JLabel statusLabel;
    private final LineDriverAdapter driver;
    private final TransformerDriverDecorator scaledDriver;
    
    private final CommandValidator commandValidator;
    private final ValidationStatusUpdater statusUpdater;   

    public CommandPreviewWindow() {
        this.setTitle("Command Preview");
        this.setSize(400, 420);
        previewScaleStrategy = new ScaleStrategy(0.5);
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());

        this.statusLabel = new JLabel("");
        this.statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(statusLabel, BorderLayout.SOUTH);

        this.previewContainerPanel = new JPanel();
        this.previewContainerPanel.setLayout(new OverlayLayout(this.previewContainerPanel));

        this.drawingPanel = new JPanel(new BorderLayout());
        this.drawingPanel.setBackground(Color.WHITE);
        this.drawingPanel.setOpaque(true);
        
        CanvasFeature.attachCanvasOverlay(this.previewContainerPanel, previewScaleStrategy);
        this.previewContainerPanel.add(this.drawingPanel);

        content.add(previewContainerPanel, BorderLayout.CENTER);

        this.drawPanelController = new DrawPanelController();
        this.drawPanelController.initialize(drawingPanel);

        this.driver = new LineDriverAdapter(drawPanelController, LineFactory.getBasicLine(), "preview");
        this.scaledDriver = new TransformerDriverDecorator(driver, previewScaleStrategy);
        
        this.commandValidator = new CommandValidator();
        this.statusUpdater = new ValidationStatusUpdater();
    }

    public void updatePreview(DriverCommand command) {
        drawPanelController.clearPanel();
        statusLabel.setText("");
        statusLabel.setForeground(Color.BLACK);

        if (command != null) {
            ICanvas canvas = CanvasFeature.getCanvasManager().getCurrentCanvas();
            if (canvas != null) {
                CanvasBoundsCheckVisitor.BoundsCheckResult validationResult = commandValidator.validate(command, canvas);
                statusUpdater.updateStatus(statusLabel, validationResult);
                command.execute(scaledDriver);
            }
        }
        previewContainerPanel.revalidate();
        previewContainerPanel.repaint();
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }

    
}