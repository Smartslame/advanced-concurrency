package ru.mipt.yakushkin.cw3.bst.seq;

import ru.mipt.yakushkin.cw3.bst.common.State;

public class NodeSeq<V extends Number & Comparable<V>> {
    private V value;
    private NodeSeq<V> left;
    private NodeSeq<V> right;
    private State state;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public NodeSeq<V> getLeft() {
        return left;
    }

    public void setLeft(NodeSeq<V> left) {
        this.left = left;
    }

    public NodeSeq<V> getRight() {
        return right;
    }

    public void setRight(NodeSeq<V> right) {
        this.right = right;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
