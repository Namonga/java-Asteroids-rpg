package org.com.gamep.game;

import org.com.gamep.Vector;

public class Zone {
    public enum Type { SAFE, MINING, PIRATE, ABANDONED }

    private Type type;
    private Vector center;
    private double radius;
    private String name;

    public Zone(Type type, double x, double y, double radius, String name) {
        this.type = type;
        this.center = new Vector(x, y);
        this.radius = radius;
        this.name = name;
    }

    public Type getType() { return type; }
    public Vector getCenter() { return center; }
    public double getRadius() { return radius; }
    public String getName() { return name; }

    public boolean contains(Vector point) {
        return Math.hypot(center.x - point.x, center.y - point.y) <= radius;
    }
}