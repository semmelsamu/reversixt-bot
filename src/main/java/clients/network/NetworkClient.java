package clients.network;

public interface NetworkClient {

    public int sendGroupNumber();

    public void receiveMap(String map);

    public void receivePlayerNumber(byte player);

    public MoveAnswer sendMoveAnswer();

    public void receiveMove(short x, short y, byte type, byte player);

    public void receiveDisqualification(byte player);

    public void receiveEndingPhase1();

    public void receiveEndingPhase2();

}
