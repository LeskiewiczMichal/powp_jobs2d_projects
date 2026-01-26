package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.canvas.CanvasMargin;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Visitor that checks if commands exceed canvas bounds or margins.
 */
public class CanvasBoundsCheckVisitor implements CommandVisitor {

    private final ICanvas canvas;
    private final CanvasMargin margin;
    private final List<BoundsViolation> violations;
    private final boolean checkMargins;

    private CanvasBoundsCheckVisitor(ICanvas canvas, CanvasMargin margin, boolean checkMargins) {
        this.canvas = canvas;
        this.margin = margin;
        this.checkMargins = checkMargins;
        this.violations = new ArrayList<>();
    }

    /**
     * Result of bounds checking.
     */
    public static class BoundsCheckResult {
        private final List<BoundsViolation> violations;
        private final boolean hasCanvasViolations;
        private final boolean hasMarginViolations;

        public BoundsCheckResult(List<BoundsViolation> violations) {
            this.violations = new ArrayList<>(violations);
            this.hasCanvasViolations = violations.stream()
                .anyMatch(v -> v.getType() == BoundsViolation.ViolationType.CANVAS_EXCEEDED);
            this.hasMarginViolations = violations.stream()
                .anyMatch(v -> v.getType() == BoundsViolation.ViolationType.MARGIN_EXCEEDED);
        }

        public List<BoundsViolation> getViolations() {
            return violations;
        }

        public boolean hasCanvasViolations() {
            return hasCanvasViolations;
        }

        public boolean hasMarginViolations() {
            return hasMarginViolations;
        }

        public boolean hasAnyViolations() {
            return !violations.isEmpty();
        }

        public int getViolationCount() {
            return violations.size();
        }
    }

    /**
     * Checks canvas bounds only.
     */
    public static BoundsCheckResult checkCanvasBounds(DriverCommand command, ICanvas canvas) {
        CanvasBoundsCheckVisitor visitor = new CanvasBoundsCheckVisitor(canvas, CanvasMargin.none(), false);
        command.accept(visitor);
        return new BoundsCheckResult(visitor.violations);
    }

    /**
     * Checks both canvas bounds and margins.
     */
    public static BoundsCheckResult checkCanvasAndMargins(DriverCommand command, ICanvas canvas, CanvasMargin margin) {
        CanvasBoundsCheckVisitor visitor = new CanvasBoundsCheckVisitor(canvas, margin, true);
        command.accept(visitor);
        return new BoundsCheckResult(visitor.violations);
    }

    @Override
    public void visit(SetPositionCommand setPositionCommand) {
        checkPoint(setPositionCommand.getPosX(), setPositionCommand.getPosY(), 
                   "SetPosition(" + setPositionCommand.getPosX() + ", " + setPositionCommand.getPosY() + ")");
    }

    @Override
    public void visit(OperateToCommand operateToCommand) {
        checkPoint(operateToCommand.getPosX(), operateToCommand.getPosY(),
                   "OperateTo(" + operateToCommand.getPosX() + ", " + operateToCommand.getPosY() + ")");
    }

    @Override
    public void visit(ICompoundCommand iCompoundCommand) {
        Iterator<DriverCommand> iterator = iCompoundCommand.iterator();

        while(iterator.hasNext()) {
            DriverCommand command = iterator.next();
            command.accept(this);
        }
    }

    private void checkPoint(int x, int y, String commandDescription) {
        if (!canvas.containsPoint(x, y)) {
            violations.add(new BoundsViolation(
                BoundsViolation.ViolationType.CANVAS_EXCEEDED, 
                x, y, commandDescription));
            return;
        }

        if (checkMargins && !canvas.containsPointWithMargin(x, y, margin)) {
            violations.add(new BoundsViolation(
                BoundsViolation.ViolationType.MARGIN_EXCEEDED, 
                x, y, commandDescription));
        }
    }
}