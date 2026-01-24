package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.appbase.gui.WindowComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DeviceMaintenancePanel extends JFrame implements InkUsageObserver, ServiceObserver, WindowComponent {

    private final JProgressBar inkProgressBar;
    private InkUsageDriverDecorator inkDecorator;
    private boolean emptyInkWarningShown = false;
    private boolean lowInkWarningShown = false;

    private final JProgressBar healthProgressBar;
    private ServiceDriverDecorator maintenanceDecorator;
    private boolean maintenanceWarningShown = false;

    public DeviceMaintenancePanel() {
        setTitle("Device Maintenance");
        setSize(400, 200);
        setLayout(new GridLayout(4, 1, 5, 5));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.inkProgressBar = setupSection("Ink Level:", "Refill", e -> {
            if (inkDecorator != null) inkDecorator.refill();
            JOptionPane.showMessageDialog(this, "Ink refilled");
        });

        this.healthProgressBar = setupSection("Device Health:", "Maintenance", e -> {
            if (maintenanceDecorator != null) maintenanceDecorator.performMaintenance();
            JOptionPane.showMessageDialog(this, "Device repaired");
        });

        setVisible(false);
    }

    private JProgressBar setupSection(String labelText, String btnText, ActionListener action) {
        add(new JLabel(labelText, SwingConstants.LEFT));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(200, 25));

        JButton btn = new JButton(btnText);
        btn.addActionListener(action);

        panel.add(bar);
        panel.add(btn);
        add(panel);

        return bar;
    }

    public void setInkDecorator(InkUsageDriverDecorator inkDecorator) {
        this.inkDecorator = inkDecorator;
        this.inkDecorator.addObserver(this);
    }

    public void setMaintenanceDecorator(ServiceDriverDecorator maintenanceDecorator) {
        this.maintenanceDecorator = maintenanceDecorator;
        this.maintenanceDecorator.addObserver(this);
    }

    @Override
    public void updateInkLevel(double currentLevel, double maxLevel) {
        SwingUtilities.invokeLater(() -> {
            int pct = (int) ((currentLevel / maxLevel) * 100);
            updateBar(inkProgressBar, pct, ((int) currentLevel + " / " + (int) maxLevel + " units"));

            if (pct < 40 && !lowInkWarningShown) {
                lowInkWarningShown = true;
                JOptionPane.showMessageDialog(this, "Warning: Ink level below 40%!");
            }
            if (currentLevel <= 0 && !emptyInkWarningShown) {
                emptyInkWarningShown = true;
                JOptionPane.showMessageDialog(this, "Error: Ink Depleted. Cannot draw anymore.");
            }

            if (pct >= 40) lowInkWarningShown = false;
            if (currentLevel > 0) emptyInkWarningShown = false;
        });
    }

    @Override
    public void updateServiceState(int usageCount, int maxUsage) {
        SwingUtilities.invokeLater(() -> {
            double health = Math.max(0, 100.0 * (1.0 - ((double) usageCount / maxUsage)));
            updateBar(healthProgressBar, (int) health, ("Health: " + (int)health + "% Operations: " + usageCount));

            if (usageCount >= maxUsage && !maintenanceWarningShown) {
                maintenanceWarningShown = true;
                JOptionPane.showMessageDialog(this, "Error: Mechanism jammed");
            }
            if (usageCount < maxUsage) maintenanceWarningShown = false;
        });
    }

    private void updateBar(JProgressBar bar, int value, String text) {
        bar.setValue(value);
        bar.setString(text);
        if (value < 20) bar.setForeground(Color.RED);
        else if (value < 40) bar.setForeground(Color.ORANGE);
        else bar.setForeground(new Color(0, 150, 0));
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        setVisible(!isVisible());
    }
}
