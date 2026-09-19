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

public class SplGameController {

    @FXML private Label titleLabel, puzzleLabel, timerLabel, coinLabel, gradeLabel, feedbackLabel;
    @FXML private Button optionA, optionB, optionC;

    private Timeline timeline;
    private long startTime;
    private int timeLeft = 300;
    private int currentQuestion = 0;
    private int coins = 0;
    private int correctAnswers = 0;

    private final List<MCQQuestion> allQuestions = List.of(
            new MCQQuestion("int a = 5; printf(\"%d\", a++);", "6","5","Compilation Error","B"),
            new MCQQuestion("printf(\"%d\", a--); if a=10", "10","9","11","A"),
            new MCQQuestion("Valid variable name in C?", "int number1;","int 1number;","int #number;","A"),
            new MCQQuestion("int x=3; int y=++x; printf(\"%d\", y);", "3","4","Compilation Error","B"),
            new MCQQuestion("Header for malloc()?", "stdlib.h","stdio.h","string.h","A"),
            new MCQQuestion("sizeof(char) returns?", "1","2","Depends on OS","A"),
            new MCQQuestion("Output of: int x=2; printf(\"%d\", x*3);", "6","5","Error","A"),
            new MCQQuestion("Which loop guarantees at least once execution?", "for","while","do-while","C"),
            new MCQQuestion("Keyword to define constant?", "const","final","static","A"),
            new MCQQuestion("What does strcmp return if equal?", "0","1","-1","A"),
            new MCQQuestion("Function to open file in C?", "fopen","open","fileopen","A"),
            new MCQQuestion("Which is logical AND operator?", "&","&&","||","B")
    );

    private List<MCQQuestion> selectedQuestions = new ArrayList<>();

    @FXML
    public void initialize() {
        Student s = Main.getCurrentStudent();
        coins = (s != null) ? s.getCoins() : 0;
        coinLabel.setText("💰 Coins: " + coins);

        titleLabel.setText((s != null ? "🎓 " + s.getName() : "🎮") + " | Trimester 2: SPL Quiz 🎮");

        feedbackLabel.setText("");
        gradeLabel.setText("🎯 Grade: --");

        selectRandomQuestions();
        startTime = System.currentTimeMillis();
        startTimer();
        showQuestion();
    }

    private void selectRandomQuestions() {
        List<MCQQuestion> temp = new ArrayList<>(allQuestions);
        Collections.shuffle(temp);
        selectedQuestions = temp.subList(0, 6);
    }

    private void showQuestion() {
        if (currentQuestion >= selectedQuestions.size()) {
            endGame();
            return;
        }

        MCQQuestion q = selectedQuestions.get(currentQuestion);
        puzzleLabel.setText(q.getQuestion());
        optionA.setText("A) " + q.getOptionA());
        optionB.setText("B) " + q.getOptionB());
        optionC.setText("C) " + q.getOptionC());
        feedbackLabel.setText("");
    }

    @FXML
    private void handleOptionA() { checkAnswer("A"); }
    @FXML
    private void handleOptionB() { checkAnswer("B"); }
    @FXML
    private void handleOptionC() { checkAnswer("C"); }

    private void checkAnswer(String selected) {
        MCQQuestion q = selectedQuestions.get(currentQuestion);
        if (selected.equals(q.getCorrectOption())) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            coins += 10;
            correctAnswers++;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + q.getCorrectOption());
            feedbackLabel.setTextFill(Color.RED);
        }

        coinLabel.setText("💰 Coins: " + coins);
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

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        double finalGrade = calculateGrade(correctAnswers, totalTime);

        ProgressUtil.setSPLGrade(finalGrade);

        int min = (int)(totalTime/60);
        int sec = (int)(totalTime%60);
        String formattedTime = String.format("%02d:%02d", min, sec);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SPL Quiz Summary");
        alert.setHeaderText("Quiz Completed!");
        alert.setContentText("✅ You got " + correctAnswers + " out of " + selectedQuestions.size() + " correct\n"
                + " Grade: " + String.format("%.2f", finalGrade) + "\n"
                + " Time: " + formattedTime + "\n"
                + " Coins Earned: " + coins);
        alert.getButtonTypes().setAll(new ButtonType("Back to Menu", ButtonBar.ButtonData.OK_DONE));

        alert.showAndWait();
        Student s = Main.getCurrentStudent();
        if (s != null) {
            // update coins locally and DB
            s.setCoins(coins);
            StudentDatabase.updateCoins(s);

            // set spl grade in ProgressUtil (so menu boxes reflect it)
            ProgressUtil.setSPLGrade(finalGrade);

            // add to local observable list so result.fxml shows it
            double cgpa = (ProgressUtil.getICSGrade()+ProgressUtil.getSPLGrade())/2;
            ResultService.addResult("SPL", s.getName(), s.getId(), finalGrade, cgpa);

            // send to server (network)
            ResultClient.sendResult("SPL", s.getName(), s.getId(), finalGrade, cgpa);

            // refresh menu UI (if open)
            if (Main.getMenuController() != null) {
                Main.getMenuController().refreshMenu();
            }

            // append to local progress log file
            try (FileWriter fw = new FileWriter("progress.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("Trimester-2 | Grade: " + String.format("%.2f", finalGrade)
                        + " | CGPA: " + String.format("%.2f", cgpa)
                        + " | Coins: " + coins
                        + " | Time: " + LocalDateTime.now());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Main.changeScene("menu.fxml", "Main Menu");
    }

    private double calculateGrade(int correct, long totalTime) {
        // Accuracy percentage
        double accuracy = (double) correct / selectedQuestions.size();

        // Base grade based on accuracy (max 4.0)
        double grade = 2.5 + 1.5 * accuracy;  // 0 correct -> 2.5, all correct -> 4.0

        // Time penalty: faster is better, slower reduces slightly
        if (totalTime > 300) {  // If over 5 min
            grade -= 0.25;
        } else if (totalTime > 240) {  // 4-5 min
            grade -= 0.15;
        } else if (totalTime > 180) {  // 3-4 min
            grade -= 0.05;
        }

        // Cap grade between 2.5 and 4.0
        if (grade > 4.0) grade = 4.0;
        if (grade < 2.5) grade = 2.5;

        return Math.round(grade * 100.0) / 100.0; // round to 2 decimals
    }

    private void saveProgress(double grade) {
        Student s = Main.getCurrentStudent();
        if (s != null) {
            // update coins locally and DB
            s.setCoins(coins);
            StudentDatabase.updateCoins(s);

            // set spl grade in ProgressUtil (so menu boxes reflect it)
            ProgressUtil.setSPLGrade(grade);

            // add to local observable list so result.fxml shows it
            double cgpa = (ProgressUtil.getICSGrade()+ProgressUtil.getSPLGrade())/2;
            ResultService.addResult("SPL", s.getName(), s.getId(), grade, cgpa);

            // send to server (network)
            ResultClient.sendResult("SPL", s.getName(), s.getId(), grade, cgpa);

            // refresh menu UI (if open)
            if (Main.getMenuController() != null) {
                Main.getMenuController().refreshMenu();
            }

            // append to local progress log file
            try (FileWriter fw = new FileWriter("progress.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("Trimester-2 | Grade: " + String.format("%.2f", grade)
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

    private static class MCQQuestion {
        private final String question;
        private final String optionA;
        private final String optionB;
        private final String optionC;
        private final String correctOption;

        public MCQQuestion(String q, String a, String b, String c, String correct) {
            question = q; optionA = a; optionB = b; optionC = c; correctOption = correct;
        }

        public String getQuestion() { return question; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getCorrectOption() { return correctOption; }
    }
}
