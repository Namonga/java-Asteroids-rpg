package org.com.gamep.sprites;

import org.com.gamep.GameConfig;


import java.util.ArrayList;
import java.util.List;

public class Spaceship extends Sprite {
    private double accelerationX = 0;
    private double accelerationY = 0;
    private boolean isThrusting = false;
    private String normalImage;
    private String thrustImage;

    private int health;
    private int maxHealth;

    private int maxHealthBonus = 0;
    private double speedMultiplier = 1.0;
    private int laserDamage = 1;
    private double shootCooldown = 0.3;
    private double timeSinceLastShot = 0;

    private double magnetRadius = 50.0;

    public Spaceship(String normalImageFileName, String thrustImageFileName) {
        super(normalImageFileName);
        this.normalImage = normalImageFileName;
        this.thrustImage = thrustImageFileName;
        position.set(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2 - 300);

        health = GameConfig.PLAYER_MAX_HEALTH;
        maxHealth = GameConfig.PLAYER_MAX_HEALTH;
    }

    public void rotateLeft() {
        rotationInDegrees -= GameConfig.SPACESHIP_ROTATION_SPEED;
    }

    public void rotateRight() {
        rotationInDegrees += GameConfig.SPACESHIP_ROTATION_SPEED;
    }

    public void thrust() {
        isThrusting = true;
        double angleRadians = Math.toRadians(rotationInDegrees);
        accelerationX = Math.cos(angleRadians) * GameConfig.SPACESHIP_ACCELERATION;
        accelerationY = Math.sin(angleRadians) * GameConfig.SPACESHIP_ACCELERATION;

        velocity.add(accelerationX, accelerationY);

        if (velocity.getLength() > GameConfig.SPACESHIP_MAX_SPEED * speedMultiplier) {
            velocity.setLength(GameConfig.SPACESHIP_MAX_SPEED * speedMultiplier);
        }
    }

    public void stopThrust() {
        isThrusting = false;
    }

    public void applyDeceleration() {
        velocity.multiply(GameConfig.SPACESHIP_DECELERATION);

        if (velocity.getLength() < 0.5) {
            velocity.set(0, 0);
        }
    }

    public Laser tryShoot() {
        if (elapseTimeSeconds - timeSinceLastShot >= shootCooldown) {
            timeSinceLastShot = elapseTimeSeconds;
            Laser laser = new Laser("laser_8x8.png", laserDamage);
            laser.position.set(position.x, position.y);
            laser.velocity.setLength(GameConfig.LASER_SPEED);
            laser.velocity.setAngleInDegrees(rotationInDegrees);
            laser.velocity.add(velocity.x, velocity.y);
            return laser;
        }
        return null;
    }

    public List<Debris> explode() {
        List<Debris> debrisList = new ArrayList<>();
        for (int i = 0; i < GameConfig.DEBRIS_COUNT_SHIP; i++) {
            debrisList.add(new Debris(position.x, position.y, i % 2 == 0));
        }
        return debrisList;
    }

    @Override
    public void update(double deltaTime) {
        applyDeceleration();
        super.update(deltaTime);
        clampToWorld();
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext context) {
        String currentImagePath = isThrusting ? thrustImage : normalImage;
        if (!image.getUrl().endsWith(currentImagePath)) {
            setImage(currentImagePath);
        }
        super.render(context);
    }

    public void brake() {
        velocity.multiply(0.9);
        if (velocity.getLength() < 0.2) velocity.set(0, 0);
    }

    // Методы для покупок
    public void repairFull() { health = maxHealth + maxHealthBonus; }
    public void increaseMaxHealth(int amount) { maxHealthBonus += amount; health += amount; }
    public void increaseSpeed(double multiplier) { speedMultiplier += multiplier; }
    public void increaseLaserDamage(int dmg) { laserDamage += dmg; }
    public void decreaseShootCooldown(double amount) { shootCooldown = Math.max(0.1, shootCooldown - amount); }

    public int getMaxHealth() { return maxHealth + maxHealthBonus; }
    public int getLaserDamage() { return laserDamage; }
    public double getShootCooldown() { return shootCooldown; }

    public void takeDamage(int amount) {
        health -= amount;
    }
    public int getHealth() { return health; }
    public boolean isAlive() { return health > 0; }
    public double getSpeedMultiplier() { return speedMultiplier; }

    public double getMagnetRadius() { return magnetRadius; }
    public void increaseMagnetRadius(double amount) { magnetRadius += amount; }
}