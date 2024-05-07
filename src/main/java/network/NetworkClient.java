package network;

/**
 * A client that operates with the server's data types, such as uint16, short, byte...
 */
public interface NetworkClient {

    int sendGroupNumber();

    void receiveMap(String map);

    void receivePlayerNumber(byte player);

    MoveAnswer sendMoveAnswer(int timeLimit, byte depthLimit);

    void receiveMove(short x, short y, byte type, byte player);

    void receiveDisqualification(byte player);

    void receiveEndingPhase1();

    void receiveEndingPhase2();

}
