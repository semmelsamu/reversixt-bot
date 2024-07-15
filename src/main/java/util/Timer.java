package util;



public class Timer {

    /**
     * Time timer is started in nanoseconds
     */
    private long startTime;

    /**
     * Timelimit in nanoseconds
     */
    private long limit;

    public Timer(long limitMs) {
        startTime = currentTime();
        if (limitMs == Long.MAX_VALUE){
            this.limit = limitMs;
        }
        else{
            // convert to nanoseconds
            this.limit = limitMs * 1000000;
        }
    }

    public Timer() {
        this(-1);
    }

    public static long currentTime() {
        return System.nanoTime();
    }

    public long timePassedInMs(){
        return fromNanoToMilli(timePassed());
    }

    public long timePassed() {
        return currentTime() - startTime;
    }

    public long timeLeft() {
        return startTime + limit - currentTime();
    }

    public long timeLeftInMs(){
        return fromNanoToMilli(timeLeft());
    }

    public boolean isUp() {
        return timeLeft() <= 0;
    }

    public long getLimitInMs(){
        return fromNanoToMilli(limit);
    }

    public static long fromNanoToMilli(long time){
        return time / 1_000_000;
    }
}
