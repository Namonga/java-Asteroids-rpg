package org.com.gamep;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.com.gamep.ui.MainMenuView;

public class AsteroidsApp extends Application {

    public static final String WINDOW_TITLE = "A S T E R O I D S";
    private ScoreDatabase database;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setResizable(false);

        database = new ScoreDatabase();

        MainMenuView mainMenu = new MainMenuView(database, primaryStage);
        Scene scene = new Scene(mainMenu.getRoot(), GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> database.close());

        primaryStage.show();
        primaryStage.getScene().getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}