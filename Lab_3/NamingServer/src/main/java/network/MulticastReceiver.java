package network;

import java.net.*;
import java.util.TreeMap;

public class MulticastReceiver {

    // Multicast poort en IP waar iedereen op luistert
    private static final int PORT = 4446;
    private static final String MULTICAST_IP = "230.0.0.0";

    // Map die de Naming Server bijhoudt: hash → IP
    private static TreeMap<Integer, String> nodeMap = new TreeMap<>();

    // Hashfunctie: maakt van een node-naam een getal tussen 0 en 32768
    public static int hashNodeName(String nodeName) {
        int hash = 0;
        for (int i = 0; i < nodeName.length(); i++) {
            hash = 31 * hash + nodeName.charAt(i); // klassieke hash-opbouw
        }
        return Math.abs(hash) % 32768;
    }

    // Methode om te luisteren naar multicastberichten
    public static void listen() throws Exception {

        // Open een socket die berichten ontvangt op poort 4446
        MulticastSocket socket = new MulticastSocket(PORT);

        // Zeg dat je wil luisteren naar berichten op multicast IP
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group);

        System.out.println("Naming Server is listening for multicast discovery...");

        // Eindeloze lus om continu berichten op te vangen
        while (true) {
            byte[] buf = new byte[256]; // buffer voor binnenkomend bericht
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // Wacht tot er een bericht aankomt
            socket.receive(packet);

            // Haal de inhoud van het pakket uit de byte-array
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received multicast message: " + received);

            // Split het bericht: verwacht formaat "nodeName,ipAddress"
            String[] parts = received.split(",");
            if (parts.length != 2) continue; // verkeerd formaat? negeer het

            String nodeName = parts[0];
            String nodeIP = parts[1];

            // Bereken de hash van de node
            int hash = hashNodeName(nodeName);

            // Voeg de nieuwe node toe aan de map
            nodeMap.put(hash, nodeIP);
            System.out.println("→ Registered: " + nodeName + " (hash=" + hash + ")");

            // Bereken hoeveel nodes er al waren voor deze toegevoegd werd
            String response = "Current node count: " + (nodeMap.size() - 1);

            // Bepaal naar welk IP en poort je het antwoord moet terugsturen
            InetAddress targetAddress = packet.getAddress(); // IP of the sender = new client
            int targetPort = packet.getPort(); // poort van de zender

            // Maak een antwoordpakket en stuur dat terug naar de afzender
            DatagramPacket reply = new DatagramPacket(
                    response.getBytes(), response.length(), targetAddress, targetPort
            );
            socket.send(reply);
        }
    }
}
