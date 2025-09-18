package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.JSONObject;

public class HandTrackingListener {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5555)) {  // Change host/port as needed
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject pos = new JSONObject(line);
                double x = pos.getDouble("x");
                double y = pos.getDouble("y");

                System.out.printf("Received coordinates: x=%.2f, y=%.2f%n", x, y);

                // TODO: Send x/y to JavaFX thread safely to move player
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
