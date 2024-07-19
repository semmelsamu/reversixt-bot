package network;

import exceptions.ClientDisqualifiedException;
import exceptions.NetworkException;
import util.Triple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Manages the communication between a client and a server via a network socket.
 */
public class NetworkEventHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private NetworkClientAdapter client;

    private byte playerNumber = 0x0;

    /**
     * Connect to a server.
     * @param ip   The IP address of the server to which the client will connect.
     * @param port The port number on the server to which the client will connect.
     */
    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    /**
     * Launch the client on the network.
     */
    public void launch() throws IOException, ClientDisqualifiedException, NetworkException {

        this.client = new NetworkClientAdapter();

        sendGroupNumber();
        run();

    }

    public void disconnect() throws IOException {
        socket.close();
    }

    private void sendGroupNumber() throws IOException {
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(client.sendGroupNumber());
    }

    public void run() throws IOException, NetworkException, ClientDisqualifiedException {

        boolean isRunning = true;

        while (isRunning) {

            int messageType = in.readByte();
            int length = in.readInt();

            switch (messageType) {


                //   1: // Client sends group name


                case 2: // Server sends map
                    byte[] map = new byte[length];
                    in.readFully(map);
                    client.receiveMap(new String(map));
                    break;


                case 3: // Server assigns player number

                    playerNumber = in.readByte();
                    client.receivePlayerNumber(playerNumber);
                    break;


                case 4: // Server requests move

                    int timeLimit = in.readInt();
                    byte depthLimit = in.readByte();

                    // Get move answer
                    Triple<Short, Short, Byte> answer =
                            client.sendMoveAnswer(timeLimit, depthLimit);

                    // Write
                    out.writeByte(5); // Message type
                    out.writeInt(5); // Message length
                    out.writeShort(answer.first()); // x
                    out.writeShort(answer.second()); // y
                    out.writeByte(answer.third()); // type

                    break;


                //   5: // Client sends move


                case 6: // Server sends move from other player

                    short x = in.readShort();
                    short y = in.readShort();
                    byte type = in.readByte();
                    byte player = in.readByte();

                    // Pass to client
                    client.receiveMove(x, y, type, player);

                    break;


                case 7: // Server disqualifies

                    byte disqualifiedPlayer = in.readByte();

                    client.receiveDisqualification(disqualifiedPlayer);

                    if (disqualifiedPlayer == playerNumber) {
                        throw new ClientDisqualifiedException("Client got disqualified");
                    }

                    break;


                case 8: // End of phase 1

                    // Once upon a time here was logging code. Now it isn't as we optimize for
                    // performance. Nice.

                    break;


                case 9: // End of phase 2 - the end.

                    isRunning = false;
                    break;


                default:
                    throw new NetworkException("Unknown message type: " + messageType);


            }

        }
    }
}
