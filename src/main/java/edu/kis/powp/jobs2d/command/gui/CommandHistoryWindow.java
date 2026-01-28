package edu.kis.powp.jobs2d.command.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.manager.CommandHistory;
import edu.kis.powp.jobs2d.command.manager.CommandHistoryEntry;
import edu.kis.powp.jobs2d.command.manager.CommandManager;

/**
 * GUI window for displaying command history with timestamps.
 * Allows users to view, reload, clear, and remove duplicate commands from history.
 */
public class CommandHistoryWindow extends JFrame implements WindowComponent {
    
    private static final long serialVersionUID = 1L;
    
    private final CommandHistory history;
    private final CommandManager commandManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    public CommandHistoryWindow(CommandHistory history, CommandManager commandManager) {
        this.history = history;
        this.commandManager = commandManager;
        
        setTitle("Command History");
        setSize(700, 400);
        setLayout(new BorderLayout());
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Table for displaying history
        String[] columnNames = {"Index", "Timestamp", "Command"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshHistoryTable());
        buttonPanel.add(btnRefresh);
        
        JButton btnReload = new JButton("Reload Selected");
        btnReload.addActionListener(e -> reloadSelectedCommand());
        buttonPanel.add(btnReload);
        
        JButton btnClear = new JButton("Clear History");
        btnClear.addActionListener(e -> clearHistory());
        buttonPanel.add(btnClear);
        
        JButton btnRemoveDuplicates = new JButton("Remove Duplicates");
        btnRemoveDuplicates.addActionListener(e -> removeDuplicates());
        buttonPanel.add(btnRemoveDuplicates);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshHistoryTable();
    }
    
    /**
     * Refreshes the history table with current data.
     */
    public void refreshHistoryTable() {
        tableModel.setRowCount(0);
        
        for (int i = 0; i < history.size(); i++) {
            CommandHistoryEntry entry = history.getEntry(i);
            String commandStr = entry.getCommand() != null ? entry.getCommand().toString() : "null";
            if (commandStr.length() > 100) {
                commandStr = commandStr.substring(0, 97) + "...";
            }
            tableModel.addRow(new Object[]{i, entry.getFormattedTimestamp(), commandStr});
        }
    }
    
    private void reloadSelectedCommand() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a command to reload.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int index = (int) tableModel.getValueAt(selectedRow, 0);
        commandManager.setCurrentCommand(history.getCommand(index));
        JOptionPane.showMessageDialog(this, "Command reloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all history?", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            history.clear();
            refreshHistoryTable();
            JOptionPane.showMessageDialog(this, "History cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void removeDuplicates() {
        int sizeBefore = history.size();
        history.removeDuplicates();
        int sizeAfter = history.size();
        refreshHistoryTable();
        
        JOptionPane.showMessageDialog(this, 
            "Removed " + (sizeBefore - sizeAfter) + " duplicate entries.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void HideIfVisibleAndShowIfHidden() {
        refreshHistoryTable();
        setVisible(!isVisible());
    }
}
