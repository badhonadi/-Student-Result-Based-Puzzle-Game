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

public class GameController {

    @FXML private Label titleLabel, puzzleLabel, feedbackLabel, timerLabel, gradeLabel, coinLabel;
    @FXML private Button trueBtn, falseBtn;

    private int timeLeft = 300;
    private long startTime;
    private Timeline timeline;
    private int currentQuestion = 0;
    private int coins = 0;
    private int correctCount = 0;

    private List<String> selectedQuestions = new ArrayList<>();
    private List<Boolean> selectedAnswers = new ArrayList<>();

    private final String[] allQuestions = {
            "The CPU is known as the brain of the computer.",
            "RAM is a permanent storage device.",
            "Binary number system uses digits 0 and 1.",
            "An input device is used to show output on the screen.",
            "Compiler translates source code into machine code.",
            "Hard disk is volatile memory.",
            "A bit is the smallest unit of data in a computer.",
            "Monitor is an input device.",
            "ROM stores the bootloader or firmware.",
            "Keyboard is an input device.",
            "ALU performs arithmetic and logic operations.",
            "A printer is an output device."
    };

    private final boolean[] allAnswers = {
            true, false, true, false, true, false, true, false, true, true, true, true
    };

    @FXML
    private void initialize() {
        Student s = Main.getCurrentStudent();
        coins = (s != null) ? s.getCoins() : 0;
        coinLabel.setText(" Coins: " + coins);

        titleLabel.setText((s != null ? " " + s.getName() : "") + " | Trimester 1: ICS Quiz ");

        feedbackLabel.setText("");
        gradeLabel.setText(" Grade: --");

        selectRandomQuestions();
        startTime = System.currentTimeMillis();
        showQuestion();
        startTimer();
    }

    private void selectRandomQuestions() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < allQuestions.length; i++) indices.add(i);
        Collections.shuffle(indices);

        selectedQuestions.clear();
        selectedAnswers.clear();
        for (int i = 0; i < 5; i++) {
            selectedQuestions.add(allQuestions[indices.get(i)]);
            selectedAnswers.add(allAnswers[indices.get(i)]);
        }
    }

    private void showQuestion() {
        if (currentQuestion < selectedQuestions.size()) {
            puzzleLabel.setText("True or False: " + selectedQuestions.get(currentQuestion));
            feedbackLabel.setText("");
        } else {
            endGame();
        }
    }

    @FXML
    private void handleTrue() {
        checkAnswer(true);
    }

    @FXML
    private void handleFalse() {
        checkAnswer(false);
    }

    private void checkAnswer(boolean userAnswer) {
        if (userAnswer == selectedAnswers.get(currentQuestion)) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            coins += 10;
            correctCount++;
        } else {
            feedbackLabel.setText(" Wrong!");
            feedbackLabel.setTextFill(Color.RED);
        }

        coinLabel.setText(" Coins: " + coins);
        currentQuestion++;
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
        timerLabel.setText(String.format(" Time: %02d:%02d", min, sec));

        if (timeLeft <= 0) {
            timeline.stop();
            feedbackLabel.setText(" Time’s up!");
            feedbackLabel.setTextFill(Color.RED);
            endGame();
        }
    }

    private void endGame() {
        if (timeline != null) timeline.stop();

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        double finalGrade = calculateGrade(correctCount, totalTime);

        // Save locally, update UI, push to ResultService and server
        saveProgress(finalGrade);

        int min = (int) (totalTime / 60);
        int sec = (int) (totalTime % 60);
        String formattedTime = String.format("%02d:%02d", min, sec);

        // Show popup summary (user will press OK to return to menu manually)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Summary");
        alert.setHeaderText("ICS Quiz Completed!");
        alert.setContentText("✅ You got " + correctCount + " out of 5 correct\n"
                + " Grade: " + String.format("%.2f", finalGrade) + "\n"
                + " Time: " + formattedTime + "\n"
                + " Coins Earned: " + (correctCount * 10));
        alert.getButtonTypes().setAll(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));

        alert.showAndWait();

        // Back to menu (same flow as before)
        Main.changeScene("menu.fxml", "Main Menu");
    }

    private double calculateGrade(int correct, long totalTime) {
        double baseGrade;

        switch (correct) {
            case 5 -> baseGrade = 4.0;
            case 4 -> baseGrade = 3.5;
            case 3 -> baseGrade = 3.0;
            case 2 -> baseGrade = 2.75;
            case 1 -> baseGrade = 2.5;
            default -> baseGrade = 2.0;
        }

        if (totalTime <= 90) baseGrade += 0.25;
        else if (totalTime <= 180) baseGrade += 0.10;

        return Math.min(baseGrade, 4.0);
    }

    private void saveProgress(double grade) {
        Student s = Main.getCurrentStudent();
        if (s != null) {
            // update coins locally and DB
            s.setCoins(coins);
            StudentDatabase.updateCoins(s);

            // set ICS grade in ProgressUtil (so menu boxes reflect it)
            ProgressUtil.setICSGrade(grade);

            // add to local observable list so result.fxml shows it
            double cgpa = ProgressUtil.getCGPA();
            ResultService.addResult("ICS", s.getName(), s.getId(), grade, cgpa);

            // send to server (network)
            ResultClient.sendResult("ICS", s.getName(), s.getId(), grade, cgpa);

            // refresh menu UI (if open)
            if (Main.getMenuController() != null) {
                Main.getMenuController().refreshMenu();
            }

            // append to local progress log file
            try (FileWriter fw = new FileWriter("progress.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("Trimester-1 | Grade: " + String.format("%.2f", grade)
                        + " | CGPA: " + String.format("%.2f", cgpa)
                        + " | Coins: " + coins
                        + " | Time: " + LocalDateTime.now());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBack() {
        if (timeline != null) timeline.stop();
        Main.changeScene("menu.fxml", "Main Menu");
    }
}
