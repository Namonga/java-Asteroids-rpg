package org.com.gamep.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.com.gamep.GameConfig;
import org.com.gamep.Rectangle;
import org.com.gamep.Vector;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Sprite {
    public enum Type { SCOUT, TANK }
    public enum Behavior { PATROL, GUARD, HUNTER, ROAMING }

    private Type type;
    private int health;
    private double shootCooldown;
    private double timeSinceLastShot;
    private int scrapValue;
    private double lastDamageTime = -999;
    private static final double FLASH_DURATION = 0.15;
    private int damage;

    private double accelerationRate;
    private double maxSpeed;
    private double deceleration;
    private boolean isThrusting = false;

    private Behavior behavior;
    private Vector guardPost;
    private Vector[] patrolPoints;
    private int currentPatrolIndex = 0;

    private double roamingChangeTimer = 0;

    private boolean isBounty = false;

    public Enemy(Type type, Behavior behavior, Vector homePosition) {
        super(getImageName(type));
        this.type = type;
        this.behavior = behavior;
        this.scale = getScale(type);
        this.health = getMaxHealth(type);
        this.shootCooldown = getShootCooldown(type);
        this.scrapValue = getScrapValue(type);
        this.timeSinceLastShot = Math.random() * shootCooldown;

        if (type == Type.SCOUT) {
            accelerationRate = GameConfig.SCOUT_ACCELERATION;
            maxSpeed = GameConfig.SCOUT_SPEED;
            deceleration = GameConfig.SCOUT_DECELERATION;
            damage = GameConfig.SCOUT_DAMAGE;
        } else {
            accelerationRate = GameConfig.TANK_ACCELERATION;
            maxSpeed = GameConfig.TANK_SPEED;
            deceleration = GameConfig.TANK_DECELERATION;
            damage = GameConfig.TANK_DAMAGE;
        }

        this.rotationInDegrees = Math.random() * 360;
        velocity.set(0, 0);

        position.set(homePosition.x, homePosition.y);


        if (behavior == Behavior.GUARD) {
            this.guardPost = new Vector(homePosition.x, homePosition.y);
        }
        if (behavior == Behavior.PATROL) {
            patrolPoints = new Vector[GameConfig.PATROL_POINTS_COUNT];
            for (int i = 0; i < GameConfig.PATROL_POINTS_COUNT; i++) {
                double angle = Math.random() * 360;
                double dist = 150 + Math.random() * 400; // разброс точек
                double px = homePosition.x + Math.cos(Math.toRadians(angle)) * dist;
                double py = homePosition.y + Math.sin(Math.toRadians(angle)) * dist;
                // Не даём точкам выходить за границы мира (с отступом)
                px = clamp(px, 100, GameConfig.WORLD_WIDTH - 100);
                py = clamp(py, 100, GameConfig.WORLD_HEIGHT - 100);
                patrolPoints[i] = new Vector(px, py);
            }
        }
        this.rotationInDegrees = Math.random() * 360;
        velocity.set(0, 0);
    }

    public List<Debris> explode() {
        List<Debris> list = new ArrayList<>();
        for (int i = 0; i < GameConfig.ENEMY_DEBRIS_COUNT; i++) {
            list.add(Debris.createEnemyDebris(position.x, position.y, i % 2 == 0));
        }
        return list;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    private static String getImageName(Type t) {
        return t == Type.TANK ? "new/R_enemy_tank.png" : "new/R_enemy_scout.png";
    }
    private static int getMaxHealth(Type t) {
        return t == Type.TANK ? GameConfig.TANK_HEALTH : GameConfig.SCOUT_HEALTH;
    }
    private static double getShootCooldown(Type t) {
        return t == Type.TANK ? GameConfig.TANK_SHOOT_COOLDOWN : GameConfig.SCOUT_SHOOT_COOLDOWN;
    }
    private static double getScale(Type t) {
        return t == Type.TANK ? GameConfig.TANK_SCALE : GameConfig.SCOUT_SCALE;
    }
    private static int getScrapValue(Type t) {
        return t == Type.TANK ? GameConfig.TANK_SCRAP_VALUE : GameConfig.SCOUT_SCRAP_VALUE;
    }

    public void takeDamage(int damage) {
        health -= damage;
        lastDamageTime = elapseTimeSeconds;
    }
    public boolean isDestroyed() { return health <= 0; }
    public int getScrapValue() { return scrapValue; }
    public Type getType() { return type; }

    public void updateAI(Vector playerPos, Vector playerVel, List<Enemy> allEnemies, double deltaTime) {
        switch (behavior) {
            case PATROL: patrolAI(playerPos, playerVel, allEnemies, deltaTime); break;
            case GUARD:  guardAI(playerPos, playerVel, allEnemies, deltaTime); break;
            case HUNTER: hunterAI(playerPos, playerVel, allEnemies, deltaTime); break;
            case ROAMING: roamingAI(playerPos, playerVel, allEnemies, deltaTime); break;
        }
    }

    private void moveToTarget(Vector target, double deltaTime, double approachDist) {
        double dx = target.x - position.x;
        double dy = target.y - position.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double desiredAngle = Math.toDegrees(Math.atan2(dy, dx));
        turnTowards(desiredAngle, GameConfig.ENEMY_TURN_SPEED);

        if (dist > approachDist) {
            isThrusting = true;
        } else {
            isThrusting = false;
        }

        if (isThrusting) {
            applyThrust(deltaTime);
        } else {
            applyDeceleration(deltaTime);
        }
        if (velocity.getLength() > maxSpeed) velocity.setLength(maxSpeed);
    }

    private void patrolAI(Vector playerPos, Vector playerVel, List<Enemy> allEnemies, double deltaTime) {
        double distToPlayer = Math.hypot(playerPos.x - position.x, playerPos.y - position.y);
        if (distToPlayer < GameConfig.PATROL_AGGRO_RANGE) {
            hunterAI(playerPos, playerVel, allEnemies, deltaTime);
            return;
        }

        if (patrolPoints == null || patrolPoints.length == 0) return;
        Vector target = patrolPoints[currentPatrolIndex];
        double distToTarget = Math.hypot(target.x - position.x, target.y - position.y);
        if (distToTarget < GameConfig.PATROL_POINT_RADIUS) {
            currentPatrolIndex = (currentPatrolIndex + 1) % patrolPoints.length;
        }
        moveToTarget(target, deltaTime, 30);
        timeSinceLastShot += deltaTime;
    }

    private void guardAI(Vector playerPos, Vector playerVel, List<Enemy> allEnemies, double deltaTime) {
        double distToPlayer = Math.hypot(playerPos.x - position.x, playerPos.y - position.y);
        if (distToPlayer < GameConfig.GUARD_AGGRO_RANGE) {
            hunterAI(playerPos, playerVel, allEnemies, deltaTime);
            return;
        }

        double distToPost = Math.hypot(guardPost.x - position.x, guardPost.y - position.y);
        if (distToPost > GameConfig.GUARD_LEASH_RANGE) {
            moveToTarget(guardPost, deltaTime, 30);
        } else {
            isThrusting = false;
            applyDeceleration(deltaTime);
        }
        timeSinceLastShot += deltaTime;
    }

    private void hunterAI(Vector playerPos, Vector playerVel, List<Enemy> allEnemies, double deltaTime) {
        double dx = playerPos.x - position.x;
        double dy = playerPos.y - position.y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        double timeToReach = dist / maxSpeed;
        double leadX = playerPos.x + playerVel.x * timeToReach * GameConfig.ENEMY_LEAD_FACTOR;
        double leadY = playerPos.y + playerVel.y * timeToReach * GameConfig.ENEMY_LEAD_FACTOR;
        double desiredAngle = Math.toDegrees(Math.atan2(leadY - position.y, leadX - position.x));

        double avoidAngle = 0;
        double avoidStrength = 0;
        for (Enemy other : allEnemies) {
            if (other == this) continue;
            double odx = other.position.x - position.x;
            double ody = other.position.y - position.y;
            double odist = Math.sqrt(odx*odx + ody*ody);
            if (odist > 200) continue;
            double angleToOther = Math.toDegrees(Math.atan2(ody, odx));
            double angleDiff = angleToOther - desiredAngle;
            while (angleDiff > 180) angleDiff -= 360;
            while (angleDiff < -180) angleDiff += 360;
            if (Math.abs(angleDiff) < 40) {
                double strength = 1.0 - (odist / 200.0);
                double sign = Math.signum(angleDiff);
                if (sign == 0) sign = 1;
                avoidAngle += sign * strength * 60;
                avoidStrength += strength;
            }
        }
        if (avoidStrength > 0) desiredAngle += avoidAngle / avoidStrength;

        turnTowards(desiredAngle, GameConfig.ENEMY_TURN_SPEED);

        double approachDist = (type == Type.SCOUT) ? GameConfig.SCOUT_APPROACH_DIST : GameConfig.TANK_APPROACH_DIST;
        if (dist > approachDist + GameConfig.ENEMY_SLOWDOWN_DIST) {
            isThrusting = true;
        } else if (dist > approachDist) {
            isThrusting = false;
            velocity.multiply(0.94);
        } else {
            isThrusting = false;
        }

        if (isThrusting) {
            applyThrust(deltaTime);
        } else {
            applyDeceleration(deltaTime);
        }
        if (velocity.getLength() > maxSpeed) velocity.setLength(maxSpeed);

        timeSinceLastShot += deltaTime;
    }

    private void roamingAI(Vector playerPos, Vector playerVel, List<Enemy> allEnemies, double deltaTime) {
        double distToPlayer = Math.hypot(playerPos.x - position.x, playerPos.y - position.y);
        if (distToPlayer < GameConfig.PATROL_AGGRO_RANGE) {
            hunterAI(playerPos, playerVel, allEnemies, deltaTime);
            return;
        }
        roamingChangeTimer += deltaTime;
        if (roamingChangeTimer > 2 + Math.random() * 2) {
            roamingChangeTimer = 0;
            double newAngle = rotationInDegrees + (Math.random() - 0.5) * 120; // поворот до ±60°
            double speed = maxSpeed * (0.5 + Math.random() * 0.5);
            velocity.setLength(speed);
            velocity.setAngleInDegrees(newAngle);
        }
        isThrusting = true;
        applyThrust(deltaTime);
        if (velocity.getLength() > maxSpeed) velocity.setLength(maxSpeed);
        timeSinceLastShot += deltaTime;
    }

    private void applyThrust(double deltaTime) {
        double angleRad = Math.toRadians(rotationInDegrees);
        velocity.add(
                Math.cos(angleRad) * accelerationRate * deltaTime,
                Math.sin(angleRad) * accelerationRate * deltaTime
        );
    }

    private void applyDeceleration(double deltaTime) {
        velocity.multiply(deceleration);
        if (velocity.getLength() < 0.1) velocity.set(0, 0);
    }

    public void resetShotTimer() {
        timeSinceLastShot = 0;
    }

    public boolean isInShootRange(Vector playerPos) {
        double dx = playerPos.x - position.x;
        double dy = playerPos.y - position.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double range = (type == Type.SCOUT) ? GameConfig.SCOUT_SHOOT_RANGE : GameConfig.TANK_SHOOT_RANGE;
        return dist <= range;
    }

    public boolean canShoot() {
        return timeSinceLastShot >= shootCooldown;
    }

    private void turnTowards(double targetAngle, double turnSpeed) {
        double diff = targetAngle - rotationInDegrees;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        if (Math.abs(diff) < turnSpeed) {
            rotationInDegrees = targetAngle;
        } else {
            rotationInDegrees += Math.signum(diff) * turnSpeed;
        }
    }

    public Vector getNosePosition() {
        double angleRad = Math.toRadians(rotationInDegrees);
        double noseX = position.x + Math.cos(angleRad) * (image.getWidth()/2 * scale);
        double noseY = position.y + Math.sin(angleRad) * (image.getHeight()/2 * scale);
        return new Vector(noseX, noseY);
    }

    public int getDamage() { return damage; }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        clampToWorld();
    }

    @Override
    public void render(GraphicsContext context) {
        super.render(context);
        if (elapseTimeSeconds - lastDamageTime < FLASH_DURATION) {
            Rectangle r = getBoundary();
            context.setFill(Color.rgb(255, 0, 0, 0.5));
            context.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }

    public boolean isBounty() { return isBounty; }
    public void setBounty(boolean bounty) { isBounty = bounty; }


    public void setHealth(int health) { this.health = health; }
    public Behavior getBehavior() {
        return behavior;
    }
}