package edu.kis.powp.jobs2d.drivers.maintenance;

public interface DeviceStatusView {
    void updateInkLevel(double currentLevel, double maxLevel);
    void updateServiceState(int usageCount, int maxUsage);
    void showWarning(String message);
    void showError(String message);
}