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

public class DsaGameController {

    @FXML private Label titleLabel;
    @FXML private Label questionLabel;
    @FXML private Label feedbackLabel, gradeLabel, timerLabel, coinLabel;
    @FXML private Button optionA, optionB, optionC, trueBtn, falseBtn;

    private Timeline timeline;
    private long startTime;
    private int timeLeft = 300; // 5 minutes
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private int coins = 0;

    // 14 DSA questions (mix of MCQ and True/False)
    private final String[][] allQuestions = {
            {"What is the time complexity of binary search in a sorted array?", "O(log n)", "O(n)", "O(n^2)", "A", "MCQ"},
            {"A stack is a FIFO data structure.", "True", "False", "", "B", "TF"},
            {"Which traversal is used in Depth First Search?", "Inorder", "DFS", "BFS", "B", "MCQ"},
            {"Queue can be implemented using two stacks.", "True", "False", "", "A", "TF"},
            {"What is the height of a complete binary tree with n nodes?", "log n", "n", "n log n", "A", "MCQ"},
            {"Linked list nodes are stored at contiguous memory locations.", "True", "False", "", "B", "TF"},
            {"What is the worst case time complexity of quicksort?", "O(n log n)", "O(n^2)", "O(n)", "B", "MCQ"},
            {"A hash table uses a key-value pair mapping.", "True", "False", "", "A", "TF"},
            {"In a min-heap, the root is always the minimum element.", "True", "False", "", "A", "TF"},
            {"DFS uses stack data structure.", "True", "False", "", "A", "TF"},
            {"The number of edges in a tree with n nodes is n.", "True", "False", "", "B", "TF"},
            {"Binary Search Tree allows duplicate elements.", "True", "False", "", "B", "TF"},
            {"In BFS, nodes are visited level by level.", "True", "False", "", "A", "TF"},
            {"What is the space complexity of merge sort?", "O(n)", "O(log n)", "O(1)", "A", "MCQ"}
    };

    private List<String[]> selectedQuestions;

    @FXML
    public void initialize() {
        Student s = Main.getCurrentStudent();
        if (s != null) {
            coinLabel.setText("💰 Coins: " + s.getCoins());
            titleLabel.setText("🎓 " + s.getName() + " | Trimester 5: DSA Quiz 🧠");
        } else {
            coinLabel.setText("💰 Coins: 0");
            titleLabel.setText("🎮 Trimester 5: DSA Quiz 🎮");
        }

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
        selectedQuestions = new ArrayList<>(list.subList(0, 7)); // select 7 random questions
    }

    private void showQuestion() {
        if (currentIndex >= selectedQuestions.size()) {
            endGame();
            return;
        }

        String[] q = selectedQuestions.get(currentIndex);
        questionLabel.setText("🧩 Q" + (currentIndex + 1) + ": " + q[0]);
        feedbackLabel.setText("");
        optionA.setVisible(false);
        optionB.setVisible(false);
        optionC.setVisible(false);
        trueBtn.setVisible(false);
        falseBtn.setVisible(false);

        if ("MCQ".equals(q[5])) {
            optionA.setText("A. " + q[1]);
            optionB.setText("B. " + q[2]);
            optionC.setText("C. " + q[3]);
            optionA.setVisible(true);
            optionB.setVisible(true);
            optionC.setVisible(true);
        } else {
            trueBtn.setVisible(true);
            falseBtn.setVisible(true);
        }
    }

    @FXML
    private void handleMCQ(javafx.event.ActionEvent e) {
        String[] q = selectedQuestions.get(currentIndex);
        Button btn = (Button) e.getSource();
        String answer = btn.getText().substring(0, 1); // "A"/"B"/"C"
        if (answer.equals(q[4])) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            correctAnswers++;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + q[4]);
            feedbackLabel.setTextFill(Color.RED);
        }
        currentIndex++;
        showQuestion();
    }

    @FXML
    private void handleTF(boolean userAns) {
        String[] q = selectedQuestions.get(currentIndex);
        boolean correct = "A".equals(q[4]);
        boolean isCorrect = (userAns && correct) || (!userAns && !"A".equals(q[4]));
        if (isCorrect) {
            feedbackLabel.setText("✅ Correct!");
            feedbackLabel.setTextFill(Color.LIGHTGREEN);
            correctAnswers++;
            coins+=10;
        } else {
            feedbackLabel.setText("❌ Wrong! Correct: " + (correct ? "TRUE" : "FALSE"));
            feedbackLabel.setTextFill(Color.RED);
        }
        currentIndex++;
        showQuestion();
    }

    @FXML
    private void handleTrueBtn() { handleTF(true); }
    @FXML
    private void handleFalseBtn() { handleTF(false); }

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
        Student s = Main.getCurrentStudent();
        if (s != null) {
            s.setCoins(s.getCoins() + coins);
            StudentDatabase.updateCoins(s);
        }

        double dsaGrade = calculateGrade();
        double icsGrade = ProgressUtil.getICSGrade();
        double splGrade = ProgressUtil.getSPLGrade();
        double oopGrade = ProgressUtil.getOOPGrade();
        double aoopGrade = ProgressUtil.getAOOPGrade();
        double totalCGPA = (icsGrade + splGrade + oopGrade + aoopGrade + dsaGrade) / 5;

        ProgressUtil.setDSAGrade(dsaGrade);
        ProgressUtil.saveGrades();

        feedbackLabel.setText("🏆 Game Complete!");
        feedbackLabel.setTextFill(Color.GOLD);
        gradeLabel.setText(String.format("🎯 DSA: %.2f | Total CGPA: %.2f", dsaGrade, totalCGPA));

        saveProgress("Trimester-5", dsaGrade, totalCGPA);

        ResultService.addResult("DSA", s.getName(), s.getId(), dsaGrade, totalCGPA);

        // send to server (network)
        ResultClient.sendResult("DSA", s.getName(), s.getId(), dsaGrade, totalCGPA);

        if (Main.getMenuController() != null) {
            Main.getMenuController().refreshMenu();
        }
        try (FileWriter fw = new FileWriter("progress.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("Trimester-5 | Grade: " + String.format("%.2f", dsaGrade)
                    + " | CGPA: " + String.format("%.2f", dsaGrade)
                    + " | Time: " + LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateGrade() {
        long endTime = System.currentTimeMillis();
        int totalSeconds = (int) ((endTime - startTime) / 1000);

        double accuracy = correctAnswers / 7.0; // 7 questions
        double timeFactor;

        if (totalSeconds <= 120) timeFactor = 1.0;
        else if (totalSeconds <= 180) timeFactor = 0.9;
        else if (totalSeconds <= 240) timeFactor = 0.8;
        else if (totalSeconds <= 300) timeFactor = 0.7;
        else timeFactor = 0.6;

        return Math.min(4.0, accuracy * 4.0 * timeFactor);
    }

    private void saveProgress(String trimester, double grade, double cgpa) {
        try (FileWriter fw = new FileWriter("progress.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(trimester + " | Grade: " + String.format("%.2f", grade)
                    + " | CGPA: " + String.format("%.2f", cgpa)
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
