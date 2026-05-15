package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.com.gamep.game.GameEngine;
import org.com.gamep.sprites.Spaceship;

public class StatsView {
    private VBox root;
    private Runnable onClose;

    public StatsView(GameEngine engine, Spaceship ship, Runnable onClose) {
        this.onClose = onClose;
        buildUI(engine, ship);
    }

    private void buildUI(GameEngine engine, Spaceship ship) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-padding: 30; -fx-border-color: #888888; -fx-border-width: 3;");
        root.setPrefSize(520, 500);

        Label title = new Label("SHIP STATUS");
        title.setFont(Font.font("Arial Black", 28));
        title.setTextFill(Color.LIGHTGRAY);

        HBox mainRow = new HBox(20);
        mainRow.setAlignment(Pos.CENTER);

        ImageView shipImage = new ImageView(new Image("spaceship_32x23.png", 64, 46, true, true));

        VBox stats = new VBox(10);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                statLabel("Health: " + ship.getHealth() + "/" + ship.getMaxHealth()),
                statLabel("Magnet Radius: " + String.format("%.0f", ship.getMagnetRadius()) + " px"),
                statLabel("Laser Damage: " + ship.getLaserDamage()),
                statLabel("Shoot Cooldown: " + String.format("%.2f", ship.getShootCooldown()) + "s"),
                statLabel("Speed Multiplier: " + String.format("%.1f", ship.getSpeedMultiplier() * 100) + "%")
        );

        VBox resources = new VBox(10);
        resources.setAlignment(Pos.CENTER_LEFT);
        Label resTitle = new Label("Resources");
        resTitle.setFont(Font.font("Arial Black", 20));
        resTitle.setTextFill(Color.LIGHTGRAY);
        resources.getChildren().add(resTitle);

        resources.getChildren().add(createResourceRow("new/scrap_metal.png", "Scrap: " + engine.getScrap(), Color.WHITE));
        resources.getChildren().add(createResourceRow("new/energy_cell.png", "Energy Cells: " + engine.getEnergyCells(), Color.WHITE));
        resources.getChildren().add(createResourceRow("new/alloy.png", "Alloy: " + engine.getAlloy(), Color.LIGHTGRAY));

        VBox leftColumn = new VBox(15);
        leftColumn.getChildren().addAll(stats, resources);

        mainRow.getChildren().addAll(shipImage, leftColumn);

        Button closeBtn = new Button("Close");
        closeBtn.setFont(Font.font("Arial", 20));
        closeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
        closeBtn.setOnAction(e -> onClose.run());

        root.getChildren().addAll(title, mainRow, closeBtn);
    }

    private HBox createResourceRow(String imgPath, String text, Color color) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        ImageView icon = new ImageView(new Image(imgPath, 24, 24, true, false));
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 18));
        lbl.setTextFill(color);
        row.getChildren().addAll(icon, lbl);
        return row;
    }

    private Label statLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 18));
        l.setTextFill(Color.WHITE);
        return l;
    }

    public VBox getRoot() { return root; }
}