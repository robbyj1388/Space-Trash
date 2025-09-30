package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;

/**
 * HandServer
 * ----------
 * Listens for Python socket messages containing hand coordinates (x, y) as JSON.
 * Example JSON: {"x":0.123,"y":0.456}
 */
public class HandServer {

    private int port;

    public HandServer(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(this::runServer).start(); // run in separate thread
    }

    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Hand Server listening on port " + port);

            // Wait for a Python client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("Python client connected: " + clientSocket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject pos = new JSONObject(line);
                    double x = pos.getDouble("x");
                    double y = pos.getDouble("y");

                    System.out.printf("Received hand coordinates: x=%.3f, y=%.3f%n", x, y);

                    // TODO: Forward x/y to JavaFX Application Thread to move player

                } catch (Exception e) {
                    System.err.println("Invalid JSON: " + line);
                }
            }

            System.out.println("Python client disconnected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Simple main method for testing
    public static void main(String[] args) {
        HandServer server = new HandServer(5555);
        server.start();
    }
}

