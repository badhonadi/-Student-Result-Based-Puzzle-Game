package com.example.game;

public class ResultModel {
    private String course;
    private String name;
    private String id;
    private double grade;
    private double cgpa;

    public ResultModel(String course, String name, String id, double grade, double cgpa) {
        this.course = course;
        this.name = name;
        this.id = id;
        this.grade = grade;
        this.cgpa = cgpa;
    }


    public String getCourse() { return course; }
    public String getName() { return name; }
    public String getId() { return id; }
    public double getGrade() { return grade; }
    public double getCgpa() { return cgpa; }
}
