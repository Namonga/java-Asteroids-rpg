package org.com.gamep;

public class Rectangle {
    private double x;
    private double y;
    private double width;
    private double height;

    public Rectangle() {
        this.setPosition(0,0);
        this.setSize(1,1);
    }

    public Rectangle(double x, double y, double width, double height) {
        this.setPosition(x,y);
        this.setSize(width,height);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean overlaps(Rectangle other) {
        boolean noOverlap = this.x + this.width < other.x ||
                other.x + other.width < this.x ||
                this.y + this.height < other.y ||
                other.y + other.height < this.y;

        return !noOverlap;
    }
}