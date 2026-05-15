package org.com.gamep.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.com.gamep.Camera;
import org.com.gamep.GameConfig;
import org.com.gamep.ScoreDatabase;
import org.com.gamep.Vector;
import org.com.gamep.game.Mission;
import org.com.gamep.game.Zone;
import org.com.gamep.game.GameEngine;
import org.com.gamep.game.InputHandler;
import org.com.gamep.sprites.Asteroid;
import org.com.gamep.sprites.Debris;
import org.com.gamep.sprites.Enemy;
import org.com.gamep.sprites.Laser;
import org.com.gamep.sprites.Loot;
import org.com.gamep.sprites.Planet;
import org.com.gamep.sprites.Sprite;
import javafx.scene.control.Button;

public class GameView {
    private StackPane root;
    private Canvas canvas;
    private GraphicsContext context;
    private GameEngine gameEngine;
    private InputHandler inputHandler;
    private AnimationTimer gameLoop;
    private ScoreDatabase database;
    private Stage stage;
    private MainMenuView mainMenu;
    private VBox stationMenu;
    private MissionBoardView missionBoardView;

    private Camera camera;

    private boolean paused = false;
    private ShopView shopView;
    private FactoryView factoryView;

    private boolean showDebug = false;
    private boolean showZones = false;
    private boolean showMissionInfo = false;

    private double[][] stars;

    private StatsView statsView;

    public GameView(ScoreDatabase database, Stage stage, MainMenuView mainMenu) {
        this.database = database;
        this.stage = stage;
        this.mainMenu = mainMenu;

        canvas = new Canvas(GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        context = canvas.getGraphicsContext2D();
        root = new StackPane(canvas);

        gameEngine = new GameEngine();
        generateStars(1500);
        camera = new Camera();
        inputHandler = new InputHandler();

        inputHandler.setupInputHandlers(stage.getScene());
        startGameLoop();
    }

    private void generateStars(int count) {
        stars = new double[count][2];
        for (int i = 0; i < count; i++) {
            stars[i][0] = Math.random() * GameConfig.WORLD_WIDTH;
            stars[i][1] = Math.random() * GameConfig.WORLD_HEIGHT;
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long nanoTime) {
                if (paused) {
                    if (inputHandler.isKeyJustPressed("E") || inputHandler.isKeyJustPressed("ESCAPE") ||
                            inputHandler.isKeyJustPressed("F4")) {
                        if (shopView != null) closeShop();
                        else if (factoryView != null) closeFactory();
                        else if (statsView != null) closeStats();
                    }
                    inputHandler.clearJustPressed();
                    return;
                }

                if (!gameEngine.isGameOver()) {
                    if (inputHandler.isKeyPressed("EQUALS") || inputHandler.isKeyPressed("ADD")) {
                        camera.zoomIn();
                    }
                    if (inputHandler.isKeyPressed("MINUS") || inputHandler.isKeyPressed("SUBTRACT")) {
                        camera.zoomOut();
                    }
                    if (inputHandler.isKeyJustPressed("DIGIT0") || inputHandler.isKeyJustPressed("0")) {
                        camera.resetZoom();
                    }
                    if (inputHandler.isKeyJustPressed("F3")) {
                        Sprite.DEBUG_HITBOX = !Sprite.DEBUG_HITBOX;
                    }
                    if (inputHandler.isKeyJustPressed("F1")) {
                        showDebug = !showDebug;
                    }
                    if (inputHandler.isKeyJustPressed("F2")) {
                        showZones = !showZones;
                    }
                    if (inputHandler.isKeyJustPressed("TAB")) {
                        showMissionInfo = !showMissionInfo;
                    }
                    if (inputHandler.isKeyJustPressed("F4")) {
                        paused = true;
                        statsView = new StatsView(gameEngine, gameEngine.getSpaceShip(), GameView.this::closeStats);
                        root.getChildren().add(statsView.getRoot());
                    }

                    if (inputHandler.isKeyJustPressed("E")) {
                        double px = gameEngine.getSpaceShip().position.x;
                        double py = gameEngine.getSpaceShip().position.y;
                        double distStation = Math.hypot(px - gameEngine.getStation().position.x, py - gameEngine.getStation().position.y);
                        double distFactory = Math.hypot(px - gameEngine.getFactory().position.x, py - gameEngine.getFactory().position.y);

                        if (distStation < 150 && distStation <= distFactory) {
                            paused = true;
                            showStationMenu();
                        }
                        else if (distFactory < 150 && distFactory < distStation) {
                            paused = true;
                            factoryView = new FactoryView(gameEngine, GameView.this::closeFactory);
                            root.getChildren().add(factoryView.getRoot());
                        }
                    }

                    gameEngine.processInput(inputHandler);
                    inputHandler.clearJustPressed();

                    gameEngine.update(1 / 60.0);
                    camera.update(gameEngine.getSpaceShip());
                    render();
                } else {
                    stop();
                    showGameOver();
                }
            }
        };
        gameLoop.start();
    }

    private void render() {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        context.setImageSmoothing(true);
        context.save();

        double zoom = camera.getZoom();
        context.translate(GameConfig.CANVAS_WIDTH / 2.0, GameConfig.CANVAS_HEIGHT / 2.0);
        context.scale(zoom, zoom);
        context.translate(-GameConfig.CANVAS_WIDTH / 2.0, -GameConfig.CANVAS_HEIGHT / 2.0);

        context.translate(-camera.getX(), -camera.getY());
        context.setFill(Color.WHITE);
        for (double[] star : stars) {
            context.fillOval(star[0], star[1], 5, 5);
        }

        context.save();
        context.setGlobalAlpha(0.6);
        for (Planet p : gameEngine.getPlanetList()) {
            p.render(context);
        }
        context.restore();

        gameEngine.getSpaceShip().render(context);
        for (Laser l : gameEngine.getLaserList()) l.render(context);
        for (Asteroid a : gameEngine.getAsteroidList()) a.render(context);
        for (Debris d : gameEngine.getDebrisList()) d.render(context);
        gameEngine.getStation().render(context);
        gameEngine.getFactory().render(context);
        for (Enemy e : gameEngine.getEnemyList()) e.render(context);

        if (gameEngine.getActiveMission() != null && gameEngine.getActiveMission().getType() == Mission.Type.BOUNTY) {
            for (Enemy e : gameEngine.getEnemyList()) {
                if (e.isBounty()) {
                    context.setFill(Color.GOLD);
                    context.setFont(Font.font("Arial Black", 14));
                    String name = gameEngine.getActiveMission().getBountyName();
                    context.fillText(name, e.position.x - 20, e.position.y + e.getBoundary().getHeight()/2 + 18);
                }
            }
        }

        for (Laser el : gameEngine.getEnemyLaserList()) el.render(context);
        for (Loot loot : gameEngine.getLootList()) loot.render(context);

        context.restore();

        renderUI();
    }



    private void renderUI() {
        context.setFill(Color.LIGHTGRAY);
        context.setFont(Font.font("Arial Black", 20));
        context.setLineWidth(1);

        String hpText = "HP: " + gameEngine.getSpaceShip().getHealth() + "/" + gameEngine.getSpaceShip().getMaxHealth();
        context.setFill(Color.LIMEGREEN);
        context.fillText(hpText, 20, 50);

        String scrapText = "Scrap: " + gameEngine.getScrap();
        context.setFill(Color.LIGHTGRAY);
        context.fillText(scrapText, 20, 80);

        int y = 120;

        if (showMissionInfo) {
            Mission activeMission = gameEngine.getActiveMission();
            if (activeMission != null) {
                String missionText = activeMission.getDescription() + " [" + activeMission.getCurrentProgress() + "/" + activeMission.getTargetAmount() + "]";
                if (activeMission.getType() == Mission.Type.BOUNTY) {
                    missionText += " Time: " + (int)activeMission.getTimeLimit() + "s";
                }
                context.setFill(Color.SILVER);
                context.fillText(missionText, 20, y);
                y += 25;

                if (activeMission.getType() == Mission.Type.BOUNTY) {
                    Enemy target = gameEngine.getBountyTarget();
                    if (target != null) {
                        double tx = target.position.x;
                        double ty = target.position.y;
                        double dist = Math.hypot(tx - gameEngine.getSpaceShip().position.x, ty - gameEngine.getSpaceShip().position.y);
                        String targetInfo = String.format("Target: %.0f, %.0f (%.0f px)", tx, ty, dist);
                        context.setFill(Color.LIGHTGOLDENRODYELLOW);
                        context.fillText(targetInfo, 20, y);
                        y += 25;
                    } else {
                        context.setFill(Color.GRAY);
                        context.fillText("Target lost", 20, y);
                        y += 25;
                    }
                }
            } else {
                context.setFill(Color.DARKGRAY);
                context.fillText("No active mission", 20, y);
                y += 25;
            }
        }

        if (showZones) {
            context.setFont(Font.font("Arial Black", 14));
            context.setFill(Color.GRAY);
            context.fillText("=== ZONES ===", 20, y);
            y += 20;
            for (Zone z : GameConfig.ZONES) {
                String info = z.getName() + " (" + (int)z.getCenter().x + "," + (int)z.getCenter().y + ") r=" + (int)z.getRadius();
                context.fillText(info, 20, y);
                y += 18;
            }
            context.setFont(Font.font("Arial Black", 20)); // восстанавливаем размер
        }

        if (showDebug) {
            context.setFont(Font.font("Arial Black", 16));
            double px = gameEngine.getSpaceShip().position.x;
            double py = gameEngine.getSpaceShip().position.y;
            String coordText = String.format("X: %.0f  Y: %.0f", px, py);
            context.setFill(Color.LIGHTGRAY);
            context.fillText(coordText, 20, y); y += 20;
            context.fillText("Zoom: " + camera.getZoom(), 20, y); y += 20;
            double distStation = Math.hypot(px - gameEngine.getStation().position.x, py - gameEngine.getStation().position.y);
            context.fillText("To Shop: " + (int)distStation + " px", 20, y); y += 20;
            String zoneText = "Zone: " + getCurrentZoneName();
            context.fillText(zoneText, 20, y); y += 20;
        }
    }


