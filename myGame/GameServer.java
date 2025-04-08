package myGame;

import java.io.IOException;
import myGame.network.ServerManager;

public class GameServer {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java GameServer <port>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            new ServerManager(port); // Server starts and runs in the background
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
