package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.*;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Factory that eliminates instanceof checks by using a dispatch table.
 * Each driver type has a registered visitor strategy.
 */
public class DriverVisitorDispatcher {
    
    private static final Map<Class<?>, BiConsumer<DriverVisitor, Job2dDriver>> DISPATCH_TABLE = new HashMap<>();
    
    static {
        // Register dispatch strategies for each driver type
        DISPATCH_TABLE.put(LoggerDriver.class, 
            (visitor, driver) -> visitor.visit((LoggerDriver) driver));
        
        DISPATCH_TABLE.put(AnimatedDriverDecorator.class, 
            (visitor, driver) -> visitor.visit((AnimatedDriverDecorator) driver));
        
        DISPATCH_TABLE.put(LineDriverAdapter.class, 
            (visitor, driver) -> visitor.visit((LineDriverAdapter) driver));
        
        DISPATCH_TABLE.put(DriverComposite.class, 
            (visitor, driver) -> visitor.visit((DriverComposite) driver));
    }
    
    /**
     * Dispatches visitor to the appropriate visit method based on driver's exact type.
     */
    public static void dispatch(DriverVisitor visitor, Job2dDriver driver) {
        BiConsumer<DriverVisitor, Job2dDriver> dispatcher = DISPATCH_TABLE.get(driver.getClass());
        if (dispatcher != null) {
            dispatcher.accept(visitor, driver);
        } else {
            throw new IllegalArgumentException("Unknown driver type: " + driver.getClass().getName());
        }
    }
}