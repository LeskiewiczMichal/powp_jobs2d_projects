package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindow;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindowObserver;
import edu.kis.powp.jobs2d.command.gui.SelectImportCommandOptionListener;
import edu.kis.powp.jobs2d.command.importer.JsonCommandImportParser;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.events.*;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.MonitoringFeature;
import edu.kis.powp.jobs2d.features.ViewFeature;

import edu.kis.powp.jobs2d.drivers.transformation.DriverFeatureFactory;
import edu.kis.powp.jobs2d.canvas.CanvasFactory;


public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     * 
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());
        SelectTestCompoundCommandOptionListener selectTestCompoundCommandOptionListener = new SelectTestCompoundCommandOptionListener(
                DriverFeature.getDriverManager());
        SelectCountCommandOptionListener selectCountCommandOptionListener = new SelectCountCommandOptionListener(CommandsFeature.getDriverCommandManager());
        SelectCountDriverOptionListener selectCountDriverOptionListener = new SelectCountDriverOptionListener();

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
        application.addTest("Figure House - CompoundCommand", selectTestCompoundCommandOptionListener);
        application.addTest("Count commands - Visitor", selectCountCommandOptionListener);
        application.addTest("Count drivers - Visitor", selectCountDriverOptionListener);
    }

    /**
     * Setup test using driver commands in context.
     * 
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        ViewFeature.addMouseListenerToControlPanel(new CanvasMouseListener());
        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());
        application.addTest("Flip command", new SelectRunCurrentFlippedCommandOptionListener());
        application.addTest("Rotate 90 command", new SelectRunCurrentRotatedCommandOptionListener());
        application.addTest("Scale 2.0 command", new SelectRunCurrentScaledUpCommandOptionListener());
        application.addTest("Scale 0.5 command", new SelectRunCurrentScaledDownCommandOptionListener());
        application.addTest("Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
    }

    /**
     * Setup driver manager, and set default VisitableJob2dDriver for application.
     * 
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {
        DriverFeature.setConfigurationStrategy(MonitoringFeature::driverConfiguration);

        VisitableJob2dDriver loggerDriver = new LoggerDriver(logger);
        DriverFeature.addDriver("Logger driver", loggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableJob2dDriver basicLineDriver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Basic line Simulator", basicLineDriver);

        AnimatedDriverDecorator slowAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        slowAnimatedDriverDecorator.setSpeedSlow();
        DriverFeature.addDriver("Animated Line - slow", slowAnimatedDriverDecorator);

        AnimatedDriverDecorator mediumAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        mediumAnimatedDriverDecorator.setSpeedMedium();
        DriverFeature.addDriver("Animated Line - medium speed", mediumAnimatedDriverDecorator);

        AnimatedDriverDecorator fastAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        fastAnimatedDriverDecorator.setSpeedFast();
        DriverFeature.addDriver("Animated Line - fast", fastAnimatedDriverDecorator);

        VisitableJob2dDriver specialLineDriver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", specialLineDriver);

        VisitableJob2dDriver basicLineWithLoggerDriver = new DriverComposite(Arrays.asList(basicLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Basic line", basicLineWithLoggerDriver);

        VisitableJob2dDriver specialLineWithLoggerDriver = new DriverComposite(Arrays.asList(specialLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Special line", specialLineWithLoggerDriver);

        RecordingDriverDecorator recordingDriver = new RecordingDriverDecorator(basicLineDriver);
        SelectLoadRecordedCommandOptionListener selectLoadRecordedCommandOptionListener = new SelectLoadRecordedCommandOptionListener(recordingDriver);
        application.addTest("Stop recording & Load recorded command", selectLoadRecordedCommandOptionListener);
        DriverFeature.addDriver("Recording Driver", recordingDriver);

        // Set default driver
        DriverFeature.getDriverManager().setCurrentDriver(basicLineDriver);
        VisitableJob2dDriver rotatedDriver = DriverFeatureFactory.createRotateDriver(basicLineDriver, 45);
        DriverFeature.addDriver("Basic Line + Rotate 45", rotatedDriver);

        VisitableJob2dDriver scaledDriver = DriverFeatureFactory.createScaleDriver(basicLineDriver, 2.0);
        DriverFeature.addDriver("Basic Line + Scale 2x", scaledDriver);

        VisitableJob2dDriver flippedDriver = DriverFeatureFactory.createFlipDriver(basicLineDriver, true, false);
        DriverFeature.addDriver("Basic Line + Flip Horizontal", flippedDriver);

        DriverFeature.updateDriverInfo();
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        SelectImportCommandOptionListener importListener = new SelectImportCommandOptionListener(
                CommandsFeature.getDriverCommandManager(),
                new JsonCommandImportParser()
        );
        commandManager.setImportActionListener(importListener);
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);

        CommandPreviewWindow commandPreviewWindow = new CommandPreviewWindow();
        commandManager.setPreviewWindow(commandPreviewWindow);
        application.addWindowComponent("Command Preview", commandPreviewWindow);
        CommandPreviewWindowObserver previewObserver = new CommandPreviewWindowObserver(
                commandPreviewWindow, 
                CommandsFeature.getDriverCommandManager()
        );
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);
    }

    /**
     * Setup canvas options.
     * 
     * @param application Application context.
     */
    private static void setupCanvases(Application application) {
        CanvasFeature.addCanvas("None", null);
        CanvasFeature.addCanvas(CanvasFactory.createA4());
        CanvasFeature.addCanvas(CanvasFactory.createA3());
        CanvasFeature.addCanvas(CanvasFactory.createB4());
        CanvasFeature.addCanvas(CanvasFactory.createCircle(200));
    }

    /**
     * Setup view options (zoom, pan, reset).
     *
     * @param application Application context.
     */
    private static void setupView(Application application) {
        SelectZoomInOptionListener zoomInListener = new SelectZoomInOptionListener();
        SelectZoomOutOptionListener zoomOutListener = new SelectZoomOutOptionListener();
        SelectResetViewOptionListener resetViewListener = new SelectResetViewOptionListener();

        application.addComponentMenuElement(ViewFeature.class, "Zoom in", zoomInListener);
        application.addComponentMenuElement(ViewFeature.class, "Zoom out", zoomOutListener);
        application.addComponentMenuElement(ViewFeature.class, "Reset", resetViewListener);
    }

    /**
     * Setup menu for adjusting logging settings.
     * 
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");
                ViewFeature.setupViewPlugin(app);
                DrawerFeature.setupDrawerPlugin(app);
                CanvasFeature.setupCanvasPlugin(app);
                CommandsFeature.setupCommandManager();

                DriverFeature.setupDriverPlugin(app);
                setupDrivers(app);
                MonitoringFeature.setupMonitoringPlugin(app, logger);
                setupCanvases(app);
                setupView(app);
                setupPresetTests(app);
                setupCommandTests(app);
                setupLogger(app);
                setupWindows(app);

                app.setVisibility(true);
            }
        });
    }

}
