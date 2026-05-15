package org.com.gamep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.com.gamep.ScoreDatabase;

public class MainMenuView {
    private VBox root;
    private ScoreDatabase database;
    private Stage stage;

    public MainMenuView(ScoreDatabase database, Stage stage) {
        this.database = database;
        this.stage = stage;
        createMenu();
    }

    private void createMenu() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Label titleLabel = new Label("A S T E R O I D S");
        titleLabel.setFont(new Font("Arial Black", 60));
        titleLabel.setTextFill(Color.WHITE);

        int highScore = database.getHighScore();
        Label highScoreLabel = new Label("High Score: " + highScore);
        highScoreLabel.setFont(new Font("Arial", 28));
        highScoreLabel.setTextFill(Color.YELLOW);

        Button newGameButton = createButton("New Game");
        newGameButton.setOnAction(e -> startNewGame());

        Button leaderboardButton = createButton("Leaderboard");
        leaderboardButton.setOnAction(e -> showLeaderboard());

        Button settingsButton = createButton("Settings");
        settingsButton.setOnAction(e -> showSettings());

        Button exitButton = createButton("Exit");
        exitButton.setOnAction(e -> {
            database.close();
            System.exit(0);
        });

        root.getChildren().addAll(titleLabel, highScoreLabel, newGameButton,
                leaderboardButton, settingsButton, exitButton);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFont(new Font("Arial", 24));
        button.setPrefWidth(200);
        return button;
    }

    private void startNewGame() {
        GameView gameView = new GameView(database, stage, this);
        stage.getScene().setRoot(gameView.getRoot());
    }

    private void showLeaderboard() {
        LeaderboardView leaderboardView = new LeaderboardView(database, this);
        stage.getScene().setRoot(leaderboardView.getRoot());
    }

    private void showSettings() {
        SettingsView settingsView = new SettingsView(this);
        stage.getScene().setRoot(settingsView.getRoot());
    }

    public void show() {
        // Refresh high score when returning to menu
        createMenu();
        stage.getScene().setRoot(root);
    }

    public VBox getRoot() {
        return root;
    }
}