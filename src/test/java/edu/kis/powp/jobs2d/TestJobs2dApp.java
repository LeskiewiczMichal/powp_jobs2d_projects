package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.canvas.CanvasFactory;
import edu.kis.powp.jobs2d.command.gui.CommandHistoryWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindow;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindowObserver;
import edu.kis.powp.jobs2d.command.gui.SelectImportCommandOptionListener;
import edu.kis.powp.jobs2d.command.importer.JsonCommandImportParser;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.CanvasLimitedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.strategy.OnCanvasExceededLogWarning;
import edu.kis.powp.jobs2d.drivers.strategy.OnCanvasExceededStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.DriverFeatureFactory;
import edu.kis.powp.jobs2d.drivers.transformation.FlipStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.RotateStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ScaleStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ShearStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ShiftStrategy;
import edu.kis.powp.jobs2d.events.CanvasMouseListener;
import edu.kis.powp.jobs2d.events.SelectCommandTransformationOptionListener;
import edu.kis.powp.jobs2d.events.SelectCountCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectCountDriverOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadRecordedCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadSecretCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectResetViewOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentFlippedCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentRotatedCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentScaledDownCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentScaledUpCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestCompoundCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigure2OptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigureOptionListener;
import edu.kis.powp.jobs2d.events.SelectValidateCanvasBoundsOptionListener;
import edu.kis.powp.jobs2d.events.SelectZoomInOptionListener;
import edu.kis.powp.jobs2d.events.SelectZoomOutOptionListener;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.features.*;

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
        SelectCountCommandOptionListener selectCountCommandOptionListener = new SelectCountCommandOptionListener(
                CommandsFeature.getDriverCommandManager());
        SelectCountDriverOptionListener selectCountDriverOptionListener = new SelectCountDriverOptionListener();
        SelectValidateCanvasBoundsOptionListener selectValidateCanvasBoundsOptionListener = new SelectValidateCanvasBoundsOptionListener(
                CommandsFeature.getDriverCommandManager(), logger);

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
        application.addTest("Figure House - CompoundCommand", selectTestCompoundCommandOptionListener);
        application.addTest("Count commands - Visitor", selectCountCommandOptionListener);
        application.addTest("Count drivers - Visitor", selectCountDriverOptionListener);
        application.addTest("Validate Canvas Bounds", selectValidateCanvasBoundsOptionListener);
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

        CommandManager manager = CommandsFeature.getDriverCommandManager();
        application.addTest("Scale x2", new SelectCommandTransformationOptionListener(manager, new ScaleStrategy(2)));
        application.addTest("Rotate 90 degrees",
                new SelectCommandTransformationOptionListener(manager, new RotateStrategy(90)));
        application.addTest("Flip",
                new SelectCommandTransformationOptionListener(manager, new FlipStrategy(true, false)));
        application.addTest("Shift (right: 15)",
                new SelectCommandTransformationOptionListener(manager, new ShiftStrategy(15, 0)));
        application.addTest("Shift (down: 15)",
                new SelectCommandTransformationOptionListener(manager, new ShiftStrategy(0, 15)));
        application.addTest("Shear (X: 0.5)",
                new SelectCommandTransformationOptionListener(manager, new ShearStrategy(0.5, 0)));
        application.addTest("Shear (Y: 0.5)",
                new SelectCommandTransformationOptionListener(manager, new ShearStrategy(0, 0.5)));
    }

    /**
     * Setup driver manager, and set default VisitableJob2dDriver for application.
     * 
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {
        DriverFeature.setConfigurationStrategy(new MonitoringDriverConfigurationStrategy());

        VisitableJob2dDriver loggerDriver = new LoggerDriver(logger);
        DriverFeature.addDriver("Logger driver", loggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableJob2dDriver basicLineDriver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(),
                "basic");
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

        VisitableJob2dDriver specialLineDriver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(),
                "special");
        DriverFeature.addDriver("Special line Simulator", specialLineDriver);

        VisitableJob2dDriver basicLineWithLoggerDriver = new DriverComposite(
                Arrays.asList(basicLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Basic line", basicLineWithLoggerDriver);

        VisitableJob2dDriver specialLineWithLoggerDriver = new DriverComposite(
                Arrays.asList(specialLineDriver, loggerDriver));
        DriverFeature.addDriver("Logger + Special line", specialLineWithLoggerDriver);

        RecordingDriverDecorator recordingDriver = new RecordingDriverDecorator(basicLineDriver);
        SelectLoadRecordedCommandOptionListener selectLoadRecordedCommandOptionListener = new SelectLoadRecordedCommandOptionListener(
                recordingDriver);
        application.addTest("Stop recording & Load recorded command", selectLoadRecordedCommandOptionListener);
        DriverFeature.addDriver("Recording Driver", recordingDriver);

        // Device maintenance panel
        VisitableJob2dDriver driver = new LineDriverAdapter(DrawerFeature.getDrawerController(), LineFactory.getBasicLine(), "basic");
        MonitoringFeature.addDriverWithMaintenance(application, driver, 500, 40, 40);

        // Set default driver
        DriverFeature.getDriverManager().setCurrentDriver(basicLineDriver);
        VisitableJob2dDriver rotatedDriver = DriverFeatureFactory.createRotateDriver(basicLineDriver, 45);
        DriverFeature.addDriver("Basic Line + Rotate 45", rotatedDriver);

        VisitableJob2dDriver scaledDriver = DriverFeatureFactory.createScaleDriver(basicLineDriver, 2.0);
        DriverFeature.addDriver("Basic Line + Scale 2x", scaledDriver);

        VisitableJob2dDriver flippedDriver = DriverFeatureFactory.createFlipDriver(basicLineDriver, true, false);
        DriverFeature.addDriver("Basic Line + Flip Horizontal", flippedDriver);

        OnCanvasExceededStrategy onCanvasExceededStrategy = new OnCanvasExceededLogWarning();
        CanvasLimitedDriverDecorator canvasLimitedDriver = new CanvasLimitedDriverDecorator(basicLineDriver,
                onCanvasExceededStrategy);
        DriverFeature.addDriver("Canvas-Limited Driver", canvasLimitedDriver);

        DriverFeature.updateDriverInfo();
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        SelectImportCommandOptionListener importListener = new SelectImportCommandOptionListener(
                CommandsFeature.getDriverCommandManager(), new JsonCommandImportParser());
        commandManager.setImportActionListener(importListener);
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);

        CommandPreviewWindow commandPreviewWindow = new CommandPreviewWindow();
        commandManager.setPreviewWindow(commandPreviewWindow);
        application.addWindowComponent("Command Preview", commandPreviewWindow);
        CommandPreviewWindowObserver previewObserver = new CommandPreviewWindowObserver(commandPreviewWindow,
                CommandsFeature.getDriverCommandManager());
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);

        CommandHistoryWindow historyWindow = new CommandHistoryWindow(CommandsFeature.getCommandHistory(),
                CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command History", historyWindow);
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

                FeatureManager featureManager = new FeatureManager();
                featureManager.setApplication(app);
                featureManager.registerFeature(new ViewFeature());
                featureManager.registerFeature(new DrawerFeature());
                featureManager.registerFeature(new CanvasFeature());
                featureManager.registerFeature(new CommandsFeature());
                featureManager.registerFeature(new DriverFeature());
                featureManager.registerFeature(new MonitoringFeature(logger));
                featureManager.setupAll();

                setupDrivers(app);
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
