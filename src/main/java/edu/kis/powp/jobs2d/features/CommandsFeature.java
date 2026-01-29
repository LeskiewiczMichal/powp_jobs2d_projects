package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.command.manager.CommandHistory;
import edu.kis.powp.jobs2d.command.manager.CommandHistorySubscriber;
import edu.kis.powp.jobs2d.command.manager.LoggerCommandChangeObserver;

public class CommandsFeature implements IFeature {

    private static CommandManager commandManager;
    private static CommandHistory commandHistory;
    private static CommandHistorySubscriber commandHistorySubscriber;

    public CommandsFeature() {
    }

    @Override
    public void setup(Application app) {
        setupCommandManager();
    }

    private static void setupCommandManager() {
        commandManager = new CommandManager();

        LoggerCommandChangeObserver loggerObserver = new LoggerCommandChangeObserver();
        commandManager.getChangePublisher().addSubscriber(loggerObserver);
        
        // Setup command history tracking
        commandHistory = new CommandHistory();
        commandHistorySubscriber = new CommandHistorySubscriber(commandHistory);
        commandManager.getChangePublisher().addSubscriber(commandHistorySubscriber);
    }

    /**
     * Get manager of application driver command.
     *
     * @return plotterCommandManager.
     */
    public static CommandManager getDriverCommandManager() {
        return commandManager;
    }

    /**
     * Get the command history instance.
     *
     * @return the CommandHistory tracking all set commands.
     */
    public static CommandHistory getCommandHistory() {
        return commandHistory;
    }

    @Override
    public String getName() {
        return "Commands";
    }
}
