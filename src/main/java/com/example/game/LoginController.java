package com.example.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void login() {
        String id = idField.getText().trim();
        String pass = passwordField.getText();
        if (id.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Enter ID and password");
            return;
        }
        Student s = StudentDatabase.login(id, pass);
        if (s == null) {
            messageLabel.setText("Invalid ID or password");
            return;
        }
        Main.setCurrentStudent(s);
        Main.changeScene("menu.fxml", "Main Menu");
    }
    public void setPrefilledId(String id) {
        idField.setText(id);
    }


    @FXML
    private void goToRegister() {
        Main.changeScene("register.fxml", "Register");
    }

    @FXML
    private void goToResetPassword() {
        Main.changeScene("ResetPassword.fxml", "Reset Password");
    }
}
