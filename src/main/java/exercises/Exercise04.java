package exercises;

import clients.RandomMoveClient;
import network.Launcher;
import network.NetworkClientAdapter;
import network.NetworkEventHandler;

import java.io.IOException;

/**
 * Implementieren Sie das Netzwerkprotokoll clientseitig, sodass eine fehlerfreie Kommunikation
 * mit dem von uns bereitgestellten Spieleserver möglich ist. Halten Sie sich dabei an die
 * gegebene Spezifikation. Dabei können sie Angaben über Zeit- und Tiefenbeschränkung zunächst
 * ignorieren.
 * -
 * Es sollen für beide Spielphasen (Spielbeginn und Endspiel) gültige Züge zurückgegeben werden
 * können. Für Bomben gilt dabei aktuell, dass sie – außer auf Löcher – überall hin geworfen
 * werden können. Sie sprengen (d.h., sie verwandeln in Löcher) alle Steine, die vom
 * Explosionszentrum so weit entfernt sind, wie die Stärke der Bombe angibt – also eine
 * Entfernung kleiner oder gleich dem Wert von Stärke haben. Dabei werden auch dadurch getroffene
 * Transitionen gelöscht. Die Kompilate des Spieleservers finden Sie im globalen Repository des
 * Moduls unter dem Ordner reversi-binaries.
 */
public class Exercise04 {

    /**
     * Abnahme: Für die Abnahme muss zunächst der Server mit einem sinnvollen
     * Zwei-Spieler-Spielbrett inkl. einer Bombe der Stärke 0 gestartet werden. Starten Sie dann
     * zwei Instanzen Ihres Clients. Diese sollen sich mit dem Server verbinden und anschließend
     * mit zufälligen aber validen Zügen gegeneinander spielen, bis das Ende des Spiels erreicht
     * ist. Aufgaben 2 und 3 werden in diesem Zusammenhang auch überprüft.
     */
    public static void abnahme(String ip, int port) {
        Launcher.launchClientOnNetwork(new RandomMoveClient(), ip, port);
    }
}
