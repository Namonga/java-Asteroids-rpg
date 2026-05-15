package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.com.gamep.ScoreDatabase;

public class GameOverView {
    private StackPane root;
    private ScoreDatabase database;
    private MainMenuView mainMenu;
    private int finalScore;

    public GameOverView(ScoreDatabase database, MainMenuView mainMenu, int finalScore, StackPane canvasPane) {
        this.database = database;
        this.mainMenu = mainMenu;
        this.finalScore = finalScore;
        createGameOverScreen(canvasPane);
    }

    private void createGameOverScreen(StackPane canvasPane) {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 50;");

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(new Font("Arial Black", 72));
        gameOverLabel.setTextFill(Color.RED);

        Label scoreLabel = new Label("Final Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", 36));
        scoreLabel.setTextFill(Color.WHITE);

        int highScore = database.getHighScore();
        if (finalScore > highScore && finalScore > 0) {
            Label newHighScoreLabel = new Label("🎉 NEW HIGH SCORE! 🎉");
            newHighScoreLabel.setFont(new Font("Arial Black", 32));
            newHighScoreLabel.setTextFill(Color.GOLD);
            gameOverBox.getChildren().add(newHighScoreLabel);
        }

        Label nameLabel = new Label("Enter your name:");
        nameLabel.setFont(new Font("Arial", 24));
        nameLabel.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setFont(new Font("Arial", 20));
        nameField.setMaxWidth(300);
        nameField.setPromptText("Player Name");

        HBox buttonBox = createButtonBox(nameField);

        gameOverBox.getChildren().addAll(gameOverLabel, scoreLabel, nameLabel, nameField, buttonBox);

        root = new StackPane(canvasPane, gameOverBox);
    }

    private HBox createButtonBox(TextField nameField) {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save Result");
        saveButton.setFont(new Font("Arial", 20));
        saveButton.setPrefWidth(150);
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> {
            String playerName = nameField.getText().trim();
            database.addScore(playerName, finalScore);
            mainMenu.show();
        });

        Button dontSaveButton = new Button("Don't Save");
        dontSaveButton.setFont(new Font("Arial", 20));
        dontSaveButton.setPrefWidth(150);
        dontSaveButton.setOnAction(e -> mainMenu.show());

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        buttonBox.getChildren().addAll(saveButton, dontSaveButton);
        return buttonBox;
    }

    public StackPane getRoot() {
        return root;
    }
}