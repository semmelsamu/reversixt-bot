package exercises;

import game.Game;
import game.GameFactory;
import util.Logger;

public class Exercise01 {

    /**
     * Abnahme: mindestens ein vorgegebenes Spielfeld wird eingelesen und die vollständige Datenstruktur
     * wieder auf die Konsole korrekt ausgegeben (inkl. Transitionen und weiterer Informationen zur Map).
     */
    public static void aufgabe3() {
        Game game = GameFactory.createFromFile("maps/example.map");
        Logger.get().log(game.toString());
    }

}
