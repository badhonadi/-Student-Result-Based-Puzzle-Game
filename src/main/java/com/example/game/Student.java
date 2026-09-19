package com.example.game;

import java.time.LocalDate;

public class Student {
    private String id;
    private String name;
    private String password;
    private int coins;
    private String email;
    private LocalDate birthday;

    public Student(String id, String name, String password, int coins) {
        this(id, name, password, coins, "", null);
    }

    public Student(String id, String name, String password, int coins, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.coins = coins;
        this.email = email;
        this.birthday = birthday;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public int getCoins() { return coins; }
    public String getEmail() { return email; }
    public LocalDate getBirthday() { return birthday; }

    public void setPassword(String password) { this.password = password; }
    public void setCoins(int coins) { this.coins = coins; }
    public void setEmail(String email) { this.email = email; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", coins=" + coins +
                '}';
    }
}
