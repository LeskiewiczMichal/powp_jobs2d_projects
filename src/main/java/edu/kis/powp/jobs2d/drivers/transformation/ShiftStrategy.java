package edu.kis.powp.jobs2d.drivers.transformation;

public class ShiftStrategy implements TransformStrategy {
    private final int shiftX;
    private final int shiftY;

    public ShiftStrategy(int shiftX, int shiftY) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public TransformCords transform(TransformCords cords) {
        return new TransformCords(cords.x + shiftX, cords.y + shiftY);
    }
}