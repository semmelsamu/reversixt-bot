package util;

public class Constants {

    /**
     * The time in milliseconds by which we want to respond earlier to avoid disqualification due to
     * network latency.
     */
    public static int TIME_BUFFER = 1000;

    /**
     * The radius in which neighbouring Communitys will merge.
     */
    public static int COMMUNITY_MERGE_RADIUS = 3;

    /**
     * The factor used to calculate the penalty for a "dead" Community. This constant is multiplied
     * by the wasted potential of the Community to determine the final penalty value.
     */
    public static int DEAD_COMMUNITY_PUNISHMENT_FACTOR = 1;

}
