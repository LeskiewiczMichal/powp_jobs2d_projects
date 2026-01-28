package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.canvas.CanvasFactory;
import edu.kis.powp.jobs2d.canvas.CanvasMargin;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.visitor.BoundsViolation;
import edu.kis.powp.jobs2d.visitor.CanvasBoundsCheckVisitor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for CanvasBoundsCheckVisitor functionality.
 */
public class CanvasBoundsCheckVisitorTest {
    
    @Test
    public void testCommandWithinA4Canvas() {
        ICanvas a4Canvas = CanvasFactory.createA4();
        DriverCommand command = CompoundCommand.builder()
            .addSetPosition(0, 0)
            .addOperateTo(100, 100)
            .addOperateTo(-100, -100)
            .build();
        
        CanvasBoundsCheckVisitor.BoundsCheckResult result = 
            CanvasBoundsCheckVisitor.checkCanvasBounds(command, a4Canvas);
        
        assertFalse("Command should fit within A4 canvas", result.hasAnyViolations());
        assertEquals(0, result.getViolationCount());
    }
    
    @Test
    public void testCommandExceedsA4Canvas() {
        ICanvas a4Canvas = CanvasFactory.createA4();
        DriverCommand command = CompoundCommand.builder()
            .addSetPosition(0, 0)
            .addOperateTo(500, 500)
            .build();
        
        CanvasBoundsCheckVisitor.BoundsCheckResult result = 
            CanvasBoundsCheckVisitor.checkCanvasBounds(command, a4Canvas);
        
        assertTrue("Command should exceed A4 canvas", result.hasCanvasViolations());
        assertEquals(1, result.getViolationCount());
        
        BoundsViolation violation = result.getViolations().get(0);
        assertEquals(BoundsViolation.ViolationType.CANVAS_EXCEEDED, violation.getType());
        assertEquals(500, violation.getX());
        assertEquals(500, violation.getY());
    }
    
    @Test
    public void testCommandWithinCanvasButExceedsMargins() {
        ICanvas a4Canvas = CanvasFactory.createA4();
        CanvasMargin margin = new CanvasMargin(20);
        DriverCommand command = CompoundCommand.builder()
            .addSetPosition(0, 0)
            .addOperateTo(100, 140)
            .build();
        
        CanvasBoundsCheckVisitor.BoundsCheckResult result = 
            CanvasBoundsCheckVisitor.checkCanvasAndMargins(command, a4Canvas, margin);
        
        assertFalse("Should not exceed canvas", result.hasCanvasViolations());
        assertTrue("Should exceed margins", result.hasMarginViolations());
        assertEquals(1, result.getViolationCount());
        
        BoundsViolation violation = result.getViolations().get(0);
        assertEquals(BoundsViolation.ViolationType.MARGIN_EXCEEDED, violation.getType());
    }
    
    @Test
    public void testCircleCanvasBounds() {
        ICanvas circleCanvas = CanvasFactory.createCircle(100);
        DriverCommand command = CompoundCommand.builder()
            .addSetPosition(0, 0)
            .addOperateTo(150, 0)
            .build();
        
        CanvasBoundsCheckVisitor.BoundsCheckResult result = 
        CanvasBoundsCheckVisitor.checkCanvasBounds(command, circleCanvas);
        
        assertTrue("Command should exceed circle canvas", result.hasCanvasViolations());
        assertEquals(1, result.getViolationCount());
    }
}