package edu.kis.powp.jobs2d;

import java.util.ArrayList;
import java.util.List;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.command.visitor.CommandVisitor;


public class CommandCounterVisitorTest {
    private static class CommandCounterVisitor implements CommandVisitor {
        private int setPositionCount = 0;
        private int operateToCount = 0;
        private int compoundCount = 0;

        @Override
        public void visit(SetPositionCommand command) {
            setPositionCount++;
        }

        @Override
        public void visit(OperateToCommand command) {
            operateToCount++;
        }

        @Override
        public void visit(ICompoundCommand command) {
            compoundCount++;

            for (DriverCommand nestedCommand : command) {
                nestedCommand.accept(this);
            }
        }

        public int getSetPositionCount() {
            return setPositionCount;
        }

        public int getOperateToCount() {
            return operateToCount;
        }

        public int getCompoundCount() {
            return compoundCount;
        }

        public int getTotalCount() {
            return setPositionCount + operateToCount + compoundCount;
        }

        public void printReport() {
            System.out.println("ICompoundCommand: " + compoundCount);
            System.out.println("SetPositionCommand: " + setPositionCount);
            System.out.println("OperateToCommand: " + operateToCount);
            System.out.println("Total commands: " + getTotalCount());
        }
    }

    public static void main(String[] args) {
        System.out.println("Command Counter Visitor");
        CommandManager manager = new CommandManager();

        System.out.println("Test 1: Single level commands");
        List<DriverCommand> simpleCommands = new ArrayList<>();
        simpleCommands.add(new SetPositionCommand(0, 0));
        simpleCommands.add(new OperateToCommand(100, 100));
        simpleCommands.add(new OperateToCommand(200, 100));
        simpleCommands.add(new SetPositionCommand(200, 200));
        
        manager.setCurrentCommand(simpleCommands, "Simple Compound");
        
        CommandCounterVisitor visitor1 = new CommandCounterVisitor();
        manager.getCurrentCommand().accept(visitor1);
        visitor1.printReport();
        System.out.println("Expected: 1 compound, 2 SetPosition, 2 OperateTo, 5 total\n");

        System.out.println("Test 2: Nested compound commands");
        List<DriverCommand> nestedCommands = new ArrayList<>();
        nestedCommands.add(new SetPositionCommand(50, 50));
        nestedCommands.add(manager.getCurrentCommand()); 
        nestedCommands.add(new OperateToCommand(300, 300));
        
        manager.setCurrentCommand(nestedCommands, "Nested Compound");
        
        CommandCounterVisitor visitor2 = new CommandCounterVisitor();
        manager.getCurrentCommand().accept(visitor2);
        visitor2.printReport();
        System.out.println("Expected: 2 compounds, 3 SetPosition, 3 OperateTo, 8 total\n");

        // Test 3: Complex compound structure
        System.out.println("Test 3: Multi-level nested compound commands");
        
        List<DriverCommand> subCommands1 = new ArrayList<>();
        subCommands1.add(new SetPositionCommand(10, 10));
        subCommands1.add(new OperateToCommand(20, 20));
        
        manager.setCurrentCommand(subCommands1, "SubCompound1");
        DriverCommand subCompound1 = manager.getCurrentCommand();

        List<DriverCommand> subCommands2 = new ArrayList<>();
        subCommands2.add(new OperateToCommand(30, 30));
        subCommands2.add(new OperateToCommand(40, 40));
        
        manager.setCurrentCommand(subCommands2, "SubCompound2");
        DriverCommand subCompound2 = manager.getCurrentCommand();

        List<DriverCommand> complexCommands = new ArrayList<>();
        complexCommands.add(subCompound1);
        complexCommands.add(new SetPositionCommand(25, 25));
        complexCommands.add(subCompound2);
        complexCommands.add(new OperateToCommand(50, 50));
        
        manager.setCurrentCommand(complexCommands, "Complex Compound");
        
        CommandCounterVisitor visitor3 = new CommandCounterVisitor();
        manager.getCurrentCommand().accept(visitor3);
        visitor3.printReport();
        System.out.println("Expected: 3 compounds, 2 SetPosition, 4 OperateTo, 9 total\n");

        System.out.println("Command Counter Visitor Test completed successfully!");
    }}