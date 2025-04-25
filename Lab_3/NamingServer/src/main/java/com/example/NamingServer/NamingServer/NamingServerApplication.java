package com.example.NamingServer.NamingServer;

import com.example.NamingServer.NamingServer.controller.networkServer.ServerMulticastReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main entry point of the Spring Boot application
@SpringBootApplication
public class NamingServerApplication {

	public static void main(String[] args) {
		// Start the Spring Boot application
		SpringApplication.run(NamingServerApplication.class, args);

		// After the application has started, also launch a separate thread
		// to listen for multicast messages from nodes that want to join the network
		new Thread(() -> {
			try {
				ServerMulticastReceiver.listen(); // continuously listens on multicast group and port
			} catch (Exception e) {
				System.err.println("Error while starting the multicast receiver:");
				e.printStackTrace();
			}
		}).start();
	}
}
