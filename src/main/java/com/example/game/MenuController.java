package com.example.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MenuController {

    @FXML public Button profileButton;
    @FXML private Label welcomeLabel;
    @FXML private Label icsGradeLabel;
    @FXML private Label splGradeLabel;
    @FXML private Label oopGradeLabel;
    @FXML private Label aoopGradeLabel;
    @FXML private Label dsaGradeLabel;
    @FXML private Label cgpaLabel;

    @FXML private Button startICSButton;
    @FXML private Button startSPLButton;
    @FXML private Button startOOPButton;
    @FXML private Button startAOOPButton;
    @FXML private Button startDSAButton;
    @FXML private Button progressButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        updateDashboard();
        Main.setMenuController(this);
    }


    public void refreshMenu() {
        updateDashboard();
    }

    private void updateDashboard() {
        Student s = Main.getCurrentStudent();

        if (s != null) {
            welcomeLabel.setText("Welcome, " + s.getName() + " | Coins: " + s.getCoins());
        } else {
            welcomeLabel.setText("Welcome, Guest");
        }

        double icsGrade = ProgressUtil.getICSGrade();
        double splGrade = ProgressUtil.getSPLGrade();
        double oopGrade = ProgressUtil.getOOPGrade();
        double aoopGrade = ProgressUtil.getAOOPGrade();
        double dsaGrade = ProgressUtil.getDSAGrade();
        double cgpa = ProgressUtil.getCGPA();

        if (icsGradeLabel != null)
            icsGradeLabel.setText("Grade: "+ (icsGrade == 0 ? "N/A" : String.format("%.2f", icsGrade)));
        if (splGradeLabel != null)
            splGradeLabel.setText("Grade: " + (splGrade == 0 ? "N/A" : String.format("%.2f", splGrade)));
        if (oopGradeLabel != null)
            oopGradeLabel.setText("Grade: " + (oopGrade == 0 ? "N/A" : String.format("%.2f", oopGrade)));
        if (aoopGradeLabel != null)
            aoopGradeLabel.setText("Grade: " + (aoopGrade == 0 ? "N/A" : String.format("%.2f", aoopGrade)));
        if (dsaGradeLabel != null)
            dsaGradeLabel.setText("Grade: " + (dsaGrade == 0 ? "N/A" : String.format("%.2f", dsaGrade)));  // NEW

        if (cgpaLabel != null)
            cgpaLabel.setText("CGPA: " + (cgpa == 0 ? "N/A" : String.format("%.2f", cgpa)));
    }

    @FXML
    private void handleStartICSGame() {
        startGame("ICS", "game.fxml", "Trimester 1: ICS Puzzle", 50, 0);
    }

    @FXML
    private void handleStartSPLGame() {
        if (ProgressUtil.getICSGrade() < 2.0) {
            showAlert(Alert.AlertType.WARNING, "🔒 You must pass Trimester 1 (ICS) to unlock SPL Puzzle!");
            return;
        }
        startGame("SPL", "spl_game.fxml", "Trimester 2: SPL Puzzle", 50, 2);
    }

    @FXML
    private void handleStartOOPGame() {
        if (ProgressUtil.getSPLGrade() < 2.0) {
            showAlert(Alert.AlertType.WARNING, "🔒 You must pass Trimester 2 (SPL) to unlock OOP Puzzle!");
            return;
        }
        startGame("OOP", "oop_game.fxml", "Trimester 3: OOP Puzzle", 50, 3);
    }


    @FXML
    private void handleStartAOOPGame() {
        if (ProgressUtil.getOOPGrade() < 2.0) {
            showAlert(Alert.AlertType.WARNING, "🔒 You must pass Trimester 3 (OOP) to unlock AOOP Puzzle!");
            return;
        }
        startGame("AOOP", "aoop_game.fxml", "Trimester 4: AOOP Puzzle", 50, 4);
    }

    @FXML
    private void handleStartDSAGame() {
        if (ProgressUtil.getAOOPGrade() < 2.0) {
            showAlert(Alert.AlertType.WARNING, "🔒 You must pass Trimester 4 (AOOP) to unlock DSA Sentence Builder!");
            return;
        }
        startGame("DSA", "dsa_game.fxml", "Trimester 5: DSA Sentence Builder", 50, 5);
    }


    private void startGame(String name, String fxml, String title, int cost, int trimester) {
        Student s = Main.getCurrentStudent();
        if (s == null) {
            Main.changeScene("login_2.fxml", "Login");
            return;
        }

        if (s.getCoins() < cost) {
            showAlert(Alert.AlertType.INFORMATION, "⚠️ Not enough coins to play!");
            return;
        }

        s.setCoins(s.getCoins() - cost);
        StudentDatabase.updateCoins(s);
        Main.setCurrentStudent(s);

        showAlert(Alert.AlertType.INFORMATION,
                "🎮 " + name + " Game started! " + cost + " coins deducted.");
        Main.changeScene(fxml, title);
    }


    @FXML
    private void handleViewProgress() {
        Main.changeScene("progress.fxml", "Progress Report");
    }


    @FXML
    private void handleLogout() {
        Main.setCurrentStudent(null);
        Main.changeScene("login_2.fxml", "Login");
    }

    @FXML
    private void handleViewProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("profile.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Student Profile");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to open profile window.");
        }
    }

    @FXML
    private void handleViewResult() {
        Main.changeScene("result.fxml", "Student Results");
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).show();
    }
}
