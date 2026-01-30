package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.drivers.SelectDriverMenuOptionListener;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class DriverFeature implements IFeature {

    private static DriverManager driverManager = new DriverManager();
    private static Application app;

    public DriverFeature() {
    }

    public static DriverManager getDriverManager() {
        return driverManager;
    }

    private static DriverConfigurationStrategy configStrategy = (name, driver) -> driver;

    @Override
    public void setup(Application application) {
        app = application;
        setupDriverPlugin(application);
    }

    /**
     * Setup jobs2d drivers Plugin and add to application.
     *
     * @param application Application context.
     */
    private static void setupDriverPlugin(Application application) {
        app = application;
        app.addComponentMenu(DriverFeature.class, "Drivers");
        driverManager.getChangePublisher().addSubscriber(DriverFeature::updateDriverInfo);
    }

    /**
     * Add driver to context, create button in driver menu.
     *
     * @param name   Button name.
     * @param driver VisitableJob2dDriver object.
     */
    public static void addDriver(String name, VisitableJob2dDriver driver) {
        VisitableJob2dDriver finalDriver = configStrategy.configure(name, driver);

        SelectDriverMenuOptionListener listener = new SelectDriverMenuOptionListener(finalDriver, driverManager);
        app.addComponentMenuElement(DriverFeature.class, name, listener);
    }

    /**
     * Update driver info.
     */
    public static void updateDriverInfo() {
        app.updateInfo(driverManager.getCurrentDriver().toString());
    }

    public static void setConfigurationStrategy(DriverConfigurationStrategy strategy) {
        configStrategy = strategy;
    }

    public static DriverConfigurationStrategy getConfigurationStrategy() {
        return configStrategy;
    }

    @Override
    public String getName() {
        return "Driver";
    }

}
