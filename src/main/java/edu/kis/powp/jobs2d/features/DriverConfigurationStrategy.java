package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

/**
 * Strategy interface for configuring {@link VisitableJob2dDriver} instances.
 *
 * <p>Implementations may wrap the provided driver with additional behavior
 * (e.g. monitoring, logging, validation) and return either the original
 * or a decorated instance.</p>
 *
 * <p>This abstraction allows the application to apply different driver
 * configuration policies without hard-coding specific decorators.</p>
 */
@FunctionalInterface
public interface DriverConfigurationStrategy {

  /**
   * Configures the given driver instance.
   *
   * @param name   logical name or label of the driver
   * @param driver original driver instance
   * @return configured (possibly decorated) driver
   */
  VisitableJob2dDriver configure(String name, VisitableJob2dDriver driver);
}
