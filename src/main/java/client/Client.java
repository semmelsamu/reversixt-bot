package client;

import player.move.Move;

public interface Client {

    public int getGroupNumber();

    public void setMap();

    public void setPlayer();

    public Move getMove();

    public void processMove();

}
