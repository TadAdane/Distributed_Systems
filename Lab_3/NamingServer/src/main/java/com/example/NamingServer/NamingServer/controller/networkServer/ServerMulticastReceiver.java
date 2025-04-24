package com.example.NamingServer.NamingServer.controller.networkServer;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerMulticastReceiver {

    private static final int PORT = 4446;
    private static final String MULTICAST_IP = "230.0.0.0";

    public static void listen() throws Exception {
        MulticastSocket socket = new MulticastSocket(PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group);

        System.out.println("Naming Server is listening for multicast discovery...");

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received multicast: " + received);

            String[] parts = received.split(",");
            if (parts.length != 2) continue;

            String nodeName = parts[0];
            String nodeIP = parts[1];

            // Maak JSON-body
            String jsonBody = String.format("{\"name\":\"%s\", \"ipAddress\":\"%s\"}", nodeName, nodeIP);

            // Stuur POST-verzoek naar lokale Naming Server
            sendPostToAddNode(jsonBody);

//            // Optioneel: stuur response terug naar de node (zoals je al had)
//            String response = "Registered via /addNode";
//            InetAddress targetAddress = packet.getAddress();
//            int targetPort = packet.getPort();
//
//            DatagramPacket reply = new DatagramPacket(
//                    response.getBytes(), response.length(), targetAddress, targetPort
//            );
//            socket.send(reply);
        }
    }

    private static void sendPostToAddNode(String jsonBody) {
        try {
            URL url = new URL("http://localhost:8080/addNode");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Schrijf JSON naar body
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("POST /addNode → status " + code);
        } catch (Exception e) {
            System.err.println("Fout bij POST /addNode:");
            e.printStackTrace();
        }
    }
}
