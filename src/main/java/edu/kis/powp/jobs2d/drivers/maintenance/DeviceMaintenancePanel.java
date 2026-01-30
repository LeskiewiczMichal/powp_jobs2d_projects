package edu.kis.powp.jobs2d.drivers.maintenance;

import edu.kis.powp.appbase.gui.WindowComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DeviceMaintenancePanel extends JFrame implements DeviceStatusView, WindowComponent {

    private final JProgressBar inkProgressBar;
    private final JProgressBar healthProgressBar;

    public DeviceMaintenancePanel(ActionListener refillAction, ActionListener repairAction) {
        setTitle("Device Maintenance");
        setSize(400, 200);
        setLayout(new GridLayout(4, 1, 5, 5));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.inkProgressBar = setupSection("  Ink Level:", "Refill", refillAction);
        this.healthProgressBar = setupSection("  Device Health:", "Maintenance", repairAction);

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

    @Override
    public void updateInkLevel(double currentLevel, double maxLevel) {
        SwingUtilities.invokeLater(() -> {
            if (maxLevel > 0) {
                int pct = (int) ((currentLevel / maxLevel) * 100);
                updateBar(inkProgressBar, pct, ((int) currentLevel + " / " + (int) maxLevel + " units"));
            } else {
                updateBar(inkProgressBar, 100, "N/A");
            }
        });
    }

    @Override
    public void updateServiceState(int usageCount, int maxUsage) {
        SwingUtilities.invokeLater(() -> {
            if (maxUsage > 0) {
                double health = Math.max(0, 100.0 * (1.0 - ((double) usageCount / maxUsage)));
                updateBar(healthProgressBar, (int) health, ("Health: " + (int) health + "% Operations: " + usageCount));
            } else {
                updateBar(healthProgressBar, 100, "N/A");
            }
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
    public void showWarning(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));
    }

    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        setVisible(!isVisible());
    }
}