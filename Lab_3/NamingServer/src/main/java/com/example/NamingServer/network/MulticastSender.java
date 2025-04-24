package com.example.NamingServer.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastSender {

    // Deze methode stuurt een multicastbericht met de naam en IP van de node
    public static void sendMulticast(String nodeName, String ipAddress) throws Exception {

        // Combineer de gegevens in één string (gescheiden door komma)
        String message = nodeName + "," + ipAddress;

        // Multicast IP-adres (moet gekozen worden tussen 224.0.0.0 en 239.255.255.255)
        InetAddress group = InetAddress.getByName("230.0.0.0");

        // De poort waar iedereen naar zal luisteren
        int port = 4446;

        // Maak een UDP socket (gebruikt voor verzenden van het bericht)
        DatagramSocket socket = new DatagramSocket();

        // Zet het bericht om naar een byte-array (UDP-pakketten gebruiken bytes)
        byte[] buf = message.getBytes();

        // Maak een UDP-pakket dat naar de multicast-groep en poort gestuurd wordt
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);

        // Verstuur het pakket via het netwerk
        socket.send(packet);

        // Sluit de socket (verplicht om resources vrij te geven)
        socket.close();

        // Debug: bevestiging tonen in de console
        System.out.println("Multicast sent: " + message);
    }
}
