package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.visitor.CommandVisitor;

import java.util.Iterator;

/**
 * Interface extending Job2dDriverCommand to execute more than one command.
 */
public interface ICompoundCommand extends DriverCommand {

    public Iterator<DriverCommand> iterator();

    @Override
    default void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
