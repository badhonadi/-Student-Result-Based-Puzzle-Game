package com.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class AoopGameController {

    @FXML private Label titleLabel;
    @FXML private Label puzzleLabel;
    @FXML private Label feedbackLabel, gradeLabel, timerLabel, coinLabel;
    @FXML private Button optionA, optionB, optionC, trueBtn, falseBtn;

    private Timeline timeline;
    private long startTime;
    private int timeLeft = 300;
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private int coins = 0;

    // Question format: {questionText, type, correctAnswer, optionA, optionB, optionC}
    // type = "MCQ" or "TF" (True/False)
    private final String[][] allQuestions = {
            {"Which layout manager arranges nodes in a vertical column?", "MCQ", "VBox", "VBox", "HBox", "FlowPane"},
            {"In JavaFX, Label can contain images.", "TF", "TRUE", "", "", ""},
            {"Which method starts the JavaFX application?", "MCQ", "start", "launch", "start", "init"},
            {"Circle is a Node in JavaFX.", "TF", "TRUE", "", "", ""},
            {"Which class is used for drawing shapes in JavaFX?", "MCQ", "Shape", "Shape", "Graphics", "Canvas"},
            {"You can attach CSS to JavaFX nodes.", "TF", "TRUE", "", "", ""},
            {"What is the parent class for all JavaFX UI controls?", "MCQ", "Control", "Node", "Control", "Region"},
            {"JavaFX SceneGraph is a tree structure.", "TF", "TRUE", "", "", ""},
            {"Button text cannot be changed dynamically.", "TF", "FALSE", "", "", ""},
            {"Which layout manager stacks nodes on top of each other?", "MCQ", "StackPane", "HBox", "VBox", "StackPane"},
            {"TextField can accept multiple lines of input.", "TF", "FALSE", "", "", ""},
            {"Which class handles events in JavaFX?", "MCQ", "EventHandler", "Event", "EventHandler", "ActionEvent"},
            {"A Stage can contain multiple Scenes simultaneously.", "TF", "FALSE", "", "", ""},
            {"Rectangle class can be filled with color.", "TF", "TRUE", "", "", ""}
    };

    private List<String[]> selectedQuestions;

    @FXML
    private void initialize() {
        Student s = Main.getCurrentStudent();
        coins = (s != null) ? s.getCoins() : 0;
        coinLabel.setText("💰 Coins: " + coins);

        titleLabel.setText((s != null ? "🎓 " + s.getName() : "🎮") + " | Trimester 4: AOOP JavaFX Quiz 🎯");

        feedbackLabel.setText("");
        gradeLabel.setText("🎯 Grade: --");

        selectRandomQuestions();
        startTime = System.currentTimeMillis();
        startTimer();
        showQuestion();
    }

    private void selectRandomQuestions() {
        List<String[]> list = new ArrayList<>(Arrays.asList(allQuestions));
        Collections.shuffle(list);
        selectedQuestions = new ArrayList<>(list.subList(0, 7)); // pick 7 random questions
    }

    private void showQuestion() {
        if (currentIndex >= selectedQuestions.size()) {
            endGame();
            return;
        }

        String[] q = selectedQuestions.get(currentIndex);
        String type = q[1];

        // Reset all buttons
        optionA.setVisible(false); optionB.setVisible(false); optionC.setVisible(false);
        trueBtn.setVisible(false); falseBtn.setVisible(false);

        puzzleLabel.setTextFill(Color.YELLOW);
        puzzleLabel.setText("Q" + (currentIndex + 1) + ": " + q[0]);
        feedbackLabel.setText("");

        if (type.equals("MCQ")) {
            optionA.setVisible(true); optionB.setVisible(true); optionC.setVisible(true);
            optionA.setText("A. " + q[3]);
            optionB.setText("B. " + q[4]);
            optionC.setText("C. " + q[5]);
        } else if (type.equals("TF")) {
            trueBtn.setVisible(true); falseBtn.setVisible(true);
        }
    }
    @FXML
    private void handleTrueBtn() { handleTF(true); }

    @FXML
    private void handleFalseBtn() { handleTF(false); }


    @FXML
    private void handleMCQ(javafx.event.ActionEvent event) {
        if (currentIndex >= selectedQuestions.size()) return;
        String[] q = selectedQuestions.get(currentIndex);
        Button btn = (Button) event.getSource();
        String userAns = btn.getText().substring(3).trim().toUpperCase();

        if (userAns.equals(q[2].toUpperCase())) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            correctAnswers++;
            coins += 10;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + q[2]);
            feedbackLabel.setTextFill(Color.RED);
        }

        coinLabel.setText("💰 Coins: " + coins);
        currentIndex++;
        showQuestion();
    }

    @FXML
    private void handleTF(boolean userAns) {
        if (currentIndex >= selectedQuestions.size()) return;
        String[] q = selectedQuestions.get(currentIndex);
        boolean correct = q[2].equalsIgnoreCase("TRUE");

        if (userAns == correct) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            correctAnswers++;
            coins += 10;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + q[2]);
            feedbackLabel.setTextFill(Color.RED);
        }

        coinLabel.setText("💰 Coins: " + coins);
        currentIndex++;
        showQuestion();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimer() {
        timeLeft--;
        int min = timeLeft / 60;
        int sec = timeLeft % 60;
        timerLabel.setText(String.format("⏳ Time: %02d:%02d", min, sec));

        if (timeLeft <= 0) {
            timeline.stop();
            feedbackLabel.setText("⏰ Time’s up!");
            feedbackLabel.setTextFill(Color.RED);
            endGame();
        }
    }

    private void endGame() {
        if (timeline != null) timeline.stop();

        long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
        double timeFactor = totalSeconds <= 300 ? (300.0 - totalSeconds) / 300.0 : 0.5;
        double accuracy = correctAnswers / 7.0;
        double finalGrade = Math.min(4.0, 4.0 * accuracy * timeFactor);

        Student s = Main.getCurrentStudent();
        if (s != null) {
            s.setCoins(s.getCoins() + coins);
            StudentDatabase.updateCoins(s);
        }

        ProgressUtil.setAOOPGrade(finalGrade);
        double cgpa =(ProgressUtil.getICSGrade()+ProgressUtil.getSPLGrade()+ProgressUtil.getOOPGrade()+ProgressUtil.getAOOPGrade())/4;

        feedbackLabel.setText(String.format(
                "✅ You got %d out of 7 correct\n🎯 Grade: %.2f\n⏳ Time: %02d:%02d\n💰 Coins Earned: %d",
                correctAnswers, finalGrade, totalSeconds / 60, totalSeconds % 60, coins
        ));
        feedbackLabel.setTextFill(Color.GOLD);
        gradeLabel.setText(String.format("🎯 Grade: %.2f", finalGrade));

        ResultService.addResult("AOOP", s.getName(), s.getId(), finalGrade, cgpa);

        // send to server (network)
        ResultClient.sendResult("AOOP", s.getName(), s.getId(), finalGrade, cgpa);

        // refresh menu UI (if open)
        if (Main.getMenuController() != null) {
            Main.getMenuController().refreshMenu();
        }
        try (FileWriter fw = new FileWriter("progress.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("Trimester-4 | Grade: " + String.format("%.2f", finalGrade)
                    + " | CGPA: " + String.format("%.2f", cgpa)
                    + " | Coins: " + coins
                    + " | Time: " + LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        Main.changeScene("menu.fxml", "Main Menu");
    }
}
