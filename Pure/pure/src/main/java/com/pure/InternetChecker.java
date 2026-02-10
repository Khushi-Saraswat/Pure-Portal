package com.pure;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetChecker {

    /**
     * Checks if the application has an active internet connection.
     * This method performs a network operation and should NOT be run on the JavaFX
     * Application Thread.
     * 
     * @return true if connected, false otherwise.
     */
    public static boolean isInternetAvailable() {
        try (Socket socket = new Socket()) {
            // Try to connect to a well-known host (e.g., Google's DNS server)
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 3000); // 3 second timeout
            return true;
        } catch (IOException e) {
            // Connection attempt failed
            return false;
        }
    }

}
