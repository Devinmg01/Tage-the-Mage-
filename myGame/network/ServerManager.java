package myGame.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import tage.networking.server.GameConnectionServer;
import tage.networking.server.IClientInfo;

public class ServerManager extends GameConnectionServer<UUID> {

    // Class Variables
    private UUID clientID, remoteID;
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
                        clientID = UUID.fromString(msgTokens[1]);
                        IClientInfo clientInfo = getServerSocket().createClientInfo(senderIP, senderPort);
                        addClient(clientInfo, clientID);
                        System.out.println("Join request received from - " + clientID.toString());
                        sendJoinedMessage(clientID, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "bye": // Message Format: bye,clientID
                    clientID = UUID.fromString(msgTokens[1]);
                    System.out.println("Exit request received from - " + clientID.toString());
                    sendByeMessages(clientID);
                    removeClient(clientID);
                    break;
                case "create": // Message Format: create,clientID,skin,x,y,z
                    clientID = UUID.fromString(msgTokens[1]);
                    skin = Integer.parseInt(msgTokens[2]);
                    position = new String[] { msgTokens[3], msgTokens[4], msgTokens[5] };
                    sendCreateMessages(clientID, skin, position);
                    sendWantsDetailsMessages(clientID);
                    break;
                case "dsfr": // Message Format: dsfr,remoteID,clientID,skin,x,y,z
                    remoteID = UUID.fromString(msgTokens[1]);
                    clientID = UUID.fromString(msgTokens[2]);
                    skin = Integer.parseInt(msgTokens[3]);
                    position = new String[] { msgTokens[4], msgTokens[5], msgTokens[6] };
                    sendDetailsForMessage(remoteID, clientID, skin, position);
                    break;
                case "move": // Message Format: move,clientID,x,y,z
                    clientID = UUID.fromString(msgTokens[1]);
                    position = new String[] { msgTokens[2], msgTokens[3], msgTokens[4] };
                    sendMoveMessages(clientID, position);
                    break;
            }
        }
    }

    /**
     * Informs the client who requested to join that their request was successful
     * Message Format: join
     */
    public void sendJoinedMessage(UUID clientID, boolean success) {
        try {
            String message = success ? "join,success" : "join,failure";
            sendPacket(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the other clients that the client with the specified identifier has
     * left the server
     * Message Format: bye,clientID
     */
    public void sendByeMessages(UUID clientID) {
        try {
            String message = new String("bye," + clientID.toString());
            forwardPacketToAll(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the other clients that a new avatar has joined the server with the
     * specified identifier
     * Message Format: create,clientID,skin,x,y,z
     */
    public void sendCreateMessages(UUID clientID, int skin, String[] position) {
        try {
            String message = new String("create," + clientID.toString() + "," + skin);
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs a client of the details for a remote client's avatar
     * Message Format: dsfr,remoteID,skin,x,y,z
     */
    public void sendDetailsForMessage(UUID clientID, UUID remoteID, int skin, String[] position) {
        try {
            String message = new String("dsfr," + remoteID.toString() + "," + skin);
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            sendPacket(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs client that a remote client wants the local client's avatar's
     * information
     * Message Format: wsds,clientID
     */
    public void sendWantsDetailsMessages(UUID clientID) {
        try {
            String message = new String("wsds," + clientID.toString());
            forwardPacketToAll(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs client that a remote client's avatar has changed position
     * Message Format: move,clientID,x,y,z
     */
    public void sendMoveMessages(UUID clientID, String[] position) {
        try {
            String message = new String("move," + clientID.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
