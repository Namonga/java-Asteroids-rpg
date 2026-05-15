package org.com.gamep;

import org.com.gamep.game.Zone;

import java.util.List;

public class GameConfig {
    public static final int CANVAS_WIDTH = 1200;
    public static final int CANVAS_HEIGHT = 800;

    public static final int WORLD_WIDTH = 30000;
    public static final int WORLD_HEIGHT = 30000;

    // Stations positions
    public static final double STATION_X = WORLD_WIDTH / 2.0;
    public static final double STATION_Y = WORLD_HEIGHT / 2.0;

    public static final double FACTORY_X = WORLD_WIDTH - 2000;
    public static final double FACTORY_Y = WORLD_HEIGHT / 2.0;

    // Planets
    public static final double[] PLANET1 = {4000, 4000};
    public static final double[] PLANET2 = {20000, 10000};
    public static final double[] PLANET3 = {8000, 25000};
    public static final double[] PLANET4 = {26000, 22000};
    public static final double[] PLANET5 = {5000, 18000};
    public static final double[] PLANET6 = {15000, 5000};
    public static final double[] PLANET7 = {25000, 15000};
    public static final double[] PLANET8 = {10000, 10000};

    // Zones
    public static final Zone SAFE_ZONE = new Zone(Zone.Type.SAFE, STATION_X, STATION_Y, 1200, "Safe Zone");
    public static final Zone MINING_BELT_1 = new Zone(Zone.Type.MINING, 6000, 6000, 1200, "Mining Belt Alpha");
    public static final Zone MINING_BELT_2 = new Zone(Zone.Type.MINING, 22000, 18000, 1400, "Mining Belt Beta");
    public static final Zone PIRATE_OUTPOST = new Zone(Zone.Type.PIRATE, 12000, 22000, 1200, "Pirate Outpost");
    public static final Zone ABANDONED_STATION = new Zone(Zone.Type.ABANDONED, 25000, 8000, 1000, "Abandoned Station");

    public static final List<Zone> ZONES = List.of(SAFE_ZONE, MINING_BELT_1, MINING_BELT_2, PIRATE_OUTPOST, ABANDONED_STATION);

    // Spaceship settings
    public static final double SPACESHIP_ROTATION_SPEED = 3.0;
    public static final double SPACESHIP_ACCELERATION = 30.0;
    public static final double SPACESHIP_DECELERATION = 0.98;
    public static final double SPACESHIP_MAX_SPEED = 2000;
    public static final int PLAYER_MAX_HEALTH = 300;

    // Bounty
    public static final double BOUNTY_TIME_LIMIT = 120.0;
    public static final int BOUNTY_REWARD_SCRAP = 50;
    public static final int BOUNTY_REWARD_CELLS = 5;
    public static final int BOUNTY_REWARD_ALLOY = 3;

    // Drop
    public static final int TANK_ALLOY_DROP_CHANCE = 40;
    public static final int ALLOY_DROP_AMOUNT = 1;

    // Enemy
    public static final int INITIAL_ENEMY_COUNT = 25;
    public static final int ENEMY_DEBRIS_COUNT = 9;
    public static final int MAX_ENEMIES_PER_ZONE = 15;
    public static final double FAST_SPAWN_DURATION = 10.0;
    public static final double FAST_SPAWN_INTERVAL = 0.1;
    public static final double NORMAL_RESPAWN_INTERVAL = 30.0;


    // Enemy Scout
    public static final double SCOUT_SPEED = 300;
    public static final double SCOUT_ACCELERATION = 80;
    public static final double SCOUT_DECELERATION = 0.98;
    public static final double SCOUT_SHOOT_COOLDOWN = 1.5;
    public static final double SCOUT_SCALE = 1.0;
    public static final int SCOUT_SCRAP_VALUE = 3;

    public static final int SCOUT_HEALTH = 3;
    public static final double SCOUT_SHOOT_RANGE = 300;
    public static final double SCOUT_APPROACH_DIST = 200;
    public static final int SCOUT_DAMAGE = 1;

    // Enemy Tank
    public static final double TANK_SPEED = 200;
    public static final double TANK_ACCELERATION = 40;
    public static final double TANK_DECELERATION = 0.98;
    public static final double TANK_SHOOT_COOLDOWN = 2.5;
    public static final double TANK_SCALE = 1.5;
    public static final int TANK_SCRAP_VALUE = 10;

    public static final int TANK_HEALTH = 5;
    public static final double TANK_SHOOT_RANGE = 900;
    public static final double TANK_APPROACH_DIST = 800;
    public static final int TANK_DAMAGE = 2;

    // Enemy AI behaviour
    public static final double ENEMY_TURN_SPEED = 3.0;
    public static final double ENEMY_LEAD_FACTOR = 0.3;
    public static final double ENEMY_SLOWDOWN_DIST = 120;
    public static final double ENEMY_REVERSE_THRUST = 0.5;

    // Enemy Behaviour Roles
    public static final double PATROL_AGGRO_RANGE = 1000;
    public static final double GUARD_AGGRO_RANGE = 1300;
    public static final double HUNTER_AGGRO_RANGE = 900;
    public static final double PATROL_POINT_RADIUS = 250;
    public static final int PATROL_POINTS_COUNT = 3;
    public static final double GUARD_LEASH_RANGE = 550;

    //Asteroids HP
    public static final int BIG_ASTEROID_HEALTH = 7;
    public static final int SMALL_ASTEROID_HEALTH = 1;

    // Asteroid settings (Easy/Hard)
    public static double ASTEROID_SPEED = 3;
    public static double DWARF_ASTEROID_SPEED = 3;

    // Laser settings
    public static final double LASER_SPEED = 800;
    public static final double LASER_LIFETIME = 2.0;

    // Game settings
    public static final int MIN_ASTEROIDS = 12;
    public static final double ASTEROID_BOUNCE_FACTOR = 0.8;

    public static int INITIAL_ASTEROID_COUNT = 3500;
    public static final int ASTEROID_INCREMENT_PER_WAVE = 2;
    public static final int MAX_ASTEROIDS_PER_WAVE = 15;
    public static final int POINTS_PER_LARGE_ASTEROID = 100;
    public static final int POINTS_PER_DWARF_ASTEROID = 50;

    // Debris settings
    public static final double DEBRIS_LIFETIME = 2.0; // seconds
    public static final int DEBRIS_COUNT_SHIP = 8;
    public static final int DEBRIS_COUNT_ASTEROID = 5;


    // Difficulty presets
    public static void setEasyMode() {
        ASTEROID_SPEED = 40;
        DWARF_ASTEROID_SPEED = 70;
        INITIAL_ASTEROID_COUNT = 4;
    }

    public static void setHardMode() {
        ASTEROID_SPEED = 70;
        DWARF_ASTEROID_SPEED = 100;
        INITIAL_ASTEROID_COUNT = 8;
    }
}