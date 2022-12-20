package ru.mipt.yakushkin.cw1.utils;

import java.util.Random;

public class DataGenerationUtil {
    private DataGenerationUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static int[] generate(int arraySize) {
        Random rd = new Random(42);
        int[] arr = new int[arraySize];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = rd.nextInt();
        }

        return arr;
    }
}
