package edu.kis.powp.jobs2d.features;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.canvas.CanvasManager;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.transformation.TransformStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.TransformerDriverDecorator;
import edu.kis.powp.jobs2d.events.SelectCanvasOptionListener;

public class CanvasFeature implements IFeature {
    @Override
    public void setup(Application app) {
        setupCanvasPlugin(app);
    }

    @Override
    public String getName() {
        return "Canvas";
    }

    private static CanvasManager canvasManager = new CanvasManager();
    private static CanvasLayerPanel canvasOverlay;
    private static Application app;

    public static CanvasManager getCanvasManager() {
        return canvasManager;
    }

    public static DrawPanelController getCanvasDrawer() {
        return canvasOverlay.getController();
    }

    public static CanvasLayerPanel attachCanvasOverlay(JComponent panel) {
        return attachCanvasOverlay(panel, null);
    }

    public static CanvasLayerPanel attachCanvasOverlay(JComponent panel, TransformStrategy transform) {
        // OverlayLayout is used to add the canvas overlay on top of the drawing panel
        // so changing the canvas will not affect the drawing panel
        if (!(panel.getLayout() instanceof OverlayLayout)) {
            panel.setLayout(new OverlayLayout(panel));
        }

        CanvasLayerPanel overlay = new CanvasLayerPanel();
        if (transform != null) {
            LineDriverAdapter baseDriver = new LineDriverAdapter(overlay.getController(), LineFactory.getSpecialLine(), "canvas");
            overlay.setDriver(new TransformerDriverDecorator(baseDriver, transform));
        }
        overlay.setCanvas(canvasManager.getCurrentCanvas());
        canvasManager.getChangePublisher().addSubscriber(() -> {
            overlay.setCanvas(canvasManager.getCurrentCanvas());
            overlay.revalidate();
            overlay.repaint();
        });
        panel.add(overlay);

        return overlay;
    }

    public static void setupCanvasPlugin(Application app) {
        CanvasFeature.app = app;
        JPanel panel = ViewFeature.getDrawingPanel();
        if (panel == null) {
            panel = app.getFreePanel();
        }
        canvasOverlay = attachCanvasOverlay(panel);

        app.addComponentMenu(CanvasFeature.class, "Canvas");
    }

    public static void addCanvas(ICanvas canvas) {
        app.addComponentMenuElement(CanvasFeature.class, canvas.getName(), new SelectCanvasOptionListener(canvas));
    }

    public static void addCanvas(String name, ICanvas canvas) {
        app.addComponentMenuElement(CanvasFeature.class, name, new SelectCanvasOptionListener(canvas));
    }
}
