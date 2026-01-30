package edu.kis.powp.jobs2d.drivers.maintenance;

public class DeviceUsageMonitor implements UsageObserver {

    private final DeviceStatusView view;
    private final int lowInkThresholdPct;

    private boolean emptyInkWarningShown = false;
    private boolean lowInkWarningShown = false;
    private boolean maintenanceWarningShown = false;

    public DeviceUsageMonitor(DeviceStatusView view, int lowInkThresholdPct) {
        this.view = view;
        this.lowInkThresholdPct = lowInkThresholdPct;
    }

    @Override
    public void updateUsage(double inkLevel, double maxInkLevel, int operationCount, int maxOperations) {
        view.updateInkLevel(inkLevel, maxInkLevel);
        view.updateServiceState(operationCount, maxOperations);

        if (maxInkLevel > 0) {
            int pct = (int) ((inkLevel / maxInkLevel) * 100);

            if (pct < lowInkThresholdPct && !lowInkWarningShown) {
                lowInkWarningShown = true;
                view.showWarning("Warning: Ink level below " + lowInkThresholdPct + "%!");
            }
            if (inkLevel <= 0 && !emptyInkWarningShown) {
                emptyInkWarningShown = true;
                view.showError("Error: Ink Depleted. Cannot draw anymore.");
            }

            if (pct >= lowInkThresholdPct) lowInkWarningShown = false;
            if (inkLevel > 0) emptyInkWarningShown = false;
        }

        if (maxOperations > 0) {
            if (operationCount >= maxOperations && !maintenanceWarningShown) {
                maintenanceWarningShown = true;
                view.showError("Error: Mechanism jammed");
            }
            if (operationCount < maxOperations) maintenanceWarningShown = false;
        }
    }
}