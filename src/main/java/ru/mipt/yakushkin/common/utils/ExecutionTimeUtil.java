package ru.mipt.yakushkin.common.utils;

public class ExecutionTimeUtil {
    private ExecutionTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static long measure(Runnable runnable, String prefix) {
        long startTime = System.currentTimeMillis();

        runnable.run();

        long endTime = System.currentTimeMillis();

        long time = endTime - startTime;

        System.out.println("[" + prefix + "] Elapsed Time in milli seconds: " + time);

        return time;
    }
}
