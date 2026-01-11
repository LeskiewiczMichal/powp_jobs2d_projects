package edu.kis.powp.jobs2d.visitor;


import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

/**
 * Interface for the Visitor pattern to traverse and process drivers.
 * Uses method overloading for clean dispatch.
 */
public interface DriverVisitor {

    /**
     * Visits an AnimatedDriverDecorator.
     * @param animatedDriverDecorator driver to visit
     */
    void visit(AnimatedDriverDecorator animatedDriverDecorator);

    /**
     * Visits a DriverComposite.
     * @param driverComposite driver to visit
     */
    void visit(DriverComposite driverComposite);

    /**
     * Visits a LoggerDriver.
     * @param loggerDriver the driver to visit
     */
    void visit(LoggerDriver loggerDriver);

    /**
     * Visits a LineDriverAdapter.
     * @param lineDriverAdapter the adapter to visit
     */
    void visit(LineDriverAdapter lineDriverAdapter);
}