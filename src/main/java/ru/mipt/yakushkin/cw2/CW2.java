package ru.mipt.yakushkin.cw2;


import org.junit.jupiter.api.Assertions;
import ru.mipt.yakushkin.common.utils.ExecutionTimeUtil;
import ru.mipt.yakushkin.cw2.bfs.Bfs;
import ru.mipt.yakushkin.cw2.bfs.BfsParallel;
import ru.mipt.yakushkin.cw2.bfs.BfsSeq;
import ru.mipt.yakushkin.cw2.objects.CubeGraph;

import java.util.Arrays;
import java.util.List;

public class CW2 {
    private static final int TEST_COUNT = 5;
    private static final List<String> BFS_PREFIX = List.of("SEQ", "PAR");
    public static final int GRAPH_SIZE = 500;

    public static void main(String[] args) throws InterruptedException {

        long[][] times = new long[2][TEST_COUNT];

        for (int i = 0; i < TEST_COUNT; i++) {
            System.out.println("Test # " + (i + 1));

            CubeGraph graph = new CubeGraph(GRAPH_SIZE);
            Bfs seq = new BfsSeq();
            Bfs par = new BfsParallel();
            final int[][] seqRes = {new int[0]};
            final int[][] parRes = {new int[0]};
            times[0][i] = ExecutionTimeUtil.measure(() -> seqRes[0] = seq.search(graph, 0), BFS_PREFIX.get(0));
            times[1][i] = ExecutionTimeUtil.measure(() -> parRes[0] = par.search(graph, 0), BFS_PREFIX.get(1));

            Assertions.assertTrue(Arrays.equals(seqRes[0], parRes[0]));

            System.out.println();
            Thread.sleep(3000);
        }

        double[] averageTimes = new double[2];
        for (int i = 0; i < 2; i++) {
            long[] time = times[i];
            averageTimes[i] = Arrays.stream(time).average().orElse(Double.NaN);
            System.out.println("[" + BFS_PREFIX.get(i) + "] Average elapsed Time in milli seconds: " + averageTimes[i]);
        }

        System.out.println();


        System.out.println("PAR faster SEQ in " + averageTimes[0] / averageTimes[1] + " times");
    }
}