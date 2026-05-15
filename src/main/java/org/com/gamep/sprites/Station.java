package org.com.gamep.sprites;

public class Station extends Sprite {
    public Station(String imageFile, double x, double y) {
        super(imageFile);
        this.position.set(x, y);
    }

    @Override
    public void update(double deltaTime) {
    }
}