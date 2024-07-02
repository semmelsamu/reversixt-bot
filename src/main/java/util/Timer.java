package util;

public class Timer {

    long startTime;
    long limit;

    public Timer(long limit) {
        startTime = currentTime();
        this.limit = limit;
    }

    public Timer() {
        this(-1);
    }

    public static long currentTime() {
        return System.currentTimeMillis();
    }

    public long timePassed() {
        return currentTime() - startTime;
    }

    public long timeLeft() {
        return startTime + limit - currentTime();
    }

    public boolean isUp() {
        return timeLeft() <= 0;
    }

}
