package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.com.gamep.game.GameEngine;

public class ShopView {
    private VBox root;
    private GameEngine engine;
    private Runnable onClose;

    private Label scrapLabel;
    private Label cellLabel;
    private Label alloyLabel;
    private Label hpLabel;

    public ShopView(GameEngine engine, Runnable onClose) {
        this.engine = engine;
        this.onClose = onClose;
        createShop();
    }

    private void createShop() {
        root = new VBox(12);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-padding: 25; -fx-border-color: #888888; -fx-border-width: 3;");
        root.setPrefSize(500, 600);
        root.setMaxSize(500, 600);

        Label title = new Label("STATION TRADE");
        title.setFont(Font.font("Arial Black", FontWeight.BOLD, 34));
        title.setTextFill(Color.LIGHTGRAY);

        HBox resBar = new HBox(15);
        resBar.setAlignment(Pos.CENTER);
        scrapLabel = resLabel("Scrap: " + engine.getScrap(), Color.WHITE);
        cellLabel  = resLabel("Cells: " + engine.getEnergyCells(), Color.CYAN);
        alloyLabel = resLabel("Alloy: " + engine.getAlloy(), Color.LIGHTGRAY);
        hpLabel    = resLabel("HP: " + engine.getSpaceShip().getHealth() + "/" + engine.getSpaceShip().getMaxHealth(), Color.LIMEGREEN);
        resBar.getChildren().addAll(scrapLabel, cellLabel, alloyLabel, hpLabel);

        Label buyHeader = new Label("BUY");
        buyHeader.setFont(Font.font("Arial Black", 24));
        buyHeader.setTextFill(Color.SILVER);

        VBox buyList = new VBox(8);
        buyList.setAlignment(Pos.CENTER);
        buyList.getChildren().addAll(
                tradeRow("Scrap", "new/scrap_metal.png", 5, 3, false),
                tradeRow("Energy Cell", "new/energy_cell.png", 1, 8, false),
                tradeRow("Alloy", "new/alloy.png", 1, 20, false)
        );

        Label sellHeader = new Label("SELL");
        sellHeader.setFont(Font.font("Arial Black", 24));
        sellHeader.setTextFill(Color.SILVER);

        VBox sellList = new VBox(8);
        sellList.setAlignment(Pos.CENTER);
        sellList.getChildren().addAll(
                tradeRow("Scrap", "new/scrap_metal.png", 5, 2, true),
                tradeRow("Energy Cell", "new/energy_cell.png", 1, 5, true),
                tradeRow("Alloy", "new/alloy.png", 1, 15, true)
        );

        Button leaveBtn = new Button("Leave Station");
        leaveBtn.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        leaveBtn.setTextFill(Color.WHITE);
        leaveBtn.setStyle("-fx-background-color: #555555; -fx-padding: 6 20;");
        leaveBtn.setOnAction(e -> onClose.run());

        root.getChildren().addAll(title, resBar, buyHeader, buyList, sellHeader, sellList, leaveBtn);
    }

    private Label resLabel(String text, Color color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        l.setTextFill(color);
        return l;
    }

    private HBox tradeRow(String name, String imgPath, int amount, int cost, boolean sellMode) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView icon = new ImageView(new Image(imgPath, 28, 28, true, false));
        Label desc = new Label((sellMode ? "Sell " : "Buy ") + amount + " " + name);
        desc.setFont(Font.font("Arial", 18));
        desc.setTextFill(Color.WHITE);

        Label price = new Label("for " + cost + " scrap");
        price.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        price.setTextFill(sellMode ? Color.LIMEGREEN : Color.LIGHTCORAL);

        Button btn = new Button(sellMode ? "Sell" : "Buy");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 4 12;");
        btn.setOnAction(e -> {
            if (sellMode) {
                if (trySpendResource(name, amount)) {
                    engine.addScrap(cost);
                    updateResLabels();
                }
            } else {
                if (engine.spendScrap(cost)) {
                    addResource(name, amount);
                    updateResLabels();
                }
            }
        });

        row.getChildren().addAll(icon, desc, price, btn);
        return row;
    }

    private boolean trySpendResource(String name, int amount) {
        switch (name) {
            case "Scrap": return engine.spendScrap(amount);
            case "Energy Cell": return engine.spendEnergyCells(amount);
            case "Alloy": return engine.spendAlloy(amount);
        }
        return false;
    }

    private void addResource(String name, int amount) {
        switch (name) {
            case "Scrap": engine.addScrap(amount); break;
            case "Energy Cell": engine.addEnergyCells(amount); break;
            case "Alloy": engine.addAlloy(amount); break;
        }
    }

    private void updateResLabels() {
        scrapLabel.setText("Scrap: " + engine.getScrap());
        cellLabel.setText("Cells: " + engine.getEnergyCells());
        alloyLabel.setText("Alloy: " + engine.getAlloy());
        hpLabel.setText("HP: " + engine.getSpaceShip().getHealth() + "/" + engine.getSpaceShip().getMaxHealth());
    }

    public VBox getRoot() { return root; }
}