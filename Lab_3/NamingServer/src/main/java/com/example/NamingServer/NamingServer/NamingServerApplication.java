package com.example.NamingServer.NamingServer;

import com.example.NamingServer.NamingServer.controller.networkServer.ServerMulticastReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamingServerApplication.class, args);

		// Start multicastreceiver in aparte thread
		new Thread(() -> {
			try {
				ServerMulticastReceiver.listen();
			} catch (Exception e) {
				System.err.println("Fout bij starten van multicastreceiver:");
				e.printStackTrace();
			}
		}).start();
	}
}
