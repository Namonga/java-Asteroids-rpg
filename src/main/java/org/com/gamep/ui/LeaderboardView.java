package org.com.gamep.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.com.gamep.ScoreDatabase;
import java.util.List;

public class LeaderboardView {
    private VBox root;
    private ScoreDatabase database;
    private MainMenuView mainMenu;

    public LeaderboardView(ScoreDatabase database, MainMenuView mainMenu) {
        this.database = database;
        this.mainMenu = mainMenu;
        createLeaderboard();
    }

    private void createLeaderboard() {
        root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: black;");
        root.setPadding(new Insets(30));

        Label titleLabel = new Label("L E A D E R B O A R D");
        titleLabel.setFont(new Font("Arial Black", 48));
        titleLabel.setTextFill(Color.YELLOW);

        HBox headerBox = createHeaderBox();
        VBox scoresListBox = createScoresList();

        ScrollPane scrollPane = new ScrollPane(scoresListBox);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: black;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Button backButton = new Button("Back to Menu");
        backButton.setFont(new Font("Arial", 24));
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> mainMenu.show());

        root.getChildren().addAll(titleLabel, headerBox, scrollPane, backButton);
    }

    private HBox createHeaderBox() {
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));

        headerBox.getChildren().addAll(
                createHeaderLabel("Rank", 100),
                createHeaderLabel("Player", 300),
                createHeaderLabel("Score", 150),
                createHeaderLabel("Date", 200)
        );

        return headerBox;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(new Font("Arial Black", 24));
        label.setTextFill(Color.WHITE);
        label.setPrefWidth(width);
        return label;
    }

    private VBox createScoresList() {
        VBox scoresListBox = new VBox(10);
        scoresListBox.setAlignment(Pos.CENTER);

        List<ScoreDatabase.ScoreEntry> topScores = database.getTopScores(10);

        if (topScores.isEmpty()) {
            Label noScoresLabel = new Label("No scores yet. Be the first!");
            noScoresLabel.setFont(new Font("Arial", 24));
            noScoresLabel.setTextFill(Color.GRAY);
            scoresListBox.getChildren().add(noScoresLabel);
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                scoresListBox.getChildren().add(createScoreRow(topScores.get(i), i));
            }
        }

        return scoresListBox;
    }

    private HBox createScoreRow(ScoreDatabase.ScoreEntry entry, int rank) {
        HBox scoreRow = new HBox(20);
        scoreRow.setAlignment(Pos.CENTER);

        Color textColor = getColorForRank(rank);

        scoreRow.getChildren().addAll(
                createScoreLabel(String.valueOf(rank + 1), 100, textColor),
                createScoreLabel(entry.getPlayerName(), 300, textColor),
                createScoreLabel(String.valueOf(entry.getScore()), 150, textColor),
                createScoreLabel(entry.getDate().substring(0, 10), 200, textColor)
        );

        return scoreRow;
    }

    private Label createScoreLabel(String text, double width, Color color) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 20));
        label.setTextFill(color);
        label.setPrefWidth(width);
        return label;
    }

    private Color getColorForRank(int rank) {
        if (rank == 0) return Color.GOLD;
        if (rank == 1) return Color.SILVER;
        if (rank == 2) return Color.rgb(205, 127, 50); // Bronze
        return Color.WHITE;
    }

    public VBox getRoot() {
        return root;
    }
}