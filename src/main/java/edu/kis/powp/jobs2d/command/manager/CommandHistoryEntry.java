package edu.kis.powp.jobs2d.command.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Represents a single entry in the command history with timestamp information.
 * Stores the command and the time when it was set as current.
 */
public class CommandHistoryEntry {
    
    private final DriverCommand command;
    private final LocalDateTime timestamp;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Creates a new history entry with the current timestamp.
     */
    public CommandHistoryEntry(DriverCommand command) {
        this.command = command;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Creates a new history entry with a specific timestamp.
     */
    public CommandHistoryEntry(DriverCommand command, LocalDateTime timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }
    
    public DriverCommand getCommand() {
        return command;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns formatted timestamp as string.
     */
    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] " + (command != null ? command.toString() : "null");
    }
}
