package com.example;
import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;



public class PSMoveClient extends WebSocketClient {

    public PSMoveClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to PSMoveService");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received data: " + message);
        try {
            // Parse the message as a JSON object
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();

            // Process the sensor data from the JSON object
            // Assuming the message has a "sensor" field
            String sensorData = json.get("sensor").getAsString();
            System.out.println("Sensor Data: " + sensorData);
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse message as JSON");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://localhost:9512"); // Example WebSocket URI (adjust as needed)
            PSMoveClient client = new PSMoveClient(uri);
            client.connect();git 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
