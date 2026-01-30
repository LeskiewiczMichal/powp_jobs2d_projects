package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.drivers.maintenance.DeviceMaintenancePanel;
import edu.kis.powp.jobs2d.drivers.maintenance.DeviceUsageMonitor;
import edu.kis.powp.jobs2d.drivers.maintenance.MaintenanceDriverConfigurationStrategy;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;

/**
 * Provides monitoring capabilities for tracked drivers. Users can select drivers
 * with monitoring enabled from the driver menu, and use this feature to view
 * usage summaries and reset counters.
 */
public class MonitoringFeature implements IFeature {

    /** Holds all registered monitored drivers by their label. */
    private static Map<String, UsageTrackingDriverDecorator> monitoredDrivers = new HashMap<>();

    /** Target logger where summaries are printed. */
    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static boolean monitoringEnabled = true;

    public MonitoringFeature() {
    }

    public MonitoringFeature(Logger customLogger) {
        if (customLogger != null) {
            logger = customLogger;
        }
    }

    @Override
    public void setup(Application app) {

        app.addComponentMenu(MonitoringFeature.class, "Monitoring", 0);
        app.addComponentMenuElementWithCheckBox(MonitoringFeature.class, "Toggle Monitoring",
            (ActionEvent e) -> monitoringEnabled = !monitoringEnabled, true);
        app.addComponentMenuElement(MonitoringFeature.class, "Report usage summary", MonitoringFeature::logUsage);
        app.addComponentMenuElement(MonitoringFeature.class, "Reset counters", MonitoringFeature::resetCounters);
    }

    /**
     * Registers a tracked driver so it can be monitored and reset.
     *
     * @param label  The label identifying this driver.
     * @param driver The tracked driver to register.
     */
    public static void registerMonitoredDriver(String label, UsageTrackingDriverDecorator driver) {
        monitoredDrivers.put(label, driver);
    }

    /**
     * Menu action that prints usage summaries for all monitored drivers.
     * Reports the total travel distance and drawing distance (ink/filament usage)
     * for each registered driver.
     *
     * @param e The action event triggered by the menu selection.
     */
    private static void logUsage(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        logger.info("=== Usage Summary ===");
        for (Map.Entry<String, UsageTrackingDriverDecorator> entry : monitoredDrivers.entrySet()) {
            UsageTrackingDriverDecorator driver = entry.getValue();
            logger.info(String.format("[%s] travel=%.2f, ink=%.2f",
                    driver.getLabel(), driver.getTravelDistance(), driver.getDrawingDistance()));
        }
    }

    /**
     * Menu action that resets usage counters on all monitored drivers.
     * Sets both travel distance and drawing distance to zero for each.
     *
     * @param e The action event triggered by the menu selection.
     */
    private static void resetCounters(ActionEvent e) {
        if (monitoredDrivers.isEmpty()) {
            logger.info("Monitoring: no monitored drivers registered");
            return;
        }
        for (UsageTrackingDriverDecorator driver : monitoredDrivers.values()) {
            driver.reset();
        }
        logger.info("Monitoring: all counters reset");
    }

    public static boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    @Override
    public String getName() {
        return "Monitoring";
    }

    /**
     * Facade method to simplify adding a maintenance-enabled driver .
     */

    public static void addDriverWithMaintenance(Application application, VisitableJob2dDriver job2dDriver, double maxInkLevel, int maxOperations, int inkThresholdWarning) {
        // Temporary default strategy change to maintenance strategy
        DriverConfigurationStrategy defaultStrategy = DriverFeature.getConfigurationStrategy();
        MaintenanceDriverConfigurationStrategy maintenanceStrategy = new MaintenanceDriverConfigurationStrategy(maxInkLevel, maxOperations);

        DriverFeature.setConfigurationStrategy(maintenanceStrategy);

        String driverName = job2dDriver.toString();
        DriverFeature.addDriver("Device Maintenance simulation (driver: " + driverName + ")", job2dDriver);

        UsageTrackingDriverDecorator maintenanceDriver = maintenanceStrategy.getCreatedDecorator();
        DeviceMaintenancePanel devicePanel = new DeviceMaintenancePanel(
                e -> maintenanceDriver.refillInk(),
                e -> maintenanceDriver.performMaintenance()
        );

        application.addWindowComponent("Device Maintenance", devicePanel);

        DeviceUsageMonitor monitor = new DeviceUsageMonitor(devicePanel, inkThresholdWarning);
        maintenanceDriver.addObserver(monitor);

        // Bring back default strategy
        DriverFeature.setConfigurationStrategy(defaultStrategy);
    }
}
