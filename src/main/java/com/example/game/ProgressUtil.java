package com.example.game;

import java.io.*;
import java.util.*;

public class ProgressUtil {

    private static final String FOLDER = "progress";

    static {
        File dir = new File(FOLDER);
        if (!dir.exists()) dir.mkdirs();
    }


    private static File getProgressFile() {
        Student s = Main.getCurrentStudent();
        if (s == null) ;
        return new File(FOLDER, s.getId() + "_progress.txt");
    }

    public static void saveGrade(String subject, double grade) {
        File file = getProgressFile();
        Properties props = new Properties();

        try {
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    props.load(reader);
                }
            }
            props.setProperty(subject, String.valueOf(grade));
            props.store(new FileWriter(file), "Progress for " + Main.getCurrentStudent().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getGrade(String subject) {
        File file = getProgressFile();
        if (!file.exists()) return 0.0;

        Properties props = new Properties();
        try (FileReader reader = new FileReader(file)) {
            props.load(reader);
            return Double.parseDouble(props.getProperty(subject, "0.0"));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static double getICSGrade() { return getGrade("ICS"); }
    public static double getSPLGrade() { return getGrade("SPL"); }
    public static double getOOPGrade() { return getGrade("OOP"); }
    public static double getAOOPGrade() { return getGrade("AOOP"); }
    public static double getDSAGrade() { return getGrade("DSA"); }

    public static double getCGPA() {
        double[] g = {getICSGrade(), getSPLGrade(), getOOPGrade(), getAOOPGrade(), getDSAGrade()};
        double sum = 0; int count = 0;
        for (double v : g) {
            if (v > 0) { sum += v; count++; }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public static void setICSGrade(double v) {
        saveGrade("ICS", v);
    }

    public static void setSPLGrade(double v) {
        saveGrade("SPL", v);
    }

    public static void setOOPGrade(double v) {
        saveGrade("OOP", v);
    }

    public static void setAOOPGrade(double v) {
        saveGrade("AOOP", v);
    }

    public static void setDSAGrade(double v) {
        saveGrade("DSA", v);
    }

    public static void saveGrades() {
        saveGrade("ICS", getICSGrade());
        saveGrade("SPL", getSPLGrade());
        saveGrade("OOP", getOOPGrade());
        saveGrade("AOOP", getAOOPGrade());
        saveGrade("DSA", getDSAGrade());
    }

}
