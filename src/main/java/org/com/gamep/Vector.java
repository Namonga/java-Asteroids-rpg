package org.com.gamep;

public class Vector {
    public double x;
    public double y;

    public Vector() {
        set(0,0);
    }

    public Vector(double x, double y) {
        set(x,y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add( double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void multiply(double multipler) {
        x *= multipler;
        y *= multipler;
    }

    public double getLength() {
        return Math.sqrt( x*x + y*y );
    }

    public void setLength(double targetLength) {
        double currentLength = getLength();

        if (currentLength == 0) {
            set(targetLength, 0);
        } else {
            multiply(1/currentLength);

            multiply(targetLength);
        }
    }

    public double getAngleInDegrees() {
        return Math.toDegrees(Math.atan2(y,x));
    }

    public void setAngleInDegrees(double angleDegrees) {
        double currentLength = getLength();
        double angleRadians = Math.toRadians(angleDegrees);
        x = currentLength * Math.cos(angleRadians);
        y = currentLength * Math.sin(angleRadians);
    }



}
