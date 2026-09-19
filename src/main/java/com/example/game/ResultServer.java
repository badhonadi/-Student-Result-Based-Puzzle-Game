package com.example.game;

import java.io.*;
import java.net.*;
import java.util.*;

public class ResultServer {
    private static final List<String> results = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println(" Result Server started on port 5000...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String command = in.readLine();

            if (command == null) return;

            if (command.startsWith("SEND_RESULT|")) {
                String result = command.substring("SEND_RESULT|".length());
                results.add(result);
                System.out.println(" New result stored: " + result);
                out.println("RESULT_SAVED");
            }
            
            else if (command.equals("GET_RESUL" +
                    "TS")) {
                for (String res : results) {
                    out.println(res);
                }
                out.println("END");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
