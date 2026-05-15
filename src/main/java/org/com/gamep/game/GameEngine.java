package org.com.gamep.game;

import org.com.gamep.GameConfig;
import org.com.gamep.Vector;
import org.com.gamep.sprites.Asteroid;
import org.com.gamep.sprites.Debris;
import org.com.gamep.sprites.Enemy;
import org.com.gamep.sprites.Factory;
import org.com.gamep.sprites.Laser;
import org.com.gamep.sprites.Loot;
import org.com.gamep.sprites.Planet;
import org.com.gamep.sprites.Spaceship;
import org.com.gamep.sprites.Sprite;
import org.com.gamep.sprites.Station;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine {
    private boolean gameOver;

    private Station station;
    private Factory factory;

    private Spaceship spaceShip;
    private ArrayList<Laser> laserList;
    private ArrayList<Asteroid> asteroidList;
    private ArrayList<Debris> debrisList;

    private ArrayList<Enemy> enemyList;
    private ArrayList<Laser> enemyLaserList;
    private int scrap;  // gold of player

    // loot
    private ArrayList<Loot> lootList;
    private int energyCells = 0;
    private int alloy = 0;

    private ArrayList<Mission> availableMissions;
    private Mission activeMission;

    private ArrayList<Planet> planetList;
    private double respawnTimer = 0;
    private double fastTimer = 0;
    private double fastSpawnTimer = 0;
    private boolean fastActive = true;

    public GameEngine() {
        reset();
    }

    public void reset() {
        gameOver = false;

        spaceShip = new Spaceship("spaceship_32x23.png", "spaceship_thrust_39x23.png");
        station = new Station("new/shop.png", GameConfig.STATION_X, GameConfig.STATION_Y);
        factory = new Factory("new/factory.png", GameConfig.FACTORY_X, GameConfig.FACTORY_Y);

        laserList = new ArrayList<>();
        asteroidList = new ArrayList<>();
        debrisList = new ArrayList<>();

        enemyList = new ArrayList<>();
        lootList = new ArrayList<>();
        energyCells = 100;
        enemyLaserList = new ArrayList<>();
        scrap = 1000;
        alloy = 100;

        respawnTimer = 0;
        fastTimer = 0;
        fastSpawnTimer = 0;
        fastActive = true;

        availableMissions = new ArrayList<>();
        generateMissions();
        activeMission = null;

        planetList = new ArrayList<>();
        planetList.add(new Planet("new/planet.png", GameConfig.PLANET1[0], GameConfig.PLANET1[1], 3.0));
        planetList.add(new Planet("new/planet_rocky.png", GameConfig.PLANET2[0], GameConfig.PLANET2[1], 2.5));
        planetList.add(new Planet("new/planet_ice.png", GameConfig.PLANET3[0], GameConfig.PLANET3[1], 2.0));
        planetList.add(new Planet("new/planet_desert.png", GameConfig.PLANET4[0], GameConfig.PLANET4[1], 2.8));
        planetList.add(new Planet("new/planet_moon.png", GameConfig.PLANET5[0], GameConfig.PLANET5[1], 1.0));
        planetList.add(new Planet("new/planet_2.png", GameConfig.PLANET6[0], GameConfig.PLANET6[1], 3.5));
        planetList.add(new Planet("new/planet_ocean.png", GameConfig.PLANET7[0], GameConfig.PLANET7[1], 2.2));
        planetList.add(new Planet("new/planet_volcanic.png", GameConfig.PLANET8[0], GameConfig.PLANET8[1], 2.0));

        spawnAsteroids(GameConfig.INITIAL_ASTEROID_COUNT);

    }

    public Station getStation() {
        return station;
    }

    public void update(double deltaTime) {
        if (gameOver) return;

        spaceShip.update(deltaTime);

        for (Asteroid asteroid : asteroidList) {
            asteroid.update(deltaTime);
        }

        // Обновление врагов
        for (Enemy enemy : enemyList) {
            enemy.updateAI(spaceShip.position, spaceShip.velocity, enemyList, deltaTime);
            if (enemy.canShoot() && enemy.isInShootRange(spaceShip.getPosition())) {
                Laser enemyLaser = enemyShoot(enemy);
                if (enemyLaser != null) enemyLaserList.add(enemyLaser);
                enemy.resetShotTimer();
            }
            enemy.update(deltaTime);
        }

        updateLasers(deltaTime);

        Iterator<Laser> enemyLaserIter = enemyLaserList.iterator();
        while (enemyLaserIter.hasNext()) {
            Laser laser = enemyLaserIter.next();
            laser.update(deltaTime);
            if (laser.isExpired() || laser.isOutOfBounds()) {
                enemyLaserIter.remove();
            }
        }

        separateEnemies();
        updateDebris(deltaTime);
        updateLoot(deltaTime);
        attractLoot(deltaTime);
        checkPlayerLootCollisions();
        checkCollisions();

        if (fastActive) {
            fastTimer += deltaTime;
            fastSpawnTimer += deltaTime;
            if (fastSpawnTimer >= GameConfig.FAST_SPAWN_INTERVAL) {
                fastSpawnTimer = 0;
                spawnSingleEnemy();
            }
            if (fastTimer >= GameConfig.FAST_SPAWN_DURATION) {
                fastActive = false;
                respawnTimer = 0;
            }
        }

        if (activeMission != null && activeMission.getType() == Mission.Type.BOUNTY) {
            activeMission.reduceTime(deltaTime);
            if (activeMission.isTimeExpired()) {
                enemyList.removeIf(e -> e.isBounty());
                abortMission();
            }
        }


        respawnTimer += deltaTime;
        if (respawnTimer > 30.0) {
            respawnTimer = 0;
            for (Zone zone : GameConfig.ZONES) {
                if (zone.getType() == Zone.Type.SAFE) continue;
                int current = countEnemiesInZone(zone);
                if (current < GameConfig.MAX_ENEMIES_PER_ZONE) {
                    Vector home = randomPointInZone(zone);
                    Enemy.Behavior behavior = (zone.getType() == Zone.Type.PIRATE) ? Enemy.Behavior.PATROL : Enemy.Behavior.GUARD;
                    Enemy.Type type = Math.random() < 0.7 ? Enemy.Type.SCOUT : Enemy.Type.TANK;
                    enemyList.add(new Enemy(type, behavior, home));
                }
            }
            if (Math.random() < 0.3) {
                int groupSize = 3 + (int) (Math.random() * 3); // 3-5
                double groupX = 1000 + Math.random() * (GameConfig.WORLD_WIDTH - 2000);
                double groupY = 1000 + Math.random() * (GameConfig.WORLD_HEIGHT - 2000);
                for (int i = 0; i < groupSize; i++) {
                    Vector pos = new Vector(groupX + Math.random() * 300 - 150, groupY + Math.random() * 300 - 150);
                    Enemy.Type type = Math.random() < 0.7 ? Enemy.Type.SCOUT : Enemy.Type.TANK;
                    Enemy enemy = new Enemy(type, Enemy.Behavior.ROAMING, pos);
                    enemy.velocity.setLength(100 + Math.random() * 200);
                    enemy.velocity.setAngleInDegrees(Math.random() * 360);
                    enemyList.add(enemy);
                }
            }
        }

    }

    private void spawnSingleEnemy() {
        Vector home;
        Enemy.Behavior behavior;
        if (Math.random() < 0.5) {
            home = new Vector(1000 + Math.random() * (GameConfig.WORLD_WIDTH - 2000), 1000 + Math.random() * (GameConfig.WORLD_HEIGHT - 2000));
            behavior = Enemy.Behavior.ROAMING;
        } else {
            Zone zone = getRandomZone();
            home = randomPointInZone(zone);
            behavior = (zone.getType() == Zone.Type.PIRATE) ? Enemy.Behavior.PATROL : Enemy.Behavior.GUARD;
        }
        Enemy.Type type = Math.random() < 0.7 ? Enemy.Type.SCOUT : Enemy.Type.TANK;
        Enemy enemy = new Enemy(type, behavior, home);
        if (behavior == Enemy.Behavior.ROAMING) {
            enemy.velocity.setLength(100 + Math.random() * 200);
            enemy.velocity.setAngleInDegrees(Math.random() * 360);
        }
        enemyList.add(enemy);
    }

    private void updateLasers(double deltaTime) {
        Iterator<Laser> laserIterator = laserList.iterator();
        while (laserIterator.hasNext()) {
            Laser laser = laserIterator.next();
            laser.update(deltaTime);
            if (laser.isExpired() || laser.isOutOfBounds()) {
                laserIterator.remove();
            }
        }
    }

    private void updateDebris(double deltaTime) {
        Iterator<Debris> debrisIterator = debrisList.iterator();
        while (debrisIterator.hasNext()) {
            Debris debris = debrisIterator.next();
            debris.update(deltaTime);
            if (debris.shouldDisappear()) {
                debrisIterator.remove();
            }
        }
    }

    private void checkCollisions() {
        checkLaserAsteroidCollisions();
        checkSpaceshipAsteroidCollisions();
        checkEnemyCollisions();
    }

    private void checkLaserAsteroidCollisions() {
        Iterator<Laser> laserIterator = laserList.iterator();
        while (laserIterator.hasNext()) {
            Laser laser = laserIterator.next();

            Iterator<Asteroid> asteroidIterator = asteroidList.iterator();
            while (asteroidIterator.hasNext()) {
                Asteroid asteroid = asteroidIterator.next();

                if (laser.overlaps(asteroid)) {
                    laserIterator.remove();
                    asteroid.takeDamage(laser.getDamage());
                    if (asteroid.isDestroyed()) {
                        asteroidIterator.remove();
                        addMissionProgress(Mission.Type.DESTROY_ASTEROIDS, 1);
                        for (Sprite sprite : asteroid.split()) {
                            if (sprite instanceof Asteroid) {
                                asteroidList.add((Asteroid) sprite);
                            } else if (sprite instanceof Debris) {
                                debrisList.add((Debris) sprite);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void checkSpaceshipAsteroidCollisions() {
        Iterator<Asteroid> asteroidIterator = asteroidList.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            if (spaceShip.overlaps(asteroid)) {
                // Урон игроку (с учётом кулдауна)
                spaceShip.takeDamage(2); // чуть больше, чем от лазера
                if (!spaceShip.isAlive()) {
                    debrisList.addAll(spaceShip.explode());
                    gameOver = true;
                    break;
                }
                // Уничтожаем астероид
                asteroidIterator.remove();
                addMissionProgress(Mission.Type.DESTROY_ASTEROIDS, 1);
                for (Sprite sprite : asteroid.split()) {
                    if (sprite instanceof Asteroid) {
                        asteroidList.add((Asteroid) sprite);
                    } else if (sprite instanceof Debris) {
                        debrisList.add((Debris) sprite);
                    }
                }
                break; // не проверяем остальные в этом кадре
            }
        }
    }

    private void checkWaveCompletion() {
        if (asteroidList.isEmpty()) {
            int asteroidsToSpawn = Math.min(GameConfig.INITIAL_ASTEROID_COUNT + 1 * GameConfig.ASTEROID_INCREMENT_PER_WAVE, GameConfig.MAX_ASTEROIDS_PER_WAVE);
            spawnAsteroids(asteroidsToSpawn);
        }
    }

    private void spawnAsteroids(int count) {
        for (int i = 0; i < count; i++) {
            asteroidList.add(new Asteroid());
        }
    }

    public void processInput(InputHandler input) {
        if (input.isKeyPressed("LEFT") || input.isKeyPressed("A")) {
            spaceShip.rotateLeft();
        }

        if (input.isKeyPressed("RIGHT") || input.isKeyPressed("D")) {
            spaceShip.rotateRight();
        }

        if (input.isKeyPressed("UP") || input.isKeyPressed("W")) {
            spaceShip.thrust();
        } else {
            spaceShip.stopThrust();
        }

        if (input.isKeyJustPressed("SPACE")) { // убрали S
            Laser laser = spaceShip.tryShoot();
            if (laser != null) {
                laserList.add(laser);
            }
        }
        if (input.isKeyPressed("S")) {
            spaceShip.brake();
        }
    }

    public void shootLaser() {
        Laser laser = spaceShip.tryShoot();
        if (laser != null) {
            laserList.add(laser);
        }
    }


    private void spawnEnemies(int totalCount) {
        int spawned = 0;
        int attempts = 0;
        while (spawned < totalCount && attempts < totalCount * 2) {
            attempts++;
            Zone zone = getRandomZone();
            if (countEnemiesInZone(zone) < GameConfig.MAX_ENEMIES_PER_ZONE) {
                Vector home = randomPointInZone(zone);
                Enemy.Behavior behavior = (zone.getType() == Zone.Type.PIRATE) ? Enemy.Behavior.PATROL : Enemy.Behavior.GUARD;
                Enemy.Type type = Math.random() < 0.7 ? Enemy.Type.SCOUT : Enemy.Type.TANK;
                enemyList.add(new Enemy(type, behavior, home));
                spawned++;
            }
        }
    }

    private Zone getRandomZone() {
        List<Zone> zones = List.of(GameConfig.MINING_BELT_1, GameConfig.MINING_BELT_2, GameConfig.PIRATE_OUTPOST, GameConfig.ABANDONED_STATION);
        return zones.get((int) (Math.random() * zones.size()));
    }

    private Vector randomPointInZone(Zone zone) {
        double angle = Math.random() * 360;
        double dist = Math.random() * zone.getRadius();
        double px = zone.getCenter().x + Math.cos(Math.toRadians(angle)) * dist;
        double py = zone.getCenter().y + Math.sin(Math.toRadians(angle)) * dist;
        return new Vector(px, py);
    }

    private Laser enemyShoot(Enemy enemy) {
        Laser laser = new Laser("new/laser_enemy_8x8.png", enemy.getDamage());
        Vector nose = enemy.getNosePosition();
        laser.position.set(nose.x, nose.y);
        laser.velocity.setLength(GameConfig.LASER_SPEED);
        laser.velocity.setAngleInDegrees(enemy.rotationInDegrees);
        laser.velocity.add(enemy.velocity.x, enemy.velocity.y);
        return laser;
    }

    private void checkEnemyCollisions() {
        // Враг против астероидов
        Iterator<Enemy> enemyIter3 = enemyList.iterator();
        while (enemyIter3.hasNext()) {
            Enemy enemy = enemyIter3.next();
            Iterator<Asteroid> asteroidIter2 = asteroidList.iterator();
            while (asteroidIter2.hasNext()) {
                Asteroid asteroid = asteroidIter2.next();
                if (enemy.overlaps(asteroid)) {
                    asteroidIter2.remove();
                    enemy.takeDamage(1);
                    if (enemy.isDestroyed()) {
                        enemyIter3.remove();
                        debrisList.addAll(enemy.explode());
                        Loot.Type lootType;
                        int amount;
                        if (enemy.getType() == Enemy.Type.TANK && Math.random() * 100 < GameConfig.TANK_ALLOY_DROP_CHANCE) {
                            lootType = Loot.Type.ALLOY;
                            amount = GameConfig.ALLOY_DROP_AMOUNT;
                        } else {
                            lootType = Math.random() < 0.3 ? Loot.Type.ENERGY_CELL : Loot.Type.SCRAP;
                            amount = (lootType == Loot.Type.SCRAP) ? enemy.getScrapValue() : 1;
                        }
                        lootList.add(new Loot(lootType, amount, enemy.position.x, enemy.position.y));
                    }
                    break;
                }
            }
        }

        // Таран врагом игрока
        Iterator<Enemy> enemyIter2 = enemyList.iterator();
        while (enemyIter2.hasNext()) {
            Enemy enemy = enemyIter2.next();
            if (spaceShip.overlaps(enemy)) {
                enemyIter2.remove();
                if (enemy.isBounty() && activeMission != null && activeMission.getType() == Mission.Type.BOUNTY) {
                    addMissionProgress(Mission.Type.BOUNTY, 1);
                }
                if (enemy.getType() == Enemy.Type.SCOUT) {
                    addMissionProgress(Mission.Type.KILL_SCOUTS, 1);
                } else if (enemy.getType() == Enemy.Type.TANK) {
                    addMissionProgress(Mission.Type.KILL_TANKS, 1);
                }
                debrisList.addAll(enemy.explode());
                spaceShip.takeDamage(2);
                if (!spaceShip.isAlive()) {
                    debrisList.addAll(spaceShip.explode());
                    gameOver = true;
                }
                break;
            }
        }

        // Лазеры игрока против врагов
        Iterator<Laser> playerLaserIter = laserList.iterator();
        while (playerLaserIter.hasNext()) {
            Laser laser = playerLaserIter.next();
            Iterator<Enemy> enemyIter = enemyList.iterator();
            while (enemyIter.hasNext()) {
                Enemy enemy = enemyIter.next();
                if (laser.overlaps(enemy)) {
                    playerLaserIter.remove();
                    enemy.takeDamage(laser.getDamage());
                    if (enemy.isDestroyed()) {
                        enemyIter.remove();
                        if (enemy.isBounty() && activeMission != null && activeMission.getType() == Mission.Type.BOUNTY) {
                            addMissionProgress(Mission.Type.BOUNTY, 1);
                        }
                        if (enemy.getType() == Enemy.Type.SCOUT) {
                            addMissionProgress(Mission.Type.KILL_SCOUTS, 1);
                        } else if (enemy.getType() == Enemy.Type.TANK) {
                            addMissionProgress(Mission.Type.KILL_TANKS, 1);
                        }
                        debrisList.addAll(enemy.explode());
                        Loot.Type lootType;
                        int amount;
                        if (enemy.getType() == Enemy.Type.TANK && Math.random() * 100 < GameConfig.TANK_ALLOY_DROP_CHANCE) {
                            lootType = Loot.Type.ALLOY;
                            amount = GameConfig.ALLOY_DROP_AMOUNT;
                        } else {
                            lootType = Math.random() < 0.3 ? Loot.Type.ENERGY_CELL : Loot.Type.SCRAP;
                            amount = (lootType == Loot.Type.SCRAP) ? enemy.getScrapValue() : 1;
                        }
                        lootList.add(new Loot(lootType, amount, enemy.position.x, enemy.position.y));
                    }
                    break;
                }
            }
        }

        // Вражеские лазеры против игрока
        Iterator<Laser> enemyLaserIter = enemyLaserList.iterator();
        while (enemyLaserIter.hasNext()) {
            Laser laser = enemyLaserIter.next();
            if (laser.overlaps(spaceShip)) {
                enemyLaserIter.remove();
                spaceShip.takeDamage(laser.getDamage());
                if (!spaceShip.isAlive()) {
                    debrisList.addAll(spaceShip.explode());
                    gameOver = true;
                }
                break;
            }
        }
    }

    private int countEnemiesInZone(Zone zone) {
        int count = 0;
        for (Enemy e : enemyList) {
            if (zone.contains(e.position)) count++;
        }
        return count;
    }

    private void attractLoot(double deltaTime) {
        double magnet = spaceShip.getMagnetRadius();
        Vector shipPos = spaceShip.position;
        for (Loot loot : lootList) {
            double dist = Math.hypot(loot.position.x - shipPos.x, loot.position.y - shipPos.y);
            if (dist <= magnet && dist > 0) {
                loot.velocity.setLength(150); // скорость притяжения
                loot.velocity.setAngleInDegrees(Math.toDegrees(Math.atan2(shipPos.y - loot.position.y, shipPos.x - loot.position.x)));
            }
        }
    }

    private void separateEnemies() {
        for (int i = 0; i < enemyList.size(); i++) {
            Enemy a = enemyList.get(i);
            for (int j = i + 1; j < enemyList.size(); j++) {
                Enemy b = enemyList.get(j);
                if (a.overlaps(b)) {
                    // Вектор раздвижения
                    double dx = b.position.x - a.position.x;
                    double dy = b.position.y - a.position.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    double overlap = (a.getBoundary().getWidth() / 2 + b.getBoundary().getWidth() / 2) - dist; // примерно
                    if (overlap > 0) {
                        double angle = Math.atan2(dy, dx);
                        double moveX = Math.cos(angle) * overlap / 2;
                        double moveY = Math.sin(angle) * overlap / 2;
                        a.position.x -= moveX;
                        a.position.y -= moveY;
                        b.position.x += moveX;
                        b.position.y += moveY;

                        a.velocity.multiply(0.9);
                        b.velocity.multiply(0.9);
                    }
                }
            }
        }
    }

    private void generateMissions() {
        availableMissions.clear();
        availableMissions.add(new Mission(Mission.Type.KILL_SCOUTS, 3, 10, 1, 0));
        availableMissions.add(new Mission(Mission.Type.KILL_TANKS, 2, 15, 0, 1));
        availableMissions.add(new Mission(Mission.Type.COLLECT_LOOT, 5, 5, 0, 0));
        availableMissions.add(new Mission(Mission.Type.DESTROY_ASTEROIDS, 10, 8, 2, 0));
        String[] names = {"Kael'thalas", "Illidan", "Arthes-menetil", "Rexar", "Pandaren"};
        String bountyName = names[(int) (Math.random() * names.length)];
        availableMissions.add(new Mission(Mission.Type.BOUNTY, 1, GameConfig.BOUNTY_REWARD_SCRAP, GameConfig.BOUNTY_REWARD_CELLS, GameConfig.BOUNTY_REWARD_ALLOY, bountyName, GameConfig.BOUNTY_TIME_LIMIT));
    }

    private void spawnBountyTarget(Mission bountyMission) {
        Zone zone = getRandomZone();
        Vector pos = randomPointInZone(zone);
        Enemy bounty = new Enemy(Enemy.Type.TANK, Enemy.Behavior.GUARD, pos);
        bounty.setBounty(true);
        bounty.setHealth(GameConfig.TANK_HEALTH * 3);
        enemyList.add(bounty);
    }

    public void acceptMission(int index) {
        if (activeMission != null) return;
        if (index >= 0 && index < availableMissions.size()) {
            activeMission = availableMissions.get(index);
            if (activeMission.getType() == Mission.Type.BOUNTY) {
                spawnBountyTarget(activeMission);
            }
            availableMissions.remove(index);
        }
    }

    public Enemy getBountyTarget() {
        for (Enemy e : enemyList) {
            if (e.isBounty()) return e;
        }
        return null;
    }

    public void abortMission() {
        if (activeMission != null) {
            availableMissions.add(activeMission);
            activeMission = null;
        }
    }


    private void addMissionProgress(Mission.Type type, int amount) {
        if (activeMission != null && activeMission.getType() == type && !activeMission.isCompleted()) {
            activeMission.addProgress(amount);
            if (activeMission.isCompleted()) {
                // Награда
                scrap += activeMission.getRewardScrap();
                energyCells += activeMission.getRewardCells();
                alloy += activeMission.getRewardAlloy();
                activeMission = null;
                generateMissions(); // обновить список доступных
            }
        }
    }

    public boolean spendScrap(int amount) {
        if (scrap >= amount) {
            scrap -= amount;
            return true;
        }
        return false;
    }

    public boolean spendEnergyCells(int amount) {
        if (energyCells >= amount) {
            energyCells -= amount;
            return true;
        }
        return false;
    }

    public boolean spendAlloy(int amount) {
        if (alloy >= amount) {
            alloy -= amount;
            return true;
        }
        return false;
    }

    public void addScrap(int amount) {
        scrap += amount;
    }

    public void addEnergyCells(int amount) {
        energyCells += amount;
    }

    public void addAlloy(int amount) {
        alloy += amount;
    }

    private void updateLoot(double deltaTime) {
        Iterator<Loot> iter = lootList.iterator();
        while (iter.hasNext()) {
            Loot loot = iter.next();
            loot.update(deltaTime);
            if (loot.isExpired()) {
                iter.remove();
            }
        }
    }

    private void checkPlayerLootCollisions() {
        Iterator<Loot> iter = lootList.iterator();
        while (iter.hasNext()) {
            Loot loot = iter.next();
            if (spaceShip.overlaps(loot)) {
                iter.remove();
                addMissionProgress(Mission.Type.COLLECT_LOOT, 1);
                if (loot.getLootType() == Loot.Type.SCRAP) {
                    scrap += loot.getAmount();
                } else if (loot.getLootType() == Loot.Type.ENERGY_CELL) {
                    energyCells += loot.getAmount();
                } else if (loot.getLootType() == Loot.Type.ALLOY) {
                    alloy += loot.getAmount();
                }

            }
        }
    }

    public Spaceship getSpaceShip() {
        return spaceShip;
    }

    public List<Laser> getLaserList() {
        return laserList;
    }

    public List<Asteroid> getAsteroidList() {
        return asteroidList;
    }

    public List<Debris> getDebrisList() {
        return debrisList;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Enemy
    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    public List<Laser> getEnemyLaserList() {
        return enemyLaserList;
    }

    public int getScrap() {
        return scrap;
    }

    // loot
    public int getEnergyCells() {
        return energyCells;
    }

    public List<Loot> getLootList() {
        return lootList;
    }

    public int getAlloy() {
        return alloy;
    }

    public Factory getFactory() {
        return factory;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public List<Mission> getAvailableMissions() {
        return availableMissions;
    }

    public List<Planet> getPlanetList() {
        return planetList;
    }

}
