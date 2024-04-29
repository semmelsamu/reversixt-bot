package network;

import util.Logger;
import util.LoggerUtils;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * Manages the communication between a client and a server via a network socket.
 */
public class NetworkEventHandler {

    Logger logger = new Logger(this.getClass().getName());

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private final NetworkClient networkClient;

    private byte playerNumber = 0x0;

    /**
     * Connect a networkClient to a server.
     *
     * @param networkClient The client object that handles the actual "client" work. Must
     *                      implement the {@link NetworkClient} interface.
     * @param ip            The IP address of the server to which the client will connect.
     * @param port          The port number on the server to which the client will connect.
     */
    public NetworkEventHandler(NetworkClient networkClient, String ip, int port) {

        logger.log("Starting network client");

        // Store client
        this.networkClient = networkClient;

        try {

            // Connect
            logger.log("Trying to connect to server " + ip + " on port " + port);
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            logger.log("Connected");

            // Launch
            sendGroupNumber();
            run();

        } catch (IOException e) {
            logger.error("Failed connecting to server: " + e.getMessage());
        }

        logger.log("Exiting NetworkEventHandler");
    }

    private void sendGroupNumber() throws IOException {
        logger.verbose("Sending group number to server");
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(networkClient.sendGroupNumber());
    }

    public void run() throws IOException {

        while (!socket.isClosed()) {

            logger.verbose("Waiting for server");

            int messageType = in.readByte();
            int length = in.readInt();
            byte[] message = new byte[length];
            in.readFully(message);

            logger.debug("Message (Length " + length + "): " + Arrays.toString(message));

            Logger.newline();

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    logger.log("Receiving map");
                    networkClient.receiveMap(new String(message));
                    break;


                case 3: // Server assigns player number
                    playerNumber = message[0];
                    logger.log("Receiving player number " + playerNumber);
                    networkClient.receivePlayerNumber(playerNumber);
                    break;


                case 4: // Server requests move
                    logger.log("Server requests move");

                    // Get move answer
                    MoveAnswer answer = networkClient.sendMoveAnswer();

                    // Write
                    out.writeByte(5); // Message type
                    out.writeInt(5); // Message length
                    out.writeShort(answer.x());
                    out.writeShort(answer.y());
                    out.writeByte(answer.type());

                    break;


                //   5: // Client sends move


                case 6: // Server sends move from other player

                    // Extract message byte stream to primitives
                    short x = (short) ((message[0] << 8) & 0xFF00);
                    x |= (message[1] & 0xFF);
                    short y = (short) ((message[2] << 8) & 0xFF00);
                    y |= (message[3] & 0xFF);
                    byte type = message[4];
                    byte player = message[5];

                    logger.log("Receiving move (" + x + "/" + y + ") type=" + type + " player=" +
                            player);

                    // Pass to client
                    networkClient.receiveMove(x, y, type, player);

                    break;


                case 7: // Server disqualifies

                    byte disqualifiedPlayer = message[0];
                    logger.log("Receiving disqualification of player " + disqualifiedPlayer);
                    networkClient.receiveDisqualification(disqualifiedPlayer);
                    if (disqualifiedPlayer == playerNumber) {
                        logger.error("Client got disqualified");
                        disconnect();
                    }

                    break;


                case 8: // End of phase 1

                    logger.log("Receiving end of phase 1");
                    networkClient.receiveEndingPhase1();
                    break;


                case 9: // End of phase 2 - the end.

                    logger.log("Receiving end of phase 2 - the end.");
                    networkClient.receiveEndingPhase2();
                    disconnect();
                    break;


                default:
                    logger.error("Unknown message type: " + messageType);

            }
        }
    }

    public void disconnect() throws IOException {
        logger.log("Disconnecting from server");
        socket.close();
        logger.log("Disconnected");
    }
}
