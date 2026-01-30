package edu.kis.powp.jobs2d.visitor;

import java.util.Iterator;

import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.CanvasLimitedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.transformation.TransformerDriverDecorator;

public class DriverCounterVisitor implements DriverVisitor {

    private int animatedDriverDecoratorCount = 0;
    private int loggerDriverCount = 0;
    private int lineDriverAdapterCount = 0;
    private int transformerDriverDecoratorCount = 0;
    private int canvasLimitedDriverDecoratorCount = 0;
    private int usageTrackingDecoratorCount = 0;
    private int recordingDriverDecoratorCount = 0;

    private DriverCounterVisitor() {
    }

    public static class DriverStats {
        private final int animatedDriverDecoratorCount;
        private final int loggerDriverCount;
        private final int lineDriverAdapterCount;
        private final int transformerDriverDecoratorCount;
        private final int canvasLimitedDriverDecoratorCount;
        private final int usageTrackingDecoratorCount;
        private final int recordingDriverDecoratorCount;

        public DriverStats(int animatedDriverDecoratorCount, int loggerDriverCount, int lineDriverAdapterCount,
                int transformerDriverDecoratorCount, int usageTrackingDecoratorCount, int recordingDriverDecoratorCount,
                int canvasLimitedDriverDecoratorCount) {
            this.animatedDriverDecoratorCount = animatedDriverDecoratorCount;
            this.loggerDriverCount = loggerDriverCount;
            this.lineDriverAdapterCount = lineDriverAdapterCount;
            this.transformerDriverDecoratorCount = transformerDriverDecoratorCount;
            this.canvasLimitedDriverDecoratorCount = canvasLimitedDriverDecoratorCount;
            this.usageTrackingDecoratorCount = usageTrackingDecoratorCount;
            this.recordingDriverDecoratorCount = recordingDriverDecoratorCount;
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

        public int getTransformerDriverDecoratorCount() {
            return transformerDriverDecoratorCount;
        }

        public int getUsageTrackingDriverDecoratorCount() {
            return usageTrackingDecoratorCount;
        }

        public int getRecordingDriverDecoratorCount() {
            return recordingDriverDecoratorCount;
        }

        public int getCanvasLimitedDriverDecoratorCount() {
            return canvasLimitedDriverDecoratorCount;
        }

        public int getCount() {
            return animatedDriverDecoratorCount + loggerDriverCount + lineDriverAdapterCount
                    + transformerDriverDecoratorCount + usageTrackingDecoratorCount + recordingDriverDecoratorCount
                    + canvasLimitedDriverDecoratorCount;
        }
    }

    public static DriverStats countDrivers(VisitableJob2dDriver driver) {
        DriverCounterVisitor visitor = new DriverCounterVisitor();
        driver.accept(visitor);
        return new DriverStats(visitor.animatedDriverDecoratorCount, visitor.loggerDriverCount,
                visitor.lineDriverAdapterCount, visitor.transformerDriverDecoratorCount,
                visitor.usageTrackingDecoratorCount, visitor.recordingDriverDecoratorCount,
                visitor.canvasLimitedDriverDecoratorCount);
    }

    @Override
    public void visit(AnimatedDriverDecorator animatedDriverDecorator) {
        animatedDriverDecoratorCount++;
        animatedDriverDecorator.getTargetDriver().accept(this);
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
    public void visit(TransformerDriverDecorator transformerDriverDecorator) {
        transformerDriverDecoratorCount++;
        transformerDriverDecorator.getDriver().accept(this);
    }

    @Override
    public void visit(DriverComposite driverComposite) {
        Iterator<VisitableJob2dDriver> iterator = driverComposite.iterator();

        while (iterator.hasNext()) {
            VisitableJob2dDriver driver = iterator.next();
            driver.accept(this);
        }
    }

    @Override
    public void visit(CanvasLimitedDriverDecorator canvasLimitedDriverDecorator) {
        canvasLimitedDriverDecoratorCount++;
        canvasLimitedDriverDecorator.getTargetDriver().accept(this);
    }

    @Override
    public void visit(UsageTrackingDriverDecorator usageTrackingDriverDecorator) {
        this.usageTrackingDecoratorCount++;
        usageTrackingDriverDecorator.getDelegate().accept(this);
    }

    @Override
    public void visit(RecordingDriverDecorator recordingDriverDecorator) {
        this.recordingDriverDecoratorCount++;
        recordingDriverDecorator.getDelegate().accept(this);
    }

}