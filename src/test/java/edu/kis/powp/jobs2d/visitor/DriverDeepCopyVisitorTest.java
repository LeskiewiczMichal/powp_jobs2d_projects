package edu.kis.powp.jobs2d.visitor;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

public class DriverDeepCopyVisitorTest {

    @Test
    public void testLoggerDriverDeepCopy() {
        LoggerDriver driver = new LoggerDriver();
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof LoggerDriver);
        Assert.assertNotSame(driver, copy);
    }

    @Test
    public void testLineDriverAdapterDeepCopy() {
        DrawPanelController controller = new DrawPanelController();
        ILine line = LineFactory.getBasicLine();
        LineDriverAdapter driver = new LineDriverAdapter(controller, line, "test");
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof LineDriverAdapter);
        Assert.assertNotSame(driver, copy);
        LineDriverAdapter copyAdapter = (LineDriverAdapter) copy;
        Assert.assertEquals(driver.getDrawController(), copyAdapter.getDrawController());
        Assert.assertNotSame(driver.getLine(), copyAdapter.getLine());
        Assert.assertEquals(driver.getName(), copyAdapter.getName());
    }

    @Test
    public void testDriverCompositeDeepCopy() {
        DriverComposite composite = new DriverComposite(new ArrayList<>());
        LoggerDriver child1 = new LoggerDriver();
        composite.add(child1);

        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        composite.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof DriverComposite);
        Assert.assertNotSame(composite, copy);

        DriverComposite copyComposite = (DriverComposite) copy;
        Iterator<VisitableJob2dDriver> originalIt = composite.iterator();
        Iterator<VisitableJob2dDriver> copyIt = copyComposite.iterator();

        Assert.assertTrue(originalIt.hasNext());
        Assert.assertTrue(copyIt.hasNext());

        while (originalIt.hasNext() && copyIt.hasNext()) {
             VisitableJob2dDriver originalChild = originalIt.next();
             VisitableJob2dDriver copyChild = copyIt.next();
             Assert.assertNotSame(originalChild, copyChild);
             Assert.assertSame(originalChild.getClass(), copyChild.getClass());
        }

        Assert.assertFalse(originalIt.hasNext());
        Assert.assertFalse(copyIt.hasNext());
    }

    @Test
    public void testAnimatedDriverDecoratorDeepCopy() {
        LoggerDriver target = new LoggerDriver();
        AnimatedDriverDecorator driver = new AnimatedDriverDecorator(target);
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof AnimatedDriverDecorator);
        Assert.assertNotSame(driver, copy);
        AnimatedDriverDecorator copyDecorator = (AnimatedDriverDecorator) copy;
        Assert.assertNotSame(driver.getTargetDriver(), copyDecorator.getTargetDriver());
        Assert.assertTrue(copyDecorator.getTargetDriver() instanceof LoggerDriver);
    }

    @Test
    public void testUsageTrackingDriverDecoratorDeepCopy() {
        LoggerDriver target = new LoggerDriver();
        UsageTrackingDriverDecorator driver = new UsageTrackingDriverDecorator(target, "test-label");
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof UsageTrackingDriverDecorator);
        Assert.assertNotSame(driver, copy);

        UsageTrackingDriverDecorator copyDecorator = (UsageTrackingDriverDecorator) copy;
        Assert.assertEquals("test-label", copyDecorator.getLabel());
        Assert.assertNotSame(driver.getDelegate(), copyDecorator.getDelegate());
        Assert.assertTrue(copyDecorator.getDelegate() instanceof LoggerDriver);
    }

    @Test
    public void testUsageTrackingBehaviorParity() {
        UsageTrackingDriverDecorator original = new UsageTrackingDriverDecorator(new LoggerDriver(), "label");
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        original.accept(visitor);
        UsageTrackingDriverDecorator copy = (UsageTrackingDriverDecorator) visitor.getCopy();

        original.setPosition(0, 0);
        original.operateTo(3, 4);

        copy.setPosition(0, 0);
        copy.operateTo(3, 4);

        Assert.assertEquals(original.getTravelDistance(), copy.getTravelDistance(), 0.0001);
        Assert.assertEquals(original.getDrawingDistance(), copy.getDrawingDistance(), 0.0001);
        Assert.assertNotSame(original.getDelegate(), copy.getDelegate());
        Assert.assertEquals(original.getDelegate().getClass(), copy.getDelegate().getClass());
    }
}
