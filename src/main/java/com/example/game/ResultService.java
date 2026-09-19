package com.example.game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResultService {
    private static final ObservableList<ResultModel> results = FXCollections.observableArrayList();

    public static void addResult(String course, String name, String id, double grade, double cgpa) {
        ResultModel result = new ResultModel(course, name, id, grade, cgpa);
        results.add(result);
    }

    public static ObservableList<ResultModel> getResults() {
        return results;
    }

    public static void submitResult(String course, String name, String id, double grade, double cgpa) {
        ResultModel result = new ResultModel(course, name, id, grade, cgpa);
        results.add(result);  // 'results' = your ObservableList<ResultModel>
    }



    public static void loadInitialResults() {
        results.clear();
    }
}
