package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HandServer
 * ----------
 * Listens for Python socket messages containing hand coordinates (x, y) as JSON.
 * Example JSON: {"x":0.123,"y":0.456}
 */
public class HandServer {

    private int port;

    // Store left hand coordinates (volatile for thread safety)
    public volatile double lx;
    public volatile double ly;

    // Store right hand coordinates (volatile for thread safety)
    public volatile double rx;
    public volatile double ry;

    ServerSocket serverSocket;

    public HandServer(int port) {
        this.port = port;
    }

    /** Starts the server on a background thread */
    public void start() {
        new Thread(this::runServer).start();
    }

    /** Handles incoming socket connections and messages */
    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Hand Server listening on port " + port);
            openServerProgram();

            // Wait for a Python client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("Python client connected: " + clientSocket.getInetAddress());

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        double lx = Double.parseDouble(parts[0]);
                        double ly = Double.parseDouble(parts[1]);
                        double rx = Double.parseDouble(parts[2]);
                        double ry = Double.parseDouble(parts[3]);

                        // Update coordinates
                        
                        setLeftX(lx);
                        setLeftY(ly);
                        setRightX(rx);
                        setRightY(ry);
                    } else {
                        System.err.println("Invalid data (expected 4 parts): " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }

            System.out.println("Python client disconnected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLeftX(double x) { lx = x; }
    public void setLeftY(double y) { ly = y; }
    public void setRightX(double x) { rx = x; }
    public void setRightY(double y) { ry = y; }

    public void openServerProgram() {
        try {
            System.out.println("Attempting start of Python program");
            Process p = Runtime.getRuntime().exec(new String[]{"python3.10 hand_tracking.py"});


            BufferedReader stdInput = new BufferedReader( new InputStreamReader(p.getInputStream()));

            String s = null;
            while((s =stdInput.readLine()) != null) {
                System.out.println(s);
            }


        } catch (IOException e) {
            System.out.println("Error: Could not automatically run hand_tracking, ensure correct Python version is installed (python3.10?)!");
            System.out.println(e);
            //TODO: Ensure that is right
            //e.printStackTrace();
        }
    }
}