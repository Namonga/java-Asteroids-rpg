package org.com.gamep.sprites;

public class Planet extends Sprite {
    public Planet(String imageFile, double x, double y, double scale) {
        super(imageFile);
        position.set(x, y);
        this.scale = scale;
    }

    @Override
    public void update(double deltaTime) {
    }
}