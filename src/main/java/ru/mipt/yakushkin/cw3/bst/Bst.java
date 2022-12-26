package ru.mipt.yakushkin.cw3.bst;

import java.util.List;

public interface Bst<V extends Number & Comparable<V>> {
    boolean contains(V v);

    boolean insert(V v);

    boolean delete(V v);

    List<V> inorderTraversal();
}
