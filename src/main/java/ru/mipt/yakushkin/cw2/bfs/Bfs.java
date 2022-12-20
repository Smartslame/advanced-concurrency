package ru.mipt.yakushkin.cw2.bfs;

import ru.mipt.yakushkin.cw2.objects.CubeGraph;

public interface Bfs {
    int[] search(CubeGraph graph, int start);
}