package com.example.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label birthdayLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label coinsLabel;
    @FXML private ImageView profileImage;
    @FXML private Button closeButton;

    @FXML
    public void initialize() {
        Student s = Main.getCurrentStudent();

        if (s != null) {
            nameLabel.setText("👤 " + s.getName());
            emailLabel.setText("📧 " + s.getEmail());
            birthdayLabel.setText("🎂 " + s.getBirthday());
            studentIdLabel.setText("🆔 " + s.getId());
            coinsLabel.setText("💰 Coins: " + s.getCoins());
        }

        try {
            Image img = new Image(getClass().getResourceAsStream("/com/example/game/images/profile.png"));
            profileImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Profile image not found.");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
