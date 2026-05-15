package org.com.gamep;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDatabase {
    private static final String DB_URL = "jdbc:sqlite:asteroids_scores.db";
    private Connection connection;

    public ScoreDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DB_URL);

            createTable();

        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error!");
            e.printStackTrace();
        }
    }

    private void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT NOT NULL,
                score INTEGER NOT NULL,
                date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error creating table!");
            e.printStackTrace();
        }
    }

    public void addScore(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty() || score < 0) {
            throw new IllegalArgumentException("Invalid player name or score");
        }

        String insertSQL = "INSERT INTO scores (player_name, score) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, playerName.trim());
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding score!");
            e.printStackTrace();
        }
    }

    public List<ScoreEntry> getTopScores(int limit) {
        List<ScoreEntry> scores = new ArrayList<>();
        String querySQL = "SELECT player_name, score, date FROM scores ORDER BY score DESC LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String playerName = rs.getString("player_name");
                int score = rs.getInt("score");
                String date = rs.getString("date");
                scores.add(new ScoreEntry(playerName, score, date));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving scores!");
            e.printStackTrace();
        }

        return scores;
    }

    public int getHighScore() {
        String querySQL = "SELECT MAX(score) as max_score FROM scores";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            if (rs.next()) {
                return rs.getInt("max_score");
            }
        } catch (SQLException e) {
            System.err.println("Error getting high score!");
            e.printStackTrace();
        }

        return 0;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database!");
            e.printStackTrace();
        }
    }

    // Inner class for score entries
    public static class ScoreEntry {
        private String playerName;
        private int score;
        private String date;

        public ScoreEntry(String playerName, int score, String date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public String getDate() {
            return date;
        }
    }
}
