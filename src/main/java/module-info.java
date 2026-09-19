module com.example.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // FXML er moddhe jodi controller thake
    opens com.example.game to javafx.fxml;
    exports com.example.game;
}
