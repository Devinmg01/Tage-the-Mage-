package myGame.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

import myGame.entity.Enemy;
import tage.networking.client.GameConnectionClient;
import org.joml.*;
import myGame.GameClient;

public class ClientManager extends GameConnectionClient {

    // Class Variables
    private GameClient game;
    private GhostManager ghostManager;
    private UUID clientId, remoteId, enemyId;
    private Vector3f position;

    public ClientManager(InetAddress serverAddr, int serverPort, GameClient game) throws IOException {
        super(serverAddr, serverPort, ProtocolType.UDP);
        this.game = game;
        this.clientId = UUID.randomUUID(); // Unique ID for this client
        this.ghostManager = game.getGhostManager();
    }

    @Override
    protected void processPacket(Object message) {
        String msg = (String) message;
        String[] msgTokens = msg.split(",");
        if (msgTokens.length > 0 && !msgTokens[0].equals("move") && !msgTokens[0].equals("enemy_spawn")) {
            System.out.println("Message received: " + msg);
        }

        if (msgTokens.length > 0) {
            switch (msgTokens[0]) {
                case "join": // Message Format: join, successful or join,failure
                    String status = msgTokens[1];
                    if (status.equals("success")) {
                        System.out.println("Join successful");
                        game.setIsClientConnected(true);
                    } else {
                        System.out.println("Join failed");
                    }
                    break;
                case "bye": // Message Format: bye,remoteId
                    remoteId = UUID.fromString(msgTokens[1]);
                    ghostManager.removeGhost(remoteId);
                    break;
                case "create": // Message Format: create,remoteID,skin,x,y,z
                case "dsfr": // Message Format: dsfr,remoteID,skin,x,y,z
                    remoteId = UUID.fromString(msgTokens[1]);
                    int skin = Integer.parseInt(msgTokens[2]);
                    position = new Vector3f(
                            Float.parseFloat(msgTokens[3]),
                            Float.parseFloat(msgTokens[4]),
                            Float.parseFloat(msgTokens[5]));
                    ghostManager.createGhost(remoteId, skin, position);
                    break;
                case "wsds": // Message Format: wsds,remoteId
                    remoteId = UUID.fromString(msgTokens[1]);
                    sendDetailsForMessage(remoteId, 0, game.getAvatar().getWorldLocation());
                    break;
                case "move": // Message Format: move,remoteID,x,y,z
                    remoteId = UUID.fromString(msgTokens[1]);
                    position = new Vector3f(
                            Float.parseFloat(msgTokens[2]),
                            Float.parseFloat(msgTokens[3]),
                            Float.parseFloat(msgTokens[4]));
                    ghostManager.updateGhost(remoteId, position);
                    break;
                case "enemy_spawn": // Format: enemy_spawn,enemyID,x,y,z
                    enemyId = UUID.fromString(msgTokens[1]);
                    position = new Vector3f(
                            Float.parseFloat(msgTokens[2]),
                            Float.parseFloat(msgTokens[3]),
                            Float.parseFloat(msgTokens[4]));
                    game.getEnemyManager().spawnEnemy(enemyId, position);
                    break;
                case "all_enemies": // Format: all_enemies,remoteID
                    remoteId = UUID.fromString(msgTokens[1]);
                    sendAllEnemies(remoteId);
                    break;
            }
        }
    }

    /**
     * Returns the ID of the client.
     */
    public UUID getId() {
        return clientId;
    }

    /**
     * Message to the server requesting to join the server
     * Message Format: join,clientID
     */
    public void sendJoinMessage() {
        try {
            sendPacket(new String("join," + clientId.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it that the client is leaving the server
     * Message Format: bye,clientID
     */
    public void sendByeMessage() {
        try {
            sendPacket(new String("bye," + clientId.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it of the client's avatar's position
     * Message Format: create,clientID,skin,x,y,z
     */
    public void sendCreateMessage(int skin, Vector3f position) {
        try {
            String message = new String("create," + clientId.toString() + "," + skin);
            message += "," + position.x;
            message += "," + position.y;
            message += "," + position.z;
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it of the local avatar's position
     * Message Format: dsfr,remoteID,clientID,skin,x,y,z
     */
    public void sendDetailsForMessage(UUID remoteId, int skin, Vector3f position) {
        try {
            String message = new String("dsfr," + remoteId.toString() + "," + clientId.toString() + "," + skin);
            message += "," + position.x;
            message += "," + position.y;
            message += "," + position.z;
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it that the local avatar has changed position
     * Message Format: move,clientID,x,y,z
     */
    public void sendMoveMessage(Vector3f position) {
        try {
            String message = new String("move," + clientId.toString());
            message += "," + position.x;
            message += "," + position.y;
            message += "," + position.z;
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it that a new enemy has spawned
     * Message Format: enemy_spawn,clientID,enemyID,x,y,z
     */
    public void sendEnemySpawn(UUID enemyId, Vector3f spawnLoc) {
        try {
            String message = String.format("enemy_spawn," + clientId.toString());
                message += "," + enemyId.toString();
                message += "," + spawnLoc.x;
                message += "," + spawnLoc.y;
                message += "," + spawnLoc.z;
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to a client that is requesting the positions of all the enemies
     * Message Format: all_enemies,remoteID,
     */
    public void sendAllEnemies(UUID remoteId) {
        ArrayList<Enemy> enemies = game.getEnemyManager().getEnemies();
        for (Enemy enemy : enemies) {
            try {
                UUID enemyId = enemy.getId();
                Vector3f enemyPos = enemy.getWorldLocation();
                String message = String.format("enemy_spawn_id," + remoteId.toString());
                message += "," + enemyId.toString();
                message += "," + enemyPos.x;
                message += "," + enemyPos.y;
                message += "," + enemyPos.z;
                sendPacket(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}