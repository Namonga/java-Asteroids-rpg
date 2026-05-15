package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.com.gamep.GameConfig;

public class SettingsView {
    private VBox root;
    private MainMenuView mainMenu;
    private boolean isDifficultMode = false;

    public SettingsView(MainMenuView mainMenu) {
        this.mainMenu = mainMenu;
        createSettings();
    }

    private void createSettings() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Label titleLabel = new Label("Settings");
        titleLabel.setFont(new Font("Arial Black", 48));
        titleLabel.setTextFill(Color.WHITE);

        HBox difficultyBox = createDifficultyBox();

        Button backButton = new Button("Back to Menu");
        backButton.setFont(new Font("Arial", 24));
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> {
            applySettings();
            mainMenu.show();
        });

        root.getChildren().addAll(titleLabel, difficultyBox, backButton);
    }

    private HBox createDifficultyBox() {
        HBox difficultyBox = new HBox(10);
        difficultyBox.setAlignment(Pos.CENTER);

        Label difficultyLabel = new Label("Difficulty:");
        difficultyLabel.setFont(new Font("Arial", 24));
        difficultyLabel.setTextFill(Color.WHITE);

        ToggleGroup difficultyGroup = new ToggleGroup();

        RadioButton easyButton = new RadioButton("Easy");
        easyButton.setFont(new Font("Arial", 20));
        easyButton.setTextFill(Color.WHITE);
        easyButton.setToggleGroup(difficultyGroup);
        easyButton.setSelected(!isDifficultMode);

        RadioButton hardButton = new RadioButton("Hard");
        hardButton.setFont(new Font("Arial", 20));
        hardButton.setTextFill(Color.WHITE);
        hardButton.setToggleGroup(difficultyGroup);
        hardButton.setSelected(isDifficultMode);

        difficultyGroup.selectedToggleProperty().addListener((obs, old, newToggle) -> {
            isDifficultMode = (newToggle == hardButton);
        });

        difficultyBox.getChildren().addAll(difficultyLabel, easyButton, hardButton);
        return difficultyBox;
    }

    private void applySettings() {
        if (isDifficultMode) {
            GameConfig.setHardMode();
        } else {
            GameConfig.setEasyMode();
        }
    }

    public VBox getRoot() {
        return root;
    }

    public boolean isDifficultMode() {
        return isDifficultMode;
    }
}