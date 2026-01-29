package edu.kis.powp.jobs2d.command.manager;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.observer.Subscriber;

/**
 * Observer that maintains a history of all commands that have been set as current.
 * 
 * Design Pattern: Observer Pattern - reacts to command changes in CommandManager
 * by recording each command to a CommandHistory instance.
 * 
 * Automatically tracks every command change and delegates storage to CommandHistory.
 * Thread-safe through delegation.
 */
public class CommandHistorySubscriber implements Subscriber {
    
    private final CommandHistory history;
    
    /**
     * Creates a new CommandHistorySubscriber.
     * 
     * @throws NullPointerException if history is null
     */
    public CommandHistorySubscriber(CommandHistory history) {
        if (history == null) {
            throw new NullPointerException("CommandHistory cannot be null");
        }
        this.history = history;
    }
    
    /**
     * Called when a command change notification is received.
     * Retrieves the current command and adds it to the history.
     */
    @Override
    public void update() {
        DriverCommand command = CommandsFeature.getDriverCommandManager().getCurrentCommand();
        history.addCommand(command);
    }
    
    /**
     * Returns the CommandHistory instance managed by this subscriber.
     */
    public CommandHistory getHistory() {
        return history;
    }
    
    @Override
    public String toString() {
        return "Command History Subscriber";
    }
}
