package ru.mipt.yakushkin.cw2.bfs;

import ru.mipt.yakushkin.cw2.objects.CubeGraph;

import java.util.*;

public class BfsSeq implements Bfs {
    @Override
    public int[] search(CubeGraph graph, int start) {
        int[] res = new int[graph.getSize()];
        res[start] = 0;
        boolean[] visited = new boolean[graph.getSize()];
        visited[start] = true;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            int[] neighbours = graph.getNeighbours(cur);
            for (int i : neighbours) {
                if (!visited[i]) {
                    visited[i] = true;
                    res[i] = res[cur] + 1;
                    queue.add(i);
                }
            }
        }
        return res;
    }
}