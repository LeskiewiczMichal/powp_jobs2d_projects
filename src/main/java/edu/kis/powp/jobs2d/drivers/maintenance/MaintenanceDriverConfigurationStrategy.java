package edu.kis.powp.jobs2d.drivers.maintenance;

import edu.kis.powp.jobs2d.features.DriverConfigurationStrategy;
import edu.kis.powp.jobs2d.features.MonitoringFeature;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class MaintenanceDriverConfigurationStrategy implements DriverConfigurationStrategy {

    private final double maxInk;
    private final int maxOps;

    private UsageTrackingDriverDecorator createdDecorator;

    public MaintenanceDriverConfigurationStrategy(double maxInk, int maxOps) {
        this.maxInk = maxInk;
        this.maxOps = maxOps;
    }

    @Override
    public VisitableJob2dDriver configure(String name, VisitableJob2dDriver driver) {
        this.createdDecorator = new UsageTrackingDriverDecorator(driver, name, maxInk, maxOps);
        MonitoringFeature.registerMonitoredDriver(name, this.createdDecorator);
        return this.createdDecorator;
    }

    public UsageTrackingDriverDecorator getCreatedDecorator() {
        return createdDecorator;
    }
}