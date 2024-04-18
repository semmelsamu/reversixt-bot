package client;

import util.Logger;

import java.io.*;
import java.net.*;

/**
 * The client connects to the server. It waits for the server's messages, and reacts accordingly.
 */
public class Client {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    private void sendGroupNumber(int groupNumber) throws IOException {
        out.writeByte(1); // Message type
        out.writeInt(1); // Message length
        out.writeByte(groupNumber);
    }

    public void run() throws IOException {

        while (!socket.isClosed()) {

            int messageType = in.readByte();
            int length = in.readInt();
            byte[] message = new byte[length];
            in.readFully(message);

            switch (messageType) {
                case 2: // Server sends map
                    processGameField(new String(message));
                    break;
                case 3: // Server assigns player number
                    processPlayerNumber(message[0]);
                    break;
                case 4: // Server requests move
                    respondToMoveRequest();
                    break;
                case 6: // Server sends move from other player
                    processMoveNotification(message);
                    break;
                case 7: // Server disqualifies
                    processDisqualification(message[0]);
                    break;
                case 8: // End of phase 1
                    break;
                case 9: // End of phase 2 - the end.
                    processGameEnd();
                    break;
                default:
                    Logger.get().warn("Unknown message type: " + messageType);
            }
        }
    }

}
