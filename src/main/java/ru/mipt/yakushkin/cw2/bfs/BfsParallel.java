package ru.mipt.yakushkin.cw2.bfs;

import ru.mipt.yakushkin.cw2.objects.CubeGraph;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BfsParallel implements Bfs {

    @Override
    public int[] search(CubeGraph graph, int start) {
        int[] frontier = new int[1];
        frontier[0] = start;

        AtomicInteger[] atomicResult = new AtomicInteger[graph.getSize()];

        IntStream.range(0, graph.getSize()).parallel()
                 .forEach(x -> atomicResult[x] = new AtomicInteger(-1));


        atomicResult[start].set(0);
        while (frontier.length != 0) {
            int[] degree = new int[frontier.length + 1];
            int[] finalFrontier = frontier;
            IntStream.range(1, degree.length).parallel()
                     .forEach(x -> degree[x] = graph.getNeighbours(finalFrontier[x - 1]).length);


            Arrays.parallelPrefix(degree, (x, y) -> x + y);
            int[] newFrontier = new int[degree[degree.length - 1]];
            Arrays.parallelSetAll(newFrontier, i -> -1);
            IntStream.range(0, frontier.length).parallel()
                     .forEach(
                             i -> {
                                 int current = finalFrontier[i];
                                 int dist = atomicResult[current].get();
                                 int next = degree[i];
                                 int[] neighbours = graph.getNeighbours(current);
                                 for (int v : neighbours) {
                                     if (atomicResult[v].compareAndSet(-1, dist + 1)) {
                                         newFrontier[next++] = v;
                                     }
                                 }
                             }
                     );

            frontier = Arrays.stream(newFrontier).parallel().filter(x -> x != -1).toArray();

        }
        int[] res = new int[graph.getSize()];
        for (int i = 0; i < atomicResult.length; i++) res[i] = atomicResult[i].get();
        return res;
    }
}