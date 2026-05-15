package org.com.gamep.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.com.gamep.GameConfig;
import org.com.gamep.Rectangle;
import org.com.gamep.Vector;

public class Sprite {
    public Vector position;
    public Vector velocity;
    public double rotationInDegrees;
    public Rectangle boundary;
    public Image image;
    public double elapseTimeSeconds;

    protected double scale = 1.0;



    public void clampToWorld() {
        double halfW = image.getWidth() / 2;
        double halfH = image.getHeight() / 2;
        if (position.x < halfW) position.x = halfW;
        if (position.x > GameConfig.WORLD_WIDTH - halfW) position.x = GameConfig.WORLD_WIDTH - halfW;
        if (position.y < halfH) position.y = halfH;
        if (position.y > GameConfig.WORLD_HEIGHT - halfH) position.y = GameConfig.WORLD_HEIGHT - halfH;
    }


    public Sprite() {
        position = new Vector();
        velocity = new Vector();
        rotationInDegrees = 0;
        boundary = new Rectangle();
        elapseTimeSeconds = 0;
    }

    public Sprite(String imageFileName) {
        this();
        setImage(imageFileName);
    }

    public void setImage(String imageFileName) {
        image = new Image(imageFileName);
        boundary.setSize(image.getWidth(), image.getHeight());
    }

    public Rectangle getBoundary() {
        double w = image.getWidth() * scale;
        double h = image.getHeight() * scale;
        boundary.setPosition(position.x - w/2, position.y - h/2);
        boundary.setSize(w, h);
        return boundary;
    }

    public Vector getPosition() {
        return position;
    }

    public boolean overlaps(Sprite other) {
        return this.getBoundary().overlaps(other.getBoundary());
    }

    public void wrap(double screenWidth, double screenHeight) {
        double halfWidth = image.getWidth()/2;
        double halfHeight = image.getHeight()/2;

        if (position.x + halfWidth < 0) {
            position.x = screenWidth + halfWidth;
        }
        if (position.x > screenWidth + halfWidth) {
            position.x = -halfWidth;
        }
        if (position.y + halfHeight < 0) {
            position.y = screenHeight + halfHeight;
        }
        if (position.y > screenHeight + halfHeight) {
            position.y = -halfHeight;
        }
    }

    public void update(double deltaTime) {
        elapseTimeSeconds += deltaTime;
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

    }

    public static boolean DEBUG_HITBOX = false;

    public void render(GraphicsContext context) {
        context.save();
        context.translate(position.x, position.y);
        context.rotate(rotationInDegrees);
        context.translate(-image.getWidth()/2, -image.getHeight()/2);
        context.drawImage(image, 0,0);
        context.restore();

        if (DEBUG_HITBOX) {
            Rectangle r = getBoundary();
            context.setStroke(Color.CYAN);
            context.setLineWidth(1.5);
            context.strokeRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }



}
