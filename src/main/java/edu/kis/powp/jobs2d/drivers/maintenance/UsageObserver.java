package edu.kis.powp.jobs2d.drivers.maintenance;

public interface UsageObserver {
    void updateUsage(double inkLevel, double maxInk, int opCount, int maxOp);
}
