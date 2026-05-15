package org.com.gamep.sprites;


import org.com.gamep.GameConfig;

public class Debris extends Sprite {
    private boolean isLong;
    private double rotationSpeed;

    public Debris(double x, double y, boolean isLong) {
        super(isLong ? "debris_long_33x3.png" : "debris_small_4x4.png");
        this.isLong = isLong;
        this.position.set(x, y);

        double angle = 360 * Math.random();
        velocity.setLength(50 + Math.random() * 100);
        velocity.setAngleInDegrees(angle);

        rotationSpeed = (Math.random() - 0.5) * 10;
    }

    public static Debris createEnemyDebris(double x, double y, boolean isLong) {
        Debris d = new Debris(x, y, isLong);
        d.setImage(isLong ? "new/g_debris_red_long_33x3.png" : "new/g_debris_red_small_4x4.png");
        return d;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        rotationInDegrees += rotationSpeed;
    }

    public boolean shouldDisappear() {
        return elapseTimeSeconds > GameConfig.DEBRIS_LIFETIME;
    }
}