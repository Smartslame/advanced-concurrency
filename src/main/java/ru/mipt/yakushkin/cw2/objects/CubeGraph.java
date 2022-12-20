package ru.mipt.yakushkin.cw2.objects;

import java.util.ArrayList;
import java.util.List;

public class CubeGraph {
    private final int size;

    public CubeGraph(int size) {
        this.size = size;
    }

    public int getSize() {
        return size * size * size;
    }

    public int[] getNeighbours(int index) {
        int i = index / size / size;

        index = index % (size * size);
        int j = index / size;

        index = index % size;
        int k = index;

        List<Integer> res = new ArrayList<>();
        if (i > 0) {
            res.add((i - 1) * size * size + j * size + k);
        }
        if (j > 0) {
            res.add(i * size * size + (j - 1) * size + k);
        }
        if (k > 0) {
            res.add(i * size * size + j * size + k - 1);
        }
        if (i < size - 1) {
            res.add((i + 1) * size * size + j * size + k);
        }
        if (j < size - 1) {
            res.add(i * size * size + (j + 1) * size + k);
        }
        if (k < size - 1) {
            res.add(i * size * size + j * size + k + 1);
        }

        int[] arr = new int[res.size()];
        for (int t = 0; t < res.size(); t++) arr[t] = res.get(t);
        return arr;
    }
}
