package org.com.gamep.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.com.gamep.GameConfig;
import org.com.gamep.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Asteroid extends Sprite {
    private boolean isLarge;
    private int health;

    private double lastDamageTime = -999;
    private static final double DAMAGE_FLASH_DURATION = 0.15;

    public Asteroid() {
        super("asteroid_54x55.png");
        isLarge = true;
        spawnAtEdge();
        this.health = GameConfig.BIG_ASTEROID_HEALTH;

        double angle = 360 * Math.random();
        velocity.setLength(GameConfig.ASTEROID_SPEED);
        velocity.setAngleInDegrees(angle);
    }

    public Asteroid(double x, double y, double inheritedVelocityX, double inheritedVelocityY) {
        super("dwarf_asteroid_37x38.png");
        isLarge = false;
        position.set(x, y);
        this.health = GameConfig.SMALL_ASTEROID_HEALTH; // например, 1

        double angle = 360 * Math.random();
        velocity.setLength(GameConfig.DWARF_ASTEROID_SPEED);
        velocity.setAngleInDegrees(angle);
        velocity.add(inheritedVelocityX * 0.5, inheritedVelocityY * 0.5);
    }

    public void takeDamage(int damage) {
        health -= damage;
        lastDamageTime = elapseTimeSeconds;
    }
    public boolean isDestroyed() {
        return health <= 0;
    }


    private void spawnAtEdge() {
        double randomChoice = Math.random();
        if (randomChoice < 0.25) {
            position.set(-boundary.getWidth(), Math.random() * GameConfig.WORLD_HEIGHT);
        } else if (randomChoice < 0.5) {
            position.set(GameConfig.WORLD_WIDTH + boundary.getWidth(), Math.random() * GameConfig.WORLD_HEIGHT);
        } else if (randomChoice < 0.75) {
            position.set(Math.random() * GameConfig.WORLD_WIDTH, -boundary.getHeight());
        } else {
            position.set(Math.random() * GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT + boundary.getHeight());
        }
    }

    private void spawnInsideWorld() {
        double margin = 80; // отступ от краёв
        position.x = margin + Math.random() * (GameConfig.WORLD_WIDTH - 2 * margin);
        position.y = margin + Math.random() * (GameConfig.WORLD_HEIGHT - 2 * margin);
    }

    public List<Sprite> split() {
        List<Sprite> pieces = new ArrayList<>();

        if (isLarge) {
            int count = 3 + (Math.random() < 0.5 ? 0 : 1);

            for (int i = 0; i < count; i++) {
                pieces.add(new Asteroid(position.x, position.y, velocity.x, velocity.y));
            }

            for (int i = 0; i < GameConfig.DEBRIS_COUNT_ASTEROID; i++) {
                pieces.add(new Debris(position.x, position.y, false));
            }
        } else {
            for (int i = 0; i < GameConfig.DEBRIS_COUNT_ASTEROID; i++) {
                pieces.add(new Debris(position.x, position.y, false));
            }
        }

        return pieces;
    }

    public boolean isLarge() {
        return isLarge;
    }

    public int getPoints() {
        return isLarge ? GameConfig.POINTS_PER_LARGE_ASTEROID : GameConfig.POINTS_PER_DWARF_ASTEROID;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        bounceAtWorldEdges();
    }

    @Override
    public void render(GraphicsContext context) {
        super.render(context);
        if (elapseTimeSeconds - lastDamageTime < DAMAGE_FLASH_DURATION) {
            Rectangle r = getBoundary();
            context.setFill(Color.rgb(0, 0, 0, 0.5));
            context.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }

    private void bounceAtWorldEdges() {
        double halfW = image.getWidth() * scale / 2;
        double halfH = image.getHeight() * scale / 2;

        if (position.x < halfW) {
            position.x = halfW;
            if (velocity.x < 0) {
                velocity.x = -velocity.x * GameConfig.ASTEROID_BOUNCE_FACTOR;
            }
        }
        else if (position.x > GameConfig.WORLD_WIDTH - halfW) {
            position.x = GameConfig.WORLD_WIDTH - halfW;
            if (velocity.x > 0) {
                velocity.x = -velocity.x * GameConfig.ASTEROID_BOUNCE_FACTOR;
            }
        }

        if (position.y < halfH) {
            position.y = halfH;
            if (velocity.y < 0) {
                velocity.y = -velocity.y * GameConfig.ASTEROID_BOUNCE_FACTOR;
            }
        }
        else if (position.y > GameConfig.WORLD_HEIGHT - halfH) {
            position.y = GameConfig.WORLD_HEIGHT - halfH;
            if (velocity.y > 0) {
                velocity.y = -velocity.y * GameConfig.ASTEROID_BOUNCE_FACTOR;
            }
        }
    }


}