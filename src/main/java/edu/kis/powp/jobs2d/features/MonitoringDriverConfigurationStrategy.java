package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

/**
 * Driver configuration strategy that enables usage monitoring.
 *
 * <p>Wraps drivers with {@link UsageTrackingDriverDecorator} and registers
 * them in {@link MonitoringFeature} so their usage statistics can be
 * reported and reset via the Monitoring menu.</p>
 */
public class MonitoringDriverConfigurationStrategy implements DriverConfigurationStrategy {

  @Override
  public VisitableJob2dDriver configure(String name, VisitableJob2dDriver driver) {
    UsageTrackingDriverDecorator monitoredDriver =
        new UsageTrackingDriverDecorator(driver, name);

    MonitoringFeature.registerMonitoredDriver(name, monitoredDriver);
    return monitoredDriver;
  }
}