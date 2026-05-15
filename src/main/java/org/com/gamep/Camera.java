package org.com.gamep;

import org.com.gamep.sprites.Spaceship;

public class Camera {
    private double x, y;
    private double zoom = 1.0;

    private static final double ZOOM_STEP = 0.03;


    public void update(Spaceship ship) {
        x = ship.position.x - GameConfig.CANVAS_WIDTH / 2;
        y = ship.position.y - GameConfig.CANVAS_HEIGHT / 2;
        if (x < 0) x = 0;
        if (x > GameConfig.WORLD_WIDTH - GameConfig.CANVAS_WIDTH) x = GameConfig.WORLD_WIDTH - GameConfig.CANVAS_WIDTH;
        if (y < 0) y = 0;
        if (y > GameConfig.WORLD_HEIGHT - GameConfig.CANVAS_HEIGHT) y = GameConfig.WORLD_HEIGHT - GameConfig.CANVAS_HEIGHT;
    }

    public void zoomIn() {
        zoom = Math.min(zoom + ZOOM_STEP, 1.0);
    }
    public void zoomOut() {
        zoom = Math.max(zoom - ZOOM_STEP, 0.1);
    }

    public void resetZoom() {
        zoom = 1.0;
    }

    public double getZoom() { return zoom; }

    public double getX() { return x; }
    public double getY() { return y; }
}