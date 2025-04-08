package myGame.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import tage.networking.client.GameConnectionClient;
import org.joml.*;
import myGame.GameClient;

public class ClientManager extends GameConnectionClient {

    // Class Variables
    private GameClient game;
    private GhostManager ghostManager;
    private UUID clientID, ghostID;
    private Vector3f position;

    public ClientManager(InetAddress serverAddr, int serverPort, GameClient game) throws IOException {
        super(serverAddr, serverPort, ProtocolType.UDP);
        this.game = game;
        this.clientID = UUID.randomUUID(); // Unique ID for this client
        this.ghostManager = game.getGhostManager();
    }

    @Override
    protected void processPacket(Object message) {
        String msg = (String) message;
        System.out.println("message received -->" + msg);
        String[] msgTokens = msg.split(",");

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
                    ghostID = UUID.fromString(msgTokens[1]);
                    ghostManager.removeGhost(ghostID);
                    break;
                case "create": // Message Format: create,remoteID,skin,x,y,z
                case "dsfr": // Message Format: dsfr,remoteID,skin,x,y,z
                    ghostID = UUID.fromString(msgTokens[1]);
                    int skin = Integer.parseInt(msgTokens[2]);
                    position = new Vector3f(
                            Float.parseFloat(msgTokens[3]),
                            Float.parseFloat(msgTokens[4]),
                            Float.parseFloat(msgTokens[5]));
                    ghostManager.createGhost(ghostID, skin, position);
                    break;
                case "wsds": // Message Format: wsds,remoteId
                    ghostID = UUID.fromString(msgTokens[1]);
                    sendDetailsForMessage(ghostID, 0, game.getAvatar().getWorldLocation());
                    break;
                case "move": // Message Format: move,remoteID,x,y,z
                    ghostID = UUID.fromString(msgTokens[1]);
                    position = new Vector3f(
                            Float.parseFloat(msgTokens[2]),
                            Float.parseFloat(msgTokens[3]),
                            Float.parseFloat(msgTokens[4]));
                    ghostManager.updateGhost(ghostID, position);
                    break;
            }
        }
    }

    /**
     * Returns the ID of the client.
     */
    public UUID getID() {
        return clientID;
    }

    /**
     * Message to the server requesting to join the server
     * Message Format: join,clientID
     */
    public void sendJoinMessage() {
        try {
            sendPacket(new String("join," + clientID.toString()));
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
            sendPacket(new String("bye," + clientID.toString()));
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
            String message = new String("create," + clientID.toString() + "," + skin);
            message += "," + position.x();
            message += "," + position.y();
            message += "," + position.z();
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
            String message = new String("dsfr," + remoteId.toString() + "," + clientID.toString() + "," + skin);
            message += "," + position.x();
            message += "," + position.y();
            message += "," + position.z();
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message to the server informing it that the local avatar has changed position
     * Message Format: move,localId,x,y,z
     */
    public void sendMoveMessage(Vector3f position) {
        try {
            String message = new String("move," + clientID.toString());
            message += "," + position.x();
            message += "," + position.y();
            message += "," + position.z();
            sendPacket(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}