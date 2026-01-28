package edu.kis.powp.jobs2d.canvas;

/**
 * Defines margins around a canvas.
 * <p>
 * Instances can be created using constructors or static factory methods:
 * <ul>
 *     <li>{@link #CanvasMargin(int, int, int, int)} - for specific margins on each side</li>
 *     <li>{@link #CanvasMargin(int)} - for uniform margin on all sides</li>
 *     <li>{@link #standard()} - returns a standard margin of 10 units</li>
 *     <li>{@link #none()} - returns a margin of 0 units</li>
 * </ul>
 */
public class CanvasMargin {
    private final int top;
    private final int bottom;
    private final int left;
    private final int right;

    /**
     * Creates a new CanvasMargin with specific values for each side.
     *
     * @param top    Top margin
     * @param bottom Bottom margin
     * @param left   Left margin
     * @param right  Right margin
     */
    public CanvasMargin(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new CanvasMargin with the same value for all sides.
     *
     * @param margin Margin value for top, bottom, left, and right
     */
    public CanvasMargin(int margin) {
        this(margin, margin, margin, margin);
    }

    /**
     * Returns a CanvasMargin with 0 margin on all sides.
     *
     * @return CanvasMargin with 0 margin
     */
    public static CanvasMargin none() {
        return new CanvasMargin(0);
    }

    /**
     * Returns a standard CanvasMargin.
     * Current standard value is 10 units for all sides.
     *
     * @return Standard CanvasMargin
     */
    public static CanvasMargin standard() {
        return new CanvasMargin(10);
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }
}