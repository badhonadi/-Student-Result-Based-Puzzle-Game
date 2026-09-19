package com.example.game;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ResultController {
    @FXML private TableView<ResultModel> resultTable;
    @FXML private TableColumn<ResultModel, String> courseCol;
    @FXML private TableColumn<ResultModel, String> nameCol;
    @FXML private TableColumn<ResultModel, String> idCol;
    @FXML private TableColumn<ResultModel, String> gradeCol;
    @FXML private TableColumn<ResultModel, String> cgpaCol;

    @FXML
    public void initialize() {
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        cgpaCol.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        resultTable.setItems(ResultService.getResults());
    }

    @FXML
    private void handleBackToMenu() {
        Main.changeScene("menu.fxml", "Main Menu");
    }
}
