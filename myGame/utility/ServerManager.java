package myGame.utility;

import myGame.entity.Enemy;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;
import tage.networking.server.GameConnectionServer;
import tage.networking.server.IClientInfo;

public class ServerManager extends GameConnectionServer<UUID> {

    // Class Variables
    private UUID clientId, remoteId, enemyId;
    private String[] position;
    private int skin;

    /**
     * Construct GameServer with the specified port
     */
    public ServerManager(int port) throws IOException {
        super(port, ProtocolType.UDP);
    }

    /**
     * Process incoming packets from clients
     */
    @Override
    public void processPacket(Object message, InetAddress senderIP, int senderPort) {
        String msg = (String) message;
        String[] msgTokens = msg.split(",");

        if (msgTokens.length > 0) {
            switch (msgTokens[0]) {
                case "join": // Message Format: join,clientID
                    try {
                        clientId = UUID.fromString(msgTokens[1]);
                        IClientInfo clientInfo = getServerSocket().createClientInfo(senderIP, senderPort);
                        addClient(clientInfo, clientId);
                        System.out.println("Join request received from - " + clientId.toString());
                        sendJoinedMessage(clientId, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "bye": // Message Format: bye,clientID
                    clientId = UUID.fromString(msgTokens[1]);
                    System.out.println("Exit request received from - " + clientId.toString());
                    sendByeMessages(clientId);
                    removeClient(clientId);
                    break;
                case "create": // Message Format: create,clientID,skin,x,y,z
                    clientId = UUID.fromString(msgTokens[1]);
                    skin = Integer.parseInt(msgTokens[2]);
                    position = new String[] { msgTokens[3], msgTokens[4], msgTokens[5] };
                    sendCreateMessages(clientId, skin, position);
                    sendWantsDetailsMessages(clientId);
                    sendWantsEnemiesMessage(clientId);
                    break;
                case "dsfr": // Message Format: dsfr,remoteID,clientID,skin,x,y,z
                    remoteId = UUID.fromString(msgTokens[1]);
                    clientId = UUID.fromString(msgTokens[2]);
                    skin = Integer.parseInt(msgTokens[3]);
                    position = new String[] { msgTokens[4], msgTokens[5], msgTokens[6] };
                    sendDetailsForMessage(remoteId, clientId, skin, position);
                    break;
                case "move": // Message Format: move,clientID,x,y,z
                    clientId = UUID.fromString(msgTokens[1]);
                    position = new String[] { msgTokens[2], msgTokens[3], msgTokens[4] };
                    sendMoveMessages(clientId, position);
                    break;
                case "enemy_spawn": // Format: enemy_spawn,clientID,enemyId,x,y,z
                    clientId = UUID.fromString(msgTokens[1]);
                    enemyId = UUID.fromString(msgTokens[2]);
                    position = new String[] { msgTokens[3], msgTokens[4], msgTokens[5] };
                    sendSpawnEnemyMessage(clientId, enemyId, position);
                    break;
                case "enemy_dies": // Format: enemy_dies,enemyId
                    enemyId = UUID.fromString(msgTokens[1]);
                    sendEnemyDiesMessage(enemyId);
                    break;
                case "enemy_spawn_id": // Format: enemy_spawn_id,remoteID,enemyId,x,y,z
                    remoteId = UUID.fromString(msgTokens[1]);
                    enemyId = UUID.fromString(msgTokens[2]);
                    position = new String[] { msgTokens[3], msgTokens[4], msgTokens[5] };
                    sendSpawnEnemyMessageToId(remoteId, enemyId, position);
                    break;
            }
        }
    }

    /**
     * Informs the client who requested to join that their request was successful
     * Message Format: join
     */
    public void sendJoinedMessage(UUID clientId, boolean success) {
        try {
            String message = success ? "join,success" : "join,failure";
            sendPacket(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the other clients that the client with the specified identifier has
     * left the server
     * Message Format: bye,clientID
     */
    public void sendByeMessages(UUID clientId) {
        try {
            String message = new String("bye," + clientId.toString());
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the other clients that a new avatar has joined the server with the
     * specified identifier
     * Message Format: create,clientID,skin,x,y,z
     */
    public void sendCreateMessages(UUID clientId, int skin, String[] position) {
        try {
            String message = new String("create," + clientId.toString() + "," + skin);
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs a client of the details for a remote client's avatar
     * Message Format: dsfr,remoteID,skin,x,y,z
     */
    public void sendDetailsForMessage(UUID clientId, UUID remoteId, int skin, String[] position) {
        try {
            String message = new String("dsfr," + remoteId.toString() + "," + skin);
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            sendPacket(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs client that a remote client wants the local client's avatar's
     * information
     * Message Format: wsds,clientID
     */
    public void sendWantsDetailsMessages(UUID clientId) {
        try {
            String message = new String("wsds," + clientId.toString());
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs client that a remote client's avatar has changed position
     * Message Format: move,clientID,x,y,z
     */
    public void sendMoveMessages(UUID clientId, String[] position) {
        try {
            String message = new String("move," + clientId.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs clients that a remote client wants the details for all the enemies
     * Message Format: enemy_request, clientID
     */
    public void sendWantsEnemiesMessage(UUID clientId) {
        try {
            String message = new String("all_enemies," + clientId.toString());
            remoteId = null;
            for (UUID client : getClients().keySet()) {
                if (!client.equals(clientId)) {
                    remoteId = client;
                    break;
                }
            }
            if (remoteId != null) {
                sendPacket(message, remoteId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the clients that a new enemy has spawned
     * Message Format: enemy_spawn,clientID,enemyID,x,y,z
     */
    public void sendSpawnEnemyMessage(UUID clientId, UUID enemyId, String[] position) {
        try {
            String message = new String("enemy_spawn," + enemyId.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the clients that an enemy has died
     * Message Format: enemy_dies,enemyID
     */
    public void sendEnemyDiesMessage(UUID enemyId) {
        try {
            String message = new String("enemy_dies," + enemyId.toString());
            forwardPacketToAll(message, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs a specific client that a new enemy has spawned
     * Message Format: enemy_spawn_id,remoteID,enemyId,x,y,z
     */
    public void sendSpawnEnemyMessageToId(UUID remoteId, UUID enemyId, String[] position) {
        try {
            String message = new String("enemy_spawn," + enemyId.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            sendPacket(message, remoteId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
