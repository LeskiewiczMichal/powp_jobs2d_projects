package edu.kis.powp.jobs2d.command.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Maintains a history of commands that have been set as current.
 * 
 * Design Pattern: Memento Pattern - stores command snapshots without exposing
 * CommandManager's internal structure.
 * 
 * Provides thread-safe storage with ability to retrieve commands by index,
 * view the entire history, or clear it.
 */
public class CommandHistory {
    
    private final List<CommandHistoryEntry> history = new ArrayList<>();
    
    /**
     * Adds a command to the history.
     * Duplicate consecutive null entries are prevented.
     */
    public synchronized void addCommand(DriverCommand command) {
        // Avoid adding consecutive nulls
        if (command == null && !history.isEmpty() && history.get(history.size() - 1).getCommand() == null) {
            return;
        }
        history.add(new CommandHistoryEntry(command));
    }
    
    /**
     * Returns an immutable copy of the command history entries.
     */
    public synchronized List<CommandHistoryEntry> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }
    
    /**
     * Returns the number of commands in history.
     * 
     * @return the size of the history
     */
    public synchronized int size() {
        return history.size();
    }
    
    /**
     * Retrieves a history entry at a specific index.
     */
    public synchronized CommandHistoryEntry getEntry(int index) {
        return history.get(index);
    }
    
    /**
     * Retrieves a command at a specific index in history.
     */
    public synchronized DriverCommand getCommand(int index) {
        return history.get(index).getCommand();
    }
    
    /**
     * Clears all command history.
     */
    public synchronized void clear() {
        history.clear();
    }
    
    /**
     * Removes duplicate consecutive commands from history.
     * Keeps the first occurrence of each consecutive group.
     */
    public synchronized void removeDuplicates() {
        if (history.size() <= 1) {
            return;
        }
        
        List<CommandHistoryEntry> deduplicated = new ArrayList<>();
        deduplicated.add(history.get(0));
        
        for (int i = 1; i < history.size(); i++) {
            DriverCommand current = history.get(i).getCommand();
            DriverCommand previous = history.get(i - 1).getCommand();
            
            if (current != previous) {
                deduplicated.add(history.get(i));
            }
        }
        
        history.clear();
        history.addAll(deduplicated);
    }
    
    /**
     * Returns whether the history is empty.
     */
    public synchronized boolean isEmpty() {
        return history.isEmpty();
    }
}
