package net.skittle.lockpicking.UI;

class LockpickingArea {

    public final int centerX;
    public final int centerY;

    public final double baseRadius;
    public final double innerRadiusSq;
    public final double outerRadiusSq;

    public LockpickingArea(
            int centerX,
            int centerY,
            double baseRadius,
            double width
    ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseRadius = baseRadius;

        double outer = baseRadius + (width / 2.0);
        double inner = baseRadius - (width / 2.0);

        this.innerRadiusSq = inner * inner;
        this.outerRadiusSq = outer * outer;
    }

    public boolean contains(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double distSq = dx * dx + dy * dy;

        return distSq >= innerRadiusSq && distSq <= outerRadiusSq;
    }
}
