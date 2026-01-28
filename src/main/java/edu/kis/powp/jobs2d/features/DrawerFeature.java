package edu.kis.powp.jobs2d.features;

import javax.swing.JPanel;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.events.SelectClearPanelOptionListener;

public class DrawerFeature implements IFeature {

    private static DrawPanelController drawerController;

    public DrawerFeature() {
    }

    @Override
    public void setup(Application application) {
        setupDrawerPlugin(application);
    }

    /**
     * Setup Drawer Plugin and add to application.
     *
     * @param application Application context.
     */
    private static void setupDrawerPlugin(Application application) {
        SelectClearPanelOptionListener selectClearPanelOptionListener = new SelectClearPanelOptionListener();

        drawerController = new DrawPanelController();
        application.addComponentMenu(DrawPanelController.class, "Draw Panel", 0);
        application.addComponentMenuElement(DrawPanelController.class, "Clear Panel", selectClearPanelOptionListener);

        JPanel panel = ViewFeature.getDrawingPanel();
        if (panel == null) {
            panel = application.getFreePanel();
        }
        drawerController.initialize(panel);
    }

    /**
     * Get controller of application drawing panel.
     *
     * @return drawPanelController.
     */
    public static DrawPanelController getDrawerController() {
        return drawerController;
    }

    @Override
    public String getName() {
        return "Drawer";
    }
}
