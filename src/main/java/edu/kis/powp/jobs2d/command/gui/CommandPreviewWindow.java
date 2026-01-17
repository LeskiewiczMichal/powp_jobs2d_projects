package edu.kis.powp.jobs2d.command.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.canvas.CanvasManager;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.features.CanvasLayerPanel;

public class CommandPreviewWindow extends JFrame implements WindowComponent {
    
    private static final long serialVersionUID = 1L;
    
    private final CanvasManager canvasManager;
    private final JPanel previewContainerPanel;
    private final JPanel drawingPanel;
    private final CanvasLayerPanel canvasOverlay;
    private final DrawPanelController drawPanelController;

    public CommandPreviewWindow() {
        this(CanvasFeature.getCanvasManager());
    }

    public CommandPreviewWindow(CanvasManager canvasManager) {
        this.setTitle("Command Preview");
        this.setSize(400, 400);
        this.canvasManager = canvasManager;
        
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());

        this.previewContainerPanel = new JPanel();
        this.previewContainerPanel.setLayout(new OverlayLayout(this.previewContainerPanel));

        this.drawingPanel = new JPanel(new BorderLayout());
        this.drawingPanel.setBackground(Color.WHITE);
        this.drawingPanel.setOpaque(true);

        this.canvasOverlay = new CanvasLayerPanel();
        
        this.previewContainerPanel.add(this.canvasOverlay);
        this.previewContainerPanel.add(this.drawingPanel);

        content.add(previewContainerPanel, BorderLayout.CENTER);

        this.drawPanelController = new DrawPanelController();
        this.drawPanelController.initialize(drawingPanel);
        
        refreshCanvasOverlay();
        canvasManager.getChangePublisher().addSubscriber(this::refreshCanvasOverlay);
    }

    public void updatePreview(DriverCommand command) {
        refreshCanvasOverlay();
        drawPanelController.clearPanel();
        
        if (command != null) {
            LineDriverAdapter driver = new LineDriverAdapter(drawPanelController, LineFactory.getBasicLine(), "preview");
            command.execute(driver);
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

    private void refreshCanvasOverlay() {
        canvasOverlay.setCanvas(canvasManager.getCurrentCanvas());
        canvasOverlay.revalidate();
        canvasOverlay.repaint();
    }
}