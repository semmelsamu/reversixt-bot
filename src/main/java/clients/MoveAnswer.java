package clients;

public record MoveAnswer(

        /*
         * Koordinaten als vorzeichenlose 16-Bit-Integer
         */
        short x,
        short y,

        /*
         * Vorzeichenlose 8-Bit-Integer für eventuelle Sonderfelder (0 bei normalem Feld; beim
         * Choice-Feld die Spielernummer mit der getauscht wird; beim Bonus-Felder eine 20 für
         * extra Bombe oder 21 für extra Überschreibstein)
         */
        byte type
) {
}
