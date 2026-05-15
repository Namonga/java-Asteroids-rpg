package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.com.gamep.game.GameEngine;
import org.com.gamep.game.Mission;

import java.util.List;

public class MissionBoardView {
    private VBox root;
    private GameEngine engine;
    private Runnable onClose;

    public MissionBoardView(GameEngine engine, Runnable onClose) {
        this.engine = engine;
        this.onClose = onClose;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-padding: 25; -fx-border-color: #888888; -fx-border-width: 3;");
        root.setPrefSize(500, 400);

        Label title = new Label("MISSION BOARD");
        title.setFont(Font.font("Arial Black", 28));
        title.setTextFill(Color.LIGHTGRAY);

        Mission active = engine.getActiveMission();
        if (active != null) {
            Label info = new Label("Active mission: " + active.getDescription());
            info.setFont(Font.font("Arial", 16));
            info.setTextFill(Color.LIGHTGRAY);
            root.getChildren().add(info);
        }

        List<Mission> missions = engine.getAvailableMissions();
        VBox list = new VBox(10);
        list.setAlignment(Pos.CENTER);
        for (int i = 0; i < missions.size(); i++) {
            Mission m = missions.get(i);
            Button btn = new Button(m.getDescription() + " (" + m.getRewardScrap() + " scr)");
            btn.setFont(Font.font("Arial", 18));
            btn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
            final int idx = i;
            btn.setOnAction(e -> {
                engine.acceptMission(idx);
                onClose.run();
            });
            list.getChildren().add(btn);
        }

        Button closeBtn = new Button("Close");
        closeBtn.setFont(Font.font("Arial", 18));
        closeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
        closeBtn.setOnAction(e -> onClose.run());

        root.getChildren().addAll(title, list, closeBtn);
    }

    public VBox getRoot() { return root; }
}