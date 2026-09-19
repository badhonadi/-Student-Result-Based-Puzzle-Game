package com.example.game;

import java.io.*;
import java.net.*;
import java.util.*;

public class ResultClient {

    public static void sendResult(String course, String name, String id, double grade, double cgpa) {
        try (
                Socket socket = new Socket("localhost", 5000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Format: SEND_RESULT|course,name,id,grade,cgpa
            String result = course + "," + name + "," + id + "," + grade + "," + cgpa;
            out.println("SEND_RESULT|" + result);
            System.out.println("📤 Result sent: " + result);

            String reply = in.readLine();
            if (reply != null) System.out.println("🖥️ Server reply: " + reply);

        } catch (IOException e) {
            System.err.println("❌ Could not send result to server.");
            e.printStackTrace();
        }
    }

    // 🟢 Fetch results from server
    public static List<String> fetchResults() {
        List<String> results = new ArrayList<>();
        try (
                Socket socket = new Socket("localhost", 5000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("GET_RESULTS");

            String line;
            while ((line = in.readLine()) != null && !line.equals("END")) {
                results.add(line);
            }

        } catch (IOException e) {
            System.err.println("❌ Could not fetch results from server.");
            e.printStackTrace();
        }
        return results;
    }
}
