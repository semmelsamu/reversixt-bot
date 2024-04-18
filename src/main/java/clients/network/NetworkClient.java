package clients.network;

public interface NetworkClient {

    int sendGroupNumber();

    void receiveMap(String map);

    void receivePlayerNumber(byte player);

    MoveAnswer sendMoveAnswer();

    void receiveMove(short x, short y, byte type, byte player);

    void receiveDisqualification(byte player);

    void receiveEndingPhase1();

    void receiveEndingPhase2();

}
