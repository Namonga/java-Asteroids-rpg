package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.com.gamep.game.GameEngine;

public class FactoryView {
    private VBox root;
    private GameEngine engine;
    private Runnable onClose;
    private Label scrapLabel, cellLabel, alloyLabel, hpLabel;

    public FactoryView(GameEngine engine, Runnable onClose) {
        this.engine = engine;
        this.onClose = onClose;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(12);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-padding: 25; -fx-border-color: #888888; -fx-border-width: 3;");
        root.setPrefSize(500, 500);
        root.setMaxSize(500, 500);

        Label title = new Label("SPACE FACTORY");
        title.setFont(Font.font("Arial Black", FontWeight.BOLD, 32));
        title.setTextFill(Color.LIGHTGRAY);

        // Ресурсы игрока
        HBox resBar = new HBox(15);
        resBar.setAlignment(Pos.CENTER);
        scrapLabel = resLabel("Scrap: " + engine.getScrap(), Color.WHITE);
        cellLabel  = resLabel("Cells: " + engine.getEnergyCells(), Color.CYAN);
        alloyLabel = resLabel("Alloy: " + engine.getAlloy(), Color.LIGHTGRAY);
        hpLabel    = resLabel("HP: " + engine.getSpaceShip().getHealth() + "/" + engine.getSpaceShip().getMaxHealth(), Color.LIMEGREEN);
        resBar.getChildren().addAll(scrapLabel, cellLabel, alloyLabel, hpLabel);

        Label upgradeHeader = new Label("UPGRADES");
        upgradeHeader.setFont(Font.font("Arial Black", 24));
        upgradeHeader.setTextFill(Color.SILVER);

        VBox upgradeList = new VBox(8);
        upgradeList.setAlignment(Pos.CENTER);
        upgradeList.getChildren().addAll(
                upgradeRow("Repair Full HP", 5, 0, 0, () -> engine.getSpaceShip().repairFull()),
                upgradeRow("+1 Max HP", 10, 1, 0, () -> engine.getSpaceShip().increaseMaxHealth(1)),
                upgradeRow("+25 Magnet Radius", 10, 0, 1, () -> engine.getSpaceShip().increaseMagnetRadius(25)),
                upgradeRow("+10% Speed", 8, 0, 1, () -> engine.getSpaceShip().increaseSpeed(0.1)),
                upgradeRow("+1 Laser Damage", 10, 2, 0, () -> engine.getSpaceShip().increaseLaserDamage(1)),
                upgradeRow("-0.05 Shoot Delay", 12, 0, 2, () -> engine.getSpaceShip().decreaseShootCooldown(0.05))
        );

        Button leaveBtn = new Button("Leave Bay");
        leaveBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        leaveBtn.setTextFill(Color.WHITE);
        leaveBtn.setStyle("-fx-background-color: #555555; -fx-padding: 6 20;");
        leaveBtn.setOnAction(e -> onClose.run());

        root.getChildren().addAll(title, resBar, upgradeHeader, upgradeList, leaveBtn);
    }

    private Label resLabel(String text, Color color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        l.setTextFill(color);
        return l;
    }

    private HBox upgradeRow(String name, int scrapCost, int cellCost, int alloyCost, Runnable action) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        String costText = scrapCost + " scr";
        if (cellCost > 0) costText += " + " + cellCost + " cell";
        if (alloyCost > 0) costText += " + " + alloyCost + " alloy";

        Label desc = new Label(name);
        desc.setFont(Font.font("Arial", 18));
        desc.setTextFill(Color.WHITE);

        Label price = new Label(costText);
        price.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        price.setTextFill(Color.LIMEGREEN);

        Button btn = new Button("Upgrade");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 4 12;");
        btn.setOnAction(e -> {
            if (engine.getScrap() >= scrapCost && engine.getEnergyCells() >= cellCost && engine.getAlloy() >= alloyCost) {
                engine.spendScrap(scrapCost);
                if (cellCost > 0) engine.spendEnergyCells(cellCost);
                if (alloyCost > 0) engine.spendAlloy(alloyCost);
                action.run();
                updateResLabels();
            }
        });

        row.getChildren().addAll(desc, price, btn);
        return row;
    }

    private void updateResLabels() {
        scrapLabel.setText("Scrap: " + engine.getScrap());
        cellLabel.setText("Cells: " + engine.getEnergyCells());
        alloyLabel.setText("Alloy: " + engine.getAlloy());
        hpLabel.setText("HP: " + engine.getSpaceShip().getHealth() + "/" + engine.getSpaceShip().getMaxHealth());
    }

    public VBox getRoot() { return root; }
}