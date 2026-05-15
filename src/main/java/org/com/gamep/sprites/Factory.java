package org.com.gamep.sprites;

public class Factory extends Sprite {
    public Factory(String imageFile, double x, double y) {
        super(imageFile);
        position.set(x, y);
    }
}