package com.example.NamingServer.NamingServer.controller.networkServer;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

// This class listens for multicast messages sent by nodes that want to register with the Naming Server
public class ServerMulticastReceiver {

    private static final int PORT = 4446; // Multicast port
    private static final String MULTICAST_IP = "230.0.0.0"; // Multicast group address

    // Main method that continuously listens for new multicast messages
    public static void listen() throws Exception {
        MulticastSocket socket = new MulticastSocket(PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group); // Join the multicast group

        System.out.println("Naming Server is listening for multicast discovery...");

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // Wait for a multicast packet
            socket.receive(packet);

            // Convert packet content to string
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received multicast: " + received);

            // Expected format: name,ip
            String[] parts = received.split(",");
            if (parts.length != 2) continue; // Skip if format is invalid

            String nodeName = parts[0];
            String nodeIP = parts[1];

            // Create a JSON string to send in the POST request body
            String jsonBody = String.format("{\"name\":\"%s\", \"ipAddress\":\"%s\"}", nodeName, nodeIP);

            // Send a POST request to the local Naming Server to register the node
            sendPostToAddNode(jsonBody);

            // Optional: you could send a response back to the sender here if needed
        }
    }

    // Helper method to send an HTTP POST request to the /addNode endpoint
    private static void sendPostToAddNode(String jsonBody) {
        try {
            URL url = new URL("http://localhost:8080/addNode");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Write the JSON body to the request output stream
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the HTTP response code
            int code = con.getResponseCode();
            System.out.println("POST /addNode → status " + code);
        } catch (Exception e) {
            System.err.println("Error while sending POST to /addNode:");
            e.printStackTrace();
        }
    }
}
