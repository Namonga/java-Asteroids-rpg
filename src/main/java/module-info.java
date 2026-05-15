module org.com.gamep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.com.gamep to javafx.fxml;
    exports org.com.gamep;
    exports org.com.gamep.sprites;
    opens org.com.gamep.sprites to javafx.fxml;
    exports org.com.gamep.game;
    opens org.com.gamep.game to javafx.fxml;
}