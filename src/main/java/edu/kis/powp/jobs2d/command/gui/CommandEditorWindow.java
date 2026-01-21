package edu.kis.powp.jobs2d.command.gui;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.visitor.CommandVisitor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandEditorWindow extends JFrame {
    private final CommandManager commandManager;
    private final CommandPreviewWindow previewWindow;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DriverCommand originalCommand;
    private JTextField xField;
    private JTextField yField;
    private JLabel typeLabel;
    private JPanel editPanel;
    private boolean isUpdatingGUI = false;

    public CommandEditorWindow(CommandManager commandManager, CommandPreviewWindow previewWindow) {
        this.commandManager = commandManager;
        this.previewWindow = previewWindow;
        this.originalCommand = commandManager.getCurrentCommand(); // Save original command
        this.setTitle("Command Editor");
        this.setSize(600, 500);
        this.setLayout(new BorderLayout());


        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Root"));
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        loadCommands();

        tree.addTreeSelectionListener(e -> updateEditPanel());
        tree.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    deleteSelectedNode();
                }
            }
        });

        editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Command"));

        typeLabel = new JLabel("Select a command");
        xField = new JTextField(10);
        yField = new JTextField(10);

        setupEditPanelLayout();
        setupFieldListeners();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), editPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton previewButton = new JButton("Preview");
        JButton upButton = new JButton("Move Up");
        JButton downButton = new JButton("Move Down");

        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> cancelChanges());
        previewButton.addActionListener(e -> {
            if (this.previewWindow != null && !this.previewWindow.isVisible()) {
                this.previewWindow.setVisible(true);
            }
        });
        upButton.addActionListener(e -> moveUp());
        downButton.addActionListener(e -> moveDown());

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(previewButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cancelChanges();
            }
        });
    }

    private void setupEditPanelLayout() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        editPanel.add(typeLabel, c);

        c.gridy = 1; c.gridwidth = 1;
        editPanel.add(new JLabel("X:"), c);
        c.gridx = 1;
        editPanel.add(xField, c);

        c.gridx = 0; c.gridy = 2;
        editPanel.add(new JLabel("Y:"), c);
        c.gridx = 1;
        editPanel.add(yField, c);

        c.gridy = 3; c.weighty = 1.0;
        editPanel.add(new JLabel(), c);
    }

    private void setupFieldListeners() {
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateNode(); }
            public void removeUpdate(DocumentEvent e) { updateNode(); }
            public void changedUpdate(DocumentEvent e) { updateNode(); }
        };
        xField.getDocument().addDocumentListener(listener);
        yField.getDocument().addDocumentListener(listener);
    }

    private void updateEditPanel() {
        isUpdatingGUI = true;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node != null && node.getUserObject() instanceof EditableCommandNode) {
            EditableCommandNode commandNode = (EditableCommandNode) node.getUserObject();
            if (commandNode instanceof EditableAtomicNode) {
                EditableAtomicNode atomicNode = (EditableAtomicNode) commandNode;
                typeLabel.setText("Type: " + atomicNode.type);
                xField.setText(String.valueOf(atomicNode.x));
                yField.setText(String.valueOf(atomicNode.y));
                xField.setEnabled(true);
                yField.setEnabled(true);
            } else {
                typeLabel.setText("Composite: " + ((EditableCompositeNode)commandNode).name);
                xField.setText("");
                yField.setText("");
                xField.setEnabled(false);
                yField.setEnabled(false);
            }
        } else {
            typeLabel.setText("Select a command");
            xField.setText("");
            yField.setText("");
            xField.setEnabled(false);
            yField.setEnabled(false);
        }
        isUpdatingGUI = false;
    }

    private void updateNode() {
        if (isUpdatingGUI) return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() instanceof EditableAtomicNode) {
            EditableAtomicNode atomicNode = (EditableAtomicNode) node.getUserObject();
            try {
                atomicNode.x = Integer.parseInt(xField.getText());
                atomicNode.y = Integer.parseInt(yField.getText());
                treeModel.nodeChanged(node);
                previewChanges();
            } catch (NumberFormatException e) {
                // Ignore invalid input while typing
            }
        }
    }

    private void moveUp() {
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected == null) return;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected.getParent();
        if (parent == null) return;

        int index = parent.getIndex(selected);
        if (index > 0) {
            treeModel.removeNodeFromParent(selected);
            treeModel.insertNodeInto(selected, parent, index - 1);
            tree.setSelectionPath(new TreePath(selected.getPath()));
            previewChanges();
        }
    }

    private void moveDown() {
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected == null) return;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected.getParent();
        if (parent == null) return;

        int index = parent.getIndex(selected);
        if (index < parent.getChildCount() - 1) {
            treeModel.removeNodeFromParent(selected);
            treeModel.insertNodeInto(selected, parent, index + 1);
            tree.setSelectionPath(new TreePath(selected.getPath()));
            previewChanges();
        }
    }

    private void deleteSelectedNode() {
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected == null) return;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected.getParent();
        if (parent == null) return;

        treeModel.removeNodeFromParent(selected);
        previewChanges();
    }

    private void loadCommands() {
        DriverCommand currentCommand = commandManager.getCurrentCommand();
        if (currentCommand != null) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            root.removeAllChildren();

            TreeBuildingVisitor visitor = new TreeBuildingVisitor(root);
            currentCommand.accept(visitor);

            treeModel.reload();
            // Expand all rows
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        }
    }

    private void previewChanges() {
        DriverCommand newCommand = rebuildCommand();
        commandManager.setCurrentCommand(newCommand);
    }

    private void cancelChanges() {
        if (originalCommand != null) {
            commandManager.setCurrentCommand(originalCommand);
        }
        dispose();
    }

    private void saveChanges() {
        DriverCommand newCommand = rebuildCommand();
        commandManager.setCurrentCommand(newCommand);
        dispose();
    }

    private DriverCommand rebuildCommand() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        return buildCommandFromNode(root);
    }

    private DriverCommand buildCommandFromNode(DefaultMutableTreeNode node) {
        if (node.isRoot()) {
            List<DriverCommand> commands = new ArrayList<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                 DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                 commands.add(buildCommandFromNode(child));
            }
            if (commands.size() == 1) return commands.get(0);
            return CompoundCommand.fromListOfCommands(commands, "Edited Command");
        }

        EditableCommandNode userObj = (EditableCommandNode) node.getUserObject();
        if (userObj instanceof EditableAtomicNode) {
            return ((EditableAtomicNode) userObj).createCommand();
        } else if (userObj instanceof EditableCompositeNode) {
             List<DriverCommand> children = new ArrayList<>();
             for (int i = 0; i < node.getChildCount(); i++) {
                 DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                 children.add(buildCommandFromNode(child));
            }
            return CompoundCommand.fromListOfCommands(children, ((EditableCompositeNode) userObj).name);
        }
        return null; // Should not happen
    }

    private static abstract class EditableCommandNode {
        abstract DriverCommand createCommand();
    }

    private static class EditableAtomicNode extends EditableCommandNode {
        String type;
        int x, y;

        EditableAtomicNode(SetPositionCommand cmd) {
            type = "SetPosition";
            x = cmd.getPosX();
            y = cmd.getPosY();
        }

        EditableAtomicNode(OperateToCommand cmd) {
            type = "OperateTo";
            x = cmd.getPosX();
            y = cmd.getPosY();
        }

        @Override
        DriverCommand createCommand() {
            if ("SetPosition".equals(type)) return new SetPositionCommand(x, y);
            return new OperateToCommand(x, y);
        }

        @Override
        public String toString() {
            return type + " [" + x + ", " + y + "]";
        }
    }

    private static class EditableCompositeNode extends EditableCommandNode {
        String name;

        EditableCompositeNode(String name) {
            this.name = name;
        }

        @Override
        DriverCommand createCommand() {
            throw new UnsupportedOperationException("Should be handled by recursion helper");
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class TreeBuildingVisitor implements CommandVisitor {
        private final DefaultMutableTreeNode parent;

        public TreeBuildingVisitor(DefaultMutableTreeNode parent) {
            this.parent = parent;
        }

        @Override
        public void visit(SetPositionCommand command) {
            parent.add(new DefaultMutableTreeNode(new EditableAtomicNode(command)));
        }

        @Override
        public void visit(OperateToCommand command) {
             parent.add(new DefaultMutableTreeNode(new EditableAtomicNode(command)));
        }

        @Override
        public void visit(ICompoundCommand command) {
            String name = "Compound Command";
            if (command instanceof CompoundCommand) {
                name = ((CompoundCommand) command).getName();
            }
            DefaultMutableTreeNode compositeNode = new DefaultMutableTreeNode(new EditableCompositeNode(name));
            parent.add(compositeNode);

            Iterator<DriverCommand> iterator = command.iterator();
            TreeBuildingVisitor visitor = new TreeBuildingVisitor(compositeNode);
            while (iterator.hasNext()) {
                iterator.next().accept(visitor);
            }
        }
    }
}
