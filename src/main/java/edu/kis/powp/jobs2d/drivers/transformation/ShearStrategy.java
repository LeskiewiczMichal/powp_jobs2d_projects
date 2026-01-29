package edu.kis.powp.jobs2d.drivers.transformation;

public class ShearStrategy implements TransformStrategy {
    private final double shearX;
    private final double shearY;

    public ShearStrategy(double shearX, double shearY) {
        this.shearX = shearX;
        this.shearY = shearY;
    }

    @Override
    public TransformCords transform(TransformCords cords) {
        return new TransformCords(
                (int) (cords.x + cords.y * shearX),
                (int) (cords.y + cords.x * shearY)
        );
    }
}