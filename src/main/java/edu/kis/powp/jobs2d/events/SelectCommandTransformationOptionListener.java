package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.visitor.ComplexCommandTransformationVisitor;
import edu.kis.powp.jobs2d.drivers.transformation.TransformStrategy;

public class SelectCommandTransformationOptionListener implements ActionListener {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final CommandManager commandManager;
    private final TransformStrategy strategy;

    public SelectCommandTransformationOptionListener(CommandManager commandManager, TransformStrategy strategy) {
        this.commandManager = commandManager;
        this.strategy = strategy;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = this.commandManager.getCurrentCommand();

        if (command == null) {
            logger.warning("No command loaded.");
            return;
        }

        DriverCommand transformedCommand = ComplexCommandTransformationVisitor.transform(command, strategy);

        this.commandManager.setCurrentCommand(transformedCommand);

        logger.info("Command transformed using " + strategy.getClass().getSimpleName());
    }
}