package com.example.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthdayPicker;
    @FXML private Label messageLabel;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    private void register() {
        String name = nameField.getText().trim();
        String pass = passwordField.getText();
        String email = emailField.getText().trim();
        LocalDate birthday = birthdayPicker.getValue();

        if (name.isEmpty() || pass.isEmpty() || email.isEmpty() || birthday == null) {
            showMessage("⚠ Please fill all fields!", "red");
            return;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            showMessage("❌ Please enter a valid name (letters only).", "red");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showMessage("❌ Please enter a valid email address.", "red");
            return;
        }

        Student s = StudentDatabase.register(name, pass, email, birthday);

        if (s == null) {
            showMessage("⚠ User already exists with this name!", "red");
            return;
        }

        showMessage("✅ Registered Successfully! Your ID: " + s.getId(), "green");

        new Thread(() -> {
            try {
                Thread.sleep(1200);
                javafx.application.Platform.runLater(() ->
                        Main.changeScene("login_2.fxml", "Login", s.getId())
                );
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @FXML
    private void goToLogin() {
        Main.changeScene("login_2.fxml", "Login");
    }

    // 🔸 Helper method to show colored message text
    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
    @FXML
    public void registerStudent(ActionEvent event) {
        String name = nameField.getText().trim();
        String pass = passwordField.getText();
        String email = emailField.getText().trim();
        LocalDate birthday = birthdayPicker.getValue();

        if (name.isEmpty() || pass.isEmpty() || email.isEmpty() || birthday == null) {
            messageLabel.setText("Please fill all fields!");
            return;
        }

        if (!Pattern.compile("^[a-zA-Z\\s]+$").matcher(name).matches()) {
            messageLabel.setText("Please enter a valid name (only alphabets).");
            return;
        }

        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches()) {
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        Student s = StudentDatabase.register(name, pass, email, birthday);
        if (s == null) {
            messageLabel.setText("User already exists (same name).");
            return;
        }

        messageLabel.setText("Registered! ID: " + s.getId());
        Main.changeScene("login_2.fxml", "Login", s.getId());
    }


}
