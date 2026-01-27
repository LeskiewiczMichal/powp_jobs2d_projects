package edu.kis.powp.jobs2d.features;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.UsageTrackingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.transformation.DriverFeatureFactory;
import edu.kis.powp.jobs2d.events.SelectLoadRecordedCommandOptionListener;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.drivers.SelectDriverMenuOptionListener;
import java.util.Arrays;
import java.util.logging.Logger;

public class DriverFeature {

    private static DriverManager driverManager = new DriverManager();
    private static Application app;

    public static DriverManager getDriverManager() {
        return driverManager;
    }

    /**
     * Setup jobs2d drivers Plugin and add to application.
     * 
     * @param application Application context.
     */
    public static void setupDriverPlugin(Application application, Logger logger) {
        app = application;
        app.addComponentMenu(DriverFeature.class, "Drivers");
        driverManager.getChangePublisher().addSubscriber(DriverFeature::updateDriverInfo);
        setupDefaultDrivers(application, logger);
    }

    /**
     * Add driver to context, create button in driver menu.
     * 
     * @param name   Button name.
     * @param driver VisitableJob2dDriver object.
     */
    public static void addDriver(String name, VisitableJob2dDriver driver) {
        SelectDriverMenuOptionListener listener = new SelectDriverMenuOptionListener(driver, driverManager);
        app.addComponentMenuElement(DriverFeature.class, name, listener);
    }

    /**
     * Update driver info.
     */
    public static void updateDriverInfo() {
        app.updateInfo(driverManager.getCurrentDriver().toString());
    }

    /**
     * Setup driver manager, and set default VisitableJob2dDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDefaultDrivers(Application application, Logger logger) {
        VisitableJob2dDriver loggerDriver = new LoggerDriver(logger);
        DriverFeature.addDriver("Logger driver", loggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableJob2dDriver basicLineDriver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Basic line Simulator", basicLineDriver);

        AnimatedDriverDecorator slowAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        slowAnimatedDriverDecorator.setSpeedSlow();
        DriverFeature.addDriver("Animated Line - slow", slowAnimatedDriverDecorator);

        AnimatedDriverDecorator mediumAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        mediumAnimatedDriverDecorator.setSpeedMedium();
        DriverFeature.addDriver("Animated Line - medium speed", mediumAnimatedDriverDecorator);

        AnimatedDriverDecorator fastAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        fastAnimatedDriverDecorator.setSpeedFast();
        DriverFeature.addDriver("Animated Line - fast", fastAnimatedDriverDecorator);

        VisitableJob2dDriver specialLineDriver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", specialLineDriver);

        VisitableJob2dDriver basicLineWithLoggerDriver = new DriverComposite(
            Arrays.asList(basicLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Basic line", basicLineWithLoggerDriver);

        VisitableJob2dDriver specialLineWithLoggerDriver = new DriverComposite(Arrays.asList(specialLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Special line", specialLineWithLoggerDriver);

        RecordingDriverDecorator recordingDriver = new RecordingDriverDecorator(basicLineDriver);
        SelectLoadRecordedCommandOptionListener selectLoadRecordedCommandOptionListener = new SelectLoadRecordedCommandOptionListener(recordingDriver);
        application.addTest("Stop recording & Load recorded command", selectLoadRecordedCommandOptionListener);
        DriverFeature.addDriver("Recording Driver", recordingDriver);

        // Add monitored versions of drivers
        UsageTrackingDriverDecorator monitoredBasicLine = new UsageTrackingDriverDecorator(basicLineDriver, "Basic line [monitored]");
        MonitoringFeature.registerMonitoredDriver("Basic line [monitored]", monitoredBasicLine);
        DriverFeature.addDriver("Basic line [monitored]", monitoredBasicLine);

        UsageTrackingDriverDecorator monitoredSpecialLine = new UsageTrackingDriverDecorator(specialLineDriver, "Special line [monitored]");
        MonitoringFeature.registerMonitoredDriver("Special line [monitored]", monitoredSpecialLine);
        DriverFeature.addDriver("Special line [monitored]", monitoredSpecialLine);

        // Set default driver
        DriverFeature.getDriverManager().setCurrentDriver(basicLineDriver);
        VisitableJob2dDriver rotatedDriver = DriverFeatureFactory.createRotateDriver(basicLineDriver, 45);
        DriverFeature.addDriver("Basic Line + Rotate 45", rotatedDriver);

        VisitableJob2dDriver scaledDriver = DriverFeatureFactory.createScaleDriver(basicLineDriver, 2.0);
        DriverFeature.addDriver("Basic Line + Scale 2x", scaledDriver);

        VisitableJob2dDriver flippedDriver = DriverFeatureFactory.createFlipDriver(basicLineDriver, true, false);
        DriverFeature.addDriver("Basic Line + Flip Horizontal", flippedDriver);
    }

}
