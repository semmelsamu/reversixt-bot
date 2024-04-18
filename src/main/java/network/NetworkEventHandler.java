package network;

import util.Logger;

import java.io.*;
import java.net.*;

/**
 * Manages the communication between a client and a server via a network socket.
 */
public class NetworkEventHandler {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    private final NetworkClient networkClient;

    /**
     * Connect a networkClient to a server.
     *
     * @param ip            The IP address of the server to which the client will connect.
     * @param port          The port number on the server to which the client will connect.
     * @param networkClient The client object that handles the actual "client" work. Must
     *                      implement the {@link NetworkClient} interface.
     * @throws IOException If at some point the connection to the server fails.
     */
    public NetworkEventHandler(String ip, int port, NetworkClient networkClient)
            throws IOException {

        // Store client
        this.networkClient = networkClient;

        // Connect
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        // Launch
        sendGroupNumber();
        run();
    }

    private void sendGroupNumber() throws IOException {
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(networkClient.sendGroupNumber());
    }

    public void run() throws IOException {

        while (!socket.isClosed()) {

            int messageType = in.readByte();
            int length = in.readInt();
            byte[] message = new byte[length];
            in.readFully(message);

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    networkClient.receiveMap(new String(message));
                    break;


                case 3: // Server assigns player number
                    networkClient.receivePlayerNumber(message[0]);
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
                    networkClient.receiveDisqualification(message[0]);
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
