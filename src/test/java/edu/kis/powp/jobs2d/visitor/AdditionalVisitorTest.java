package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;

public class AdditionalVisitorTest {
    public static void main(String[] args) {
        testCounting();
        testDeepCopy();
        System.out.println("Additional Tests PASSED");
    }

    private static void testCounting() {
        System.out.println("Testing UsageTracking Counting...");
        LoggerDriver logger = new LoggerDriver();
        UsageTrackingDriverDecorator tracked = new UsageTrackingDriverDecorator(logger, "tracked");

        DriverCounterVisitor.DriverStats stats = DriverCounterVisitor.countDrivers(tracked);
        if (stats.getUsageTrackingDriverDecoratorCount() != 1) {
            throw new RuntimeException(
                    "Expected 1 usage tracking driver, got " + stats.getUsageTrackingDriverDecoratorCount());
        }
        System.out.println("Counting OK");
    }

    private static void testDeepCopy() {
        System.out.println("Testing UsageTracking Deep Copy...");
        LoggerDriver logger = new LoggerDriver();
        UsageTrackingDriverDecorator tracked = new UsageTrackingDriverDecorator(logger, "tracked");

        VisitableJob2dDriver copy = DriverDeepCopyVisitor.createDeepCopyOf(tracked);

        if (!(copy instanceof UsageTrackingDriverDecorator)) {
            throw new RuntimeException("Copy is not UsageTrackingDriverDecorator");
        }
        UsageTrackingDriverDecorator copyTracked = (UsageTrackingDriverDecorator) copy;
        if (copyTracked == tracked) {
            throw new RuntimeException("Copy is same instance");
        }
        if (copyTracked.getDelegate() == tracked.getDelegate()) {
            throw new RuntimeException("Delegate should be deep copied");
        }
        if (!copyTracked.getLabel().equals(tracked.getLabel())) {
            throw new RuntimeException("Label mismatch");
        }
        System.out.println("Deep Copy OK");
    }
}
