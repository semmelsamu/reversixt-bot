package network;

import util.Logger;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * Manages the communication between a client and a server via a network socket.
 */
public class NetworkEventHandler {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    private final NetworkClient networkClient;

    private byte playerNumber = 0x0;

    /**
     * Connect a networkClient to a server.
     *
     * @param networkClient The client object that handles the actual "client" work. Must
     *                      implement the {@link NetworkClient} interface.
     * @param ip            The IP address of the server to which the client will connect.
     * @param port          The port number on the server to which the client will connect.
     * @throws IOException If at some point the connection to the server fails.
     */
    public NetworkEventHandler(NetworkClient networkClient, String ip, int port)
            throws IOException {

        Logger.get().log("Starting network client");

        // Store client
        this.networkClient = networkClient;

        // Connect
        Logger.get().log("Trying to connect to server " + ip + " on port " + port);
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        Logger.get().log("Connected");

        // Launch
        sendGroupNumber();
        run();

        Logger.get().log("Exiting NetworkEventHandler");
    }

    private void sendGroupNumber() throws IOException {
        Logger.get().log("Sending group number to server");
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(networkClient.sendGroupNumber());
    }

    public void run() throws IOException {

        while (!socket.isClosed()) {

            Logger.get().log("Waiting for server");

            int messageType = in.readByte();
            int length = in.readInt();
            byte[] message = new byte[length];
            in.readFully(message);

            Logger.get().log("Server responded with code " + messageType);
            Logger.get().log("Message length: " + length);
            Logger.get().log("Body: " + Arrays.toString(message));

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    networkClient.receiveMap(new String(message));
                    break;


                case 3: // Server assigns player number
                    playerNumber = message[0];
                    networkClient.receivePlayerNumber(playerNumber);
                    break;


                case 4: // Server requests move

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

                    // Pass to client
                    networkClient.receiveMove(x, y, type, player);

                    break;


                case 7: // Server disqualifies
                    byte disqualifiedPlayer = message[0];
                    networkClient.receiveDisqualification(disqualifiedPlayer);
                    if(disqualifiedPlayer == playerNumber) {
                        Logger.get().log("Disconnecting from server");
                        socket.close();
                        Logger.get().log("Disconnected");
                    }
                    break;


                case 8: // End of phase 1
                    networkClient.receiveEndingPhase1();
                    break;


                case 9: // End of phase 2 - the end.
                    networkClient.receiveEndingPhase2();
                    break;


                default:
                    Logger.get().warn("Unknown message type: " + messageType);

            }
        }
    }
}
