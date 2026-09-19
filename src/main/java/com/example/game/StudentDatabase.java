package com.example.game;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class StudentDatabase {
    private static final String FILE_NAME = System.getProperty("user.dir") + File.separator + "students.txt";

    public static Student register(String name, String password, String email, LocalDate birthday) {
        Map<String, Student> map = loadMap();

        for (Student s : map.values()) {
            if (s.getName().equalsIgnoreCase(name) || s.getEmail().equalsIgnoreCase(email)) {
                return null;
            }
        }

        String id;
        Random r = new Random();
        do {
            int num = 1000 + r.nextInt(9000);
            id = "011241" + num;
        } while (map.containsKey(id));

        Student st = new Student(id, name, password, 500, email, birthday);

        map.put(id, st);
        saveMap(map);
        System.out.println(" Student registered: " + st.getId());
        return st;
    }

    public static Student login(String id, String password) {
        Map<String, Student> map = loadMap();
        Student s = map.get(id);
        if (s != null && s.getPassword().equals(password)) return s;
        return null;
    }

    public static boolean updatePassword(String id, String newPassword) {
        Map<String, Student> map = loadMap();
        Student s = map.get(id);
        if (s == null) return false;
        s.setPassword(newPassword);
        map.put(id, s);
        saveMap(map);
        return true;
    }

    public static void updateCoins(Student student) {
        Map<String, Student> map = loadMap();
        if (map.containsKey(student.getId())) {
            map.put(student.getId(), student);
            saveMap(map);
        }
    }


    private static Map<String, Student> loadMap() {
        Map<String, Student> map = new LinkedHashMap<>();
        File f = new File(FILE_NAME);

        if (!f.exists()) {
            try {
                if (f.createNewFile()) {
                    System.out.println("[📁] Created new student file: " + FILE_NAME);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }

        // ✅ Read file safely
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 6) {
                    String id = p[0];
                    String name = p[1];
                    String pass = p[2];
                    int coins = Integer.parseInt(p[3]);
                    String email = p[4];
                    LocalDate birthday = p[5].isEmpty() ? null : LocalDate.parse(p[5]);
                    map.put(id, new Student(id, name, pass, coins, email, birthday));
                } else if (p.length >= 4) {
                    String id = p[0];
                    String name = p[1];
                    String pass = p[2];
                    int coins = Integer.parseInt(p[3]);
                    map.put(id, new Student(id, name, pass, coins));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void saveMap(Map<String, Student> map) {
        File f = new File(FILE_NAME);

        // ✅ Ensure file exists before writing
        try {
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
            for (Student s : map.values()) {
                String birthdayStr = (s.getBirthday() != null) ? s.getBirthday().toString() : "";
                String emailStr = (s.getEmail() != null) ? s.getEmail() : "";
                bw.write(String.join(",", s.getId(), s.getName(), s.getPassword(),
                        String.valueOf(s.getCoins()), emailStr, birthdayStr));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
