package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.DriverComposite;

import java.util.Iterator;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

public class DriverCounterVisitor implements DriverVisitor {

    private int animatedDriverDecoratorCount = 0;
    private int loggerDriverCount = 0;
    private int lineDriverAdapterCount = 0;
    private int driverCompositeCount = 0;
    
    private DriverCounterVisitor() {}

    public static class DriverStats {
        private final int animatedDriverDecoratorCount;
        private final int loggerDriverCount;
        private final int lineDriverAdapterCount;

        public DriverStats(int animatedDriverDecoratorCount, int loggerDriverCount, 
                          int lineDriverAdapterCount) {
            this.animatedDriverDecoratorCount = animatedDriverDecoratorCount;
            this.loggerDriverCount = loggerDriverCount;
            this.lineDriverAdapterCount = lineDriverAdapterCount;
        }

        public int getAnimatedDriverDecoratorCount() {
            return animatedDriverDecoratorCount;
        }

        public int getLoggerDriverCount() {
            return loggerDriverCount;
        }
        
        public int getLineDriverAdapterCount() {
            return lineDriverAdapterCount;
        }
    
        public int getCount() {
            return animatedDriverDecoratorCount + loggerDriverCount + lineDriverAdapterCount;
        }
    }

    public static DriverStats countDrivers(Job2dDriver driver) {
        DriverCounterVisitor visitor = new DriverCounterVisitor();
        DriverVisitorDispatcher.dispatch(visitor, driver);
        return new DriverStats(visitor.animatedDriverDecoratorCount, visitor.loggerDriverCount, 
                              visitor.lineDriverAdapterCount);
    }

    @Override
    public void visit(AnimatedDriverDecorator animatedDriverDecorator) {
        animatedDriverDecoratorCount++;
    }

    @Override
    public void visit(LoggerDriver loggerDriver) {
        loggerDriverCount++;
    }

    @Override
    public void visit(LineDriverAdapter lineDriverAdapter) {
        lineDriverAdapterCount++;
    }

    @Override
    public void visit(DriverComposite driverComposite) {
        Iterator<Job2dDriver> iterator = driverComposite.iterator();

        while(iterator.hasNext()) {
            Job2dDriver driver = iterator.next();
            DriverVisitorDispatcher.dispatch(this, driver);
        }
    }
}