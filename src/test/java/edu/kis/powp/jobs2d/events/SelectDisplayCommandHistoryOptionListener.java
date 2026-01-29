package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import edu.kis.powp.jobs2d.command.manager.CommandHistory;
import edu.kis.powp.jobs2d.features.CommandsFeature;

public class SelectDisplayCommandHistoryOptionListener implements ActionListener {
    
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    @Override
    public void actionPerformed(ActionEvent e) {
        CommandHistory history = CommandsFeature.getCommandHistory();
        logger.info("=== Command History ===");
        logger.info("Total commands: " + history.size());
        for (int i = 0; i < history.size(); i++) {
            logger.info("[" + i + "] " + history.getCommand(i));
        }
        logger.info("======================");
    }
}