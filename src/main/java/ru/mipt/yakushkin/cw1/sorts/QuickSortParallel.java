package ru.mipt.yakushkin.cw1.sorts;

import ru.mipt.yakushkin.cw1.utils.PartitionUtil;

import java.util.concurrent.RecursiveTask;

public class QuickSortParallel extends RecursiveTask<Integer> {
    private static final int SEQ_LIMIT = 300000;

    int begin, end;
    int[] arr;

    public QuickSortParallel(int[] arr, int begin, int end) {
        this.arr = arr;
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (begin >= end)
            return null;

        if (end - begin <= SEQ_LIMIT) {
            QuickSortSeq.sort(arr, begin, end);
            return null;
        }

        int p = PartitionUtil.partition(arr, begin, end);

        QuickSortParallel left = new QuickSortParallel(arr, begin, p - 1);

        QuickSortParallel right = new QuickSortParallel(arr, p + 1, end);

        left.fork();
        right.compute();

        left.join();

        return null;
    }
}