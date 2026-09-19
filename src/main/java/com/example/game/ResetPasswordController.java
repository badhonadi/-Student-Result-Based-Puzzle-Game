package com.example.game;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ResetPasswordController {

    @FXML
    private TextField idField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void resetPassword() {
        String id = idField.getText().trim();
        String p1 = newPasswordField.getText();
        String p2 = confirmPasswordField.getText();

        if (id.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            messageLabel.setText("⚠️ Please fill all fields!");
            return;
        }

        if (!p1.equals(p2)) {
            messageLabel.setText("❌ Passwords do not match!");
            return;
        }

        boolean ok = StudentDatabase.updatePassword(id, p1);

        if (ok) {
            messageLabel.setText("✅ Password successfully updated!");
        } else {
            messageLabel.setText("❌ User ID not found.");
        }
    }

    @FXML
    private void goToLogin() {
        Main.changeScene("login_2.fxml", "Login");
    }
}
