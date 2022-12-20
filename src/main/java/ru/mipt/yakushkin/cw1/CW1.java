package ru.mipt.yakushkin.cw1;

import org.junit.jupiter.api.Assertions;
import ru.mipt.yakushkin.cw1.sorts.QuickSortParallel;
import ru.mipt.yakushkin.cw1.sorts.QuickSortSeq;
import ru.mipt.yakushkin.cw1.utils.DataGenerationUtil;
import ru.mipt.yakushkin.common.utils.ExecutionTimeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class CW1 {

    private static final int TEST_COUNT = 5;
    private static final List<String> SORTS_PREFIX = List.of("PAR", "SEQ", "DEFAULT");
    public static final int DATA_SIZE = (int) 1e8;

    public static void main(String[] args) {
        final ForkJoinPool pool = ForkJoinPool.commonPool();

        long[][] times = new long[3][TEST_COUNT];

        for (int i = 0; i < TEST_COUNT; i++) {
            System.out.println("Test # " + (i + 1));

            int[] arr = DataGenerationUtil.generate(DATA_SIZE);
            int[] arrForSeq = Arrays.copyOf(arr, arr.length);
            int[] arrForParr = Arrays.copyOf(arr, arr.length);

            times[0][i] = ExecutionTimeUtil.measure(() -> pool.invoke(new QuickSortParallel(arrForParr, 0, arrForParr.length - 1)), SORTS_PREFIX.get(0));
            times[1][i] = ExecutionTimeUtil.measure(() -> QuickSortSeq.sort(arrForSeq, 0, arrForSeq.length - 1), SORTS_PREFIX.get(1));
            times[2][i] = ExecutionTimeUtil.measure(() -> Arrays.sort(arr), SORTS_PREFIX.get(2));

            Assertions.assertTrue(Arrays.equals(arr, arrForParr));
            Assertions.assertTrue(Arrays.equals(arr, arrForSeq));

            System.out.println();
        }

        double[] averageTimes = new double[3];
        for (int i = 0; i < 3; i++) {
            long[] time = times[i];
            averageTimes[i] = Arrays.stream(time).average().orElse(Double.NaN);
            System.out.println("[" + SORTS_PREFIX.get(i) + "] Average elapsed Time in milli seconds: " + averageTimes[i]);
        }

        System.out.println();


        System.out.println("PAR faster SEQ in " + averageTimes[1] / averageTimes[0] + " times");
        System.out.println("PAR faster DEFAULT in " + averageTimes[2] / averageTimes[0] + " times");

    }
}
