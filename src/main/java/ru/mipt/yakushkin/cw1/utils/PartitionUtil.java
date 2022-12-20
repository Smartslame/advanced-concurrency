package ru.mipt.yakushkin.cw1.utils;

import java.util.Random;

public class PartitionUtil {

    private PartitionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static int partition(int[] arr, int start, int end) {

        int i = start;
        int j = end;

        int pivot = new Random().nextInt(j - i) + i;

        swap(arr, pivot, j);
        j--;

        while (i <= j) {

            if (arr[i] <= arr[end]) {
                i++;
                continue;
            }

            if (arr[j] >= arr[end]) {
                j--;
                continue;
            }

            swap(arr, i, j);

            j--;
            i++;
        }

        swap(arr, j + 1, end);

        return j + 1;
    }

    private static void swap(int[] arr, int from, int to) {
        int t = arr[from];
        arr[from] = arr[to];
        arr[to] = t;
    }

}
