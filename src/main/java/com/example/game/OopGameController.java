package com.example.game;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class OopGameController {

    @FXML private Label titleLabel;
    @FXML private Label puzzleLabel;
    @FXML private Label feedbackLabel;
    @FXML private Label gradeLabel;
    @FXML private Label timerLabel;
    @FXML private Label coinLabel;
    @FXML private Button optionA, optionB, optionC;
    @FXML private Button backButton;

    private Timeline timeline;
    private long startTime;
    private int timeLeft = 300;
    private int currentIndex = 0;
    private int correctAnswers = 0;

    private final String[][] allQuestions = {
            {"What is inheritance in Java?", "Mechanism of deriving class", "Method overloading", "Encapsulation", "A"},
            {"Which keyword is used to define a class?", "class", "interface", "object", "A"},
            {"What is polymorphism?", "Multiple forms of object", "Private access", "Class variable", "A"},
            {"Which is not a Java access modifier?", "private", "protected", "package", "C"},
            {"What does 'new' keyword do?", "Creates object", "Calls method", "Deletes object", "A"},
            {"Which method is entry point of Java program?", "main()", "start()", "run()", "A"},
            {"What is encapsulation?", "Wrapping data & methods", "Data hiding only", "Method overloading", "A"},
            {"Which is a constructor?", "Same name as class", "main()", "void function", "A"},
            {"Which of these is an interface?", "abstract class", "interface", "enum", "B"},
            {"What is super keyword used for?", "Refer parent class", "Declare variable", "Access private method", "A"},
            {"Which of these is not a primitive type?", "int", "String", "double", "B"},
            {"What is method overloading?", "Same method different params", "Override method", "Encapsulation", "A"},
            {"Which keyword prevents inheritance?", "static", "final", "private", "B"},
            {"Which is correct way to implement interface?", "class A implements I", "class A extends I", "interface A implements I", "A"},
            {"Which of these is a Java package?", "java.util", "system.io", "javax.file", "A"}
    };

    private List<String[]> selectedQuestions;

    @FXML
    public void initialize() {
        Student s = Main.getCurrentStudent();
        if (s != null) {
            coinLabel.setText("💰 Coins: " + s.getCoins());
            titleLabel.setText("🎓 " + s.getName() + " | Trimester 3: OOP Quiz 🎯");
        } else {
            coinLabel.setText("💰 Coins: 0");
            titleLabel.setText("🎮 Trimester 3: OOP Quiz 🎮");
        }

        feedbackLabel.setText("");
        gradeLabel.setText("🎯 Grade: --");

        selectRandomQuestions();
        startTime = System.currentTimeMillis();
        showQuestion();
        startTimer();
    }

    private void selectRandomQuestions() {
        List<String[]> list = new ArrayList<>(Arrays.asList(allQuestions));
        Collections.shuffle(list);
        selectedQuestions = new ArrayList<>(list.subList(0, 7)); // 7 random questions
    }

    private void showQuestion() {
        if (currentIndex >= selectedQuestions.size()) {
            endGame();
            return;
        }

        String[] q = selectedQuestions.get(currentIndex);
        puzzleLabel.setText("❓ " + q[0]);
        optionA.setText(q[1]);
        optionB.setText(q[2]);
        optionC.setText(q[3]);
        feedbackLabel.setText("");
    }

    @FXML
    private void handleOptionA() { checkAnswer("A"); }
    @FXML
    private void handleOptionB() { checkAnswer("B"); }
    @FXML
    private void handleOptionC() { checkAnswer("C"); }

    private void checkAnswer(String selected) {
        String correct = selectedQuestions.get(currentIndex)[4];
        if (selected.equals(correct)) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            correctAnswers++;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + correct);
            feedbackLabel.setTextFill(Color.RED);
        }
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

        int coinsEarned = correctAnswers * 10;

        Student s = Main.getCurrentStudent();
        if (s != null) {
            s.setCoins(s.getCoins() + coinsEarned);
            StudentDatabase.updateCoins(s);
        }

        // Save grade for menu
        ProgressUtil.setOOPGrade(finalGrade);
        double cgpa =(ProgressUtil.getICSGrade()+ProgressUtil.getSPLGrade()+ProgressUtil.getOOPGrade())/3;

        // Show popup style result
        feedbackLabel.setText(String.format(
                "✅ You got %d out of 7 correct\n🎯 Grade: %.2f\n⏳ Time: %02d:%02d\n💰 Coins Earned: %d",
                correctAnswers, finalGrade, totalSeconds / 60, totalSeconds % 60, coinsEarned
        ));
        feedbackLabel.setTextFill(Color.GOLD);

        gradeLabel.setText(String.format("🎯 Grade: %.2f", finalGrade));
        ResultService.addResult("OOP", s.getName(), s.getId(), finalGrade, cgpa);

        // send to server (network)
        ResultClient.sendResult("OOP", s.getName(), s.getId(), finalGrade, cgpa);

        // refresh menu UI (if open)
        if (Main.getMenuController() != null) {
            Main.getMenuController().refreshMenu();
        }
        try (FileWriter fw = new FileWriter("progress.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("Trimester-3 | Grade: " + String.format("%.2f", finalGrade)
                    + " | CGPA: " + String.format("%.2f", cgpa)
                    + " | Coins: " + coinsEarned
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
