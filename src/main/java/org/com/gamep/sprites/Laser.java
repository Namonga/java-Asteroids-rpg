package org.com.gamep.sprites;

import org.com.gamep.GameConfig;

public class Laser extends Sprite {

    private int damage = 1;

    public Laser(String imageFileName) {
        super(imageFileName);
    }

    public Laser(String imageFileName, int damage) {
        super(imageFileName);
        this.damage = damage;
    }

    public boolean isExpired() {
        return elapseTimeSeconds > GameConfig.LASER_LIFETIME;
    }


    public boolean isOutOfBounds() {
        return position.x < 0 || position.x > GameConfig.WORLD_WIDTH ||
                position.y < 0 || position.y > GameConfig.WORLD_HEIGHT;
    }



    @Override
    public void update(double deltaTime) {
        elapseTimeSeconds += deltaTime;
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    public int getDamage() { return damage; }
}