package ru.mipt.yakushkin.cw1.sorts;

import ru.mipt.yakushkin.cw1.utils.PartitionUtil;

public class QuickSortSeq {
    public static void sort(int arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = PartitionUtil.partition(arr, begin, end);

            sort(arr, begin, partitionIndex - 1);
            sort(arr, partitionIndex + 1, end);
        }
    }
}
