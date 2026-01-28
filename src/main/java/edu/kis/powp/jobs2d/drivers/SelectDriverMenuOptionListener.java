package edu.kis.powp.jobs2d.drivers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class SelectDriverMenuOptionListener implements ActionListener {
    private DriverManager driverManager;
    private VisitableJob2dDriver driver = null;

    public SelectDriverMenuOptionListener(VisitableJob2dDriver driver, DriverManager driverManager) {
        this.driverManager = driverManager;
        this.driver = driver;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        driverManager.setCurrentDriver(driver);
    }
}
