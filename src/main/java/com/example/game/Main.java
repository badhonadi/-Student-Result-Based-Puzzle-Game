package com.example.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;
    public static final String RES = "/com/example/game/";
    private static Student currentStudent;

    private static MenuController menuController;

    public static void setMenuController(MenuController controller) {
        menuController = controller;
    }

    public static MenuController getMenuController() {
        return menuController;
    }

    public static void setCurrentStudent(Student s) {
        currentStudent = s;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        ResultService.loadInitialResults();

        changeScene("register.fxml", "Welcome to University Game");

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void changeScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(RES + fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(Main.class.getResource(RES + "style.css").toExternalForm());
            } catch (Exception ignored) {}

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load FXML: " + fxmlFile);
        }
    }

    public static void changeScene(String fxmlFile, String title, String studentId) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(RES + fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof LoginController loginController) {
                loginController.setPrefilledId(studentId);
            }

            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(Main.class.getResource(RES + "style.css").toExternalForm());
            } catch (Exception ignored) {}

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load FXML with studentId: " + fxmlFile);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
