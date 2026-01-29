package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.*;
import edu.kis.powp.jobs2d.drivers.transformation.TransformCords;
import edu.kis.powp.jobs2d.drivers.transformation.TransformStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplexCommandTransformationVisitor implements CommandVisitor {

    private final TransformStrategy strategy;
    private DriverCommand result;

    private ComplexCommandTransformationVisitor(TransformStrategy strategy) {
        this.strategy = strategy;
    }

    // static method to avoid state-related issues as overlapping of results
    public static DriverCommand transform(DriverCommand command, TransformStrategy strategy) {
        ComplexCommandTransformationVisitor visitor = new ComplexCommandTransformationVisitor(strategy);
        command.accept(visitor);
        return visitor.result;
    }

    @Override
    public void visit(SetPositionCommand command) {
        TransformCords cords = strategy.transform(new TransformCords(command.getPosX(), command.getPosY()));
        result = new SetPositionCommand(cords.x, cords.y);
    }

    @Override
    public void visit(OperateToCommand command) {
        TransformCords cords = strategy.transform(new TransformCords(command.getPosX(), command.getPosY()));
        result = new OperateToCommand(cords.x, cords.y);
    }

    @Override
    public void visit(ICompoundCommand compoundCommand) {
        List<DriverCommand> transformedCommands = new ArrayList<>();
        Iterator<DriverCommand> iterator = compoundCommand.iterator();

        while (iterator.hasNext()) {
            iterator.next().accept(this);
            transformedCommands.add(result);
        }

        result = CompoundCommand.fromListOfCommands(transformedCommands, compoundCommand + " (transformed)"
        );
    }
}