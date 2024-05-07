package network;

import util.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

            Logger.newline();

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    byte[] map = new byte[length];
                    in.readFully(map);
                    logger.log("Receiving map");
                    networkClient.receiveMap(new String(map));
                    break;


                case 3: // Server assigns player number

                    playerNumber = in.readByte();

                    logger.log("Receiving player number " + playerNumber);
                    networkClient.receivePlayerNumber(playerNumber);
                    break;


                case 4: // Server requests move

                    int timeLimit = in.readInt();
                    byte depthLimit = in.readByte();

                    logger.log("Server requests move (time/depth) " + timeLimit + " " + depthLimit);

                    // Get move answer
                    MoveAnswer answer = networkClient.sendMoveAnswer(timeLimit, depthLimit);

                    // Write
                    out.writeByte(5); // Message type
                    out.writeInt(5); // Message length
                    out.writeShort(answer.x());
                    out.writeShort(answer.y());
                    out.writeByte(answer.type());

                    break;


                //   5: // Client sends move


                case 6: // Server sends move from other player

                    short x = in.readShort();
                    short y = in.readShort();
                    byte type = in.readByte();
                    byte player = in.readByte();

                    logger.log("Receiving move (" + x + "/" + y + ") type=" + type + " player=" +
                            player);

                    // Pass to client
                    networkClient.receiveMove(x, y, type, player);

                    break;


                case 7: // Server disqualifies

                    byte disqualifiedPlayer = in.readByte();

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
