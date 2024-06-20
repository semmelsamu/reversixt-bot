package network;

import exceptions.ClientDisqualifiedException;
import exceptions.NetworkException;
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

    private NetworkClient client;

    private byte playerNumber = 0x0;

    /**
     * Connect to a server.
     *
     * @param ip   The IP address of the server to which the client will connect.
     * @param port The port number on the server to which the client will connect.
     */
    public void connect(String ip, int port) {
        logger.log("Attempting to connect to server " + ip + " on port " + port);

        try {
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            logger.log("Connected");
        }
        catch (IOException e) {
            logger.error("Failed connecting to server: " + e.getMessage());

        }

    }

    /**
     * Launch a client on the network.
     *
     * @param client The client object.
     */
    public void launch(NetworkClient client) {

        this.client = client;

        try {
            sendGroupNumber();
            run();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        finally {
            this.client.exit();
        }

    }

    public void disconnect() {

        logger.log("Disconnecting from server");

        try {
            socket.close();
            logger.log("Disconnected");
        }
        catch (IOException e) {
            logger.error("Failed disconnecting from server: " + e.getMessage());
        }

    }

    private void sendGroupNumber() throws IOException {
        logger.verbose("Sending group number to server");
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(client.sendGroupNumber());
    }

    public void run() throws IOException, NetworkException, ClientDisqualifiedException {

        boolean isRunning = true;

        while (isRunning) {

            logger.debug("Waiting for server");

            int messageType = in.readByte();
            int length = in.readInt();

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    byte[] map = new byte[length];
                    in.readFully(map);
                    logger.log("Receiving map");
                    client.receiveMap(new String(map));
                    break;


                case 3: // Server assigns player number

                    playerNumber = in.readByte();

                    logger.log("Received player number " + playerNumber);
                    client.receivePlayerNumber(playerNumber);
                    break;


                case 4: // Server requests move

                    int timeLimit = in.readInt();
                    byte depthLimit = in.readByte();

                    logger.log("Server requests move (time/depth) " + timeLimit + " " + depthLimit);

                    // Get move answer
                    MoveAnswer answer = client.sendMoveAnswer(timeLimit, depthLimit);

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

                    logger.log("Received move (" + x + "/" + y + ") type=" + type + " player=" +
                            player);

                    // Pass to client
                    client.receiveMove(x, y, type, player);

                    break;


                case 7: // Server disqualifies

                    byte disqualifiedPlayer = in.readByte();

                    logger.log("Received disqualification of player " + disqualifiedPlayer);
                    client.receiveDisqualification(disqualifiedPlayer);

                    if (disqualifiedPlayer == playerNumber) {
                        throw new ClientDisqualifiedException("Client got disqualified");
                    }

                    break;


                case 8: // End of phase 1

                    logger.log("Received end of phase 1");
                    client.receiveEndingPhase1();
                    break;


                case 9: // End of phase 2 - the end.

                    logger.log("Received end of phase 2 - the end.");
                    client.receiveEndingPhase2();
                    isRunning = false;
                    break;


                default:
                    throw new NetworkException("Unknown message type: " + messageType);


            }

        }
    }
}