  private void showGameOver() {
      GameOverView gameOverView = new GameOverView(database, mainMenu, gameEngine.getScrap(), root);
      stage.getScene().setRoot(gameOverView.getRoot());
  }

    private void closeShop() {
        if (shopView != null) {
            root.getChildren().remove(shopView.getRoot());
            shopView = null;
        }
        paused = false;
        inputHandler.clearJustPressed(); // чтобы повторное нажатие E сразу не открыло заново
    }

    private void closeFactory() {
        if (factoryView != null) {
            root.getChildren().remove(factoryView.getRoot());
            factoryView = null;
        }
        paused = false;
        inputHandler.clearJustPressed();
    }

    private void closeStats() {
        if (statsView != null) {
            root.getChildren().remove(statsView.getRoot());
            statsView = null;
        }
        paused = false;
        inputHandler.clearJustPressed();
    }

    private void showStationMenu() {
        VBox menu = new VBox(15);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: rgba(0,0,0,0.95); -fx-padding: 30;");
        Button shopBtn = new Button("Shop");
        shopBtn.setFont(Font.font("Arial", 24));
        shopBtn.setOnAction(e -> {
            closeStationMenu();
            shopView = new ShopView(gameEngine, GameView.this::closeShop);
            root.getChildren().add(shopView.getRoot());
            paused = true;
        });
        Button missionBtn = new Button("Missions");
        missionBtn.setFont(Font.font("Arial", 24));
        missionBtn.setOnAction(e -> {
            closeStationMenu();          // убрали меню станции
            showMissionBoard();          // открыли доску (пауза включится внутри)
        });
        Button closeBtn = new Button("Cancel");
        closeBtn.setFont(Font.font("Arial", 24));
        closeBtn.setOnAction(e -> closeStationMenu());

        menu.getChildren().addAll(shopBtn, missionBtn, closeBtn);
        root.getChildren().add(menu);
        stationMenu = menu;
        paused = true;
    }

    private void closeStationMenu() {
        if (stationMenu != null) {
            root.getChildren().remove(stationMenu);
            stationMenu = null;
            paused = false;
            inputHandler.clearJustPressed();
        }
    }

    private void showMissionBoard() {
        if (missionBoardView != null) return;
        paused = true;
        missionBoardView = new MissionBoardView(gameEngine, GameView.this::closeMissionBoard);
        root.getChildren().add(missionBoardView.getRoot());
    }

    private void closeMissionBoard() {
        if (missionBoardView != null) {
            root.getChildren().remove(missionBoardView.getRoot());
            missionBoardView = null;
        }
        paused = false;
        inputHandler.clearJustPressed();
    }

    public StackPane getRoot() {
        return root;
    }

    private String getCurrentZoneName() {
        double px = gameEngine.getSpaceShip().position.x;
        double py = gameEngine.getSpaceShip().position.y;
        Vector pos = new Vector(px, py);
        if (GameConfig.SAFE_ZONE.contains(pos)) return GameConfig.SAFE_ZONE.getName();
        if (GameConfig.MINING_BELT_1.contains(pos)) return GameConfig.MINING_BELT_1.getName();
        if (GameConfig.MINING_BELT_2.contains(pos)) return GameConfig.MINING_BELT_2.getName();
        if (GameConfig.PIRATE_OUTPOST.contains(pos)) return GameConfig.PIRATE_OUTPOST.getName();
        if (GameConfig.ABANDONED_STATION.contains(pos)) return GameConfig.ABANDONED_STATION.getName();
        return "Deep Space";
    }
}