package org.com.gamep.sprites;

import javafx.scene.canvas.GraphicsContext;

public class Loot extends Sprite {
    public enum Type { SCRAP, ENERGY_CELL, ALLOY }


    private Type type;
    private int amount;
    private double lifetime = 8.0;

    public Loot(Type type, int amount, double x, double y) {
        super(getImageName(type));
        this.type = type;
        this.amount = amount;
        position.set(x, y);
        velocity.setLength(15 + Math.random() * 20);
        velocity.setAngleInDegrees(Math.random() * 360);
    }

    private static String getImageName(Type t) {
        switch (t) {
            case ENERGY_CELL: return "new/energy_cell.png";
            case ALLOY: return "new/alloy.png";
            default: return "new/scrap_metal.png";
        }
    }

    public Type getLootType() { return type; }
    public int getAmount() { return amount; }

    public boolean isExpired() {
        return elapseTimeSeconds > lifetime;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext context) {
        super.render(context);
    }
}