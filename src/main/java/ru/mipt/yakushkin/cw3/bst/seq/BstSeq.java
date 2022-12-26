package ru.mipt.yakushkin.cw3.bst.seq;

import ru.mipt.yakushkin.cw3.bst.Bst;
import ru.mipt.yakushkin.cw3.bst.common.State;

import java.util.*;

public class BstSeq<V extends Number & Comparable<V>> implements Bst<V> {
    private NodeSeq<V> root;

    public BstSeq(V initialVal) {
        this.root = new NodeSeq<V>();
        this.root.setValue(initialVal);
        this.root.setState(State.DATA);
    }

    private List<NodeSeq<V>> traversal(V v) {
        NodeSeq<V> gPrev = new NodeSeq<>();
        NodeSeq<V> prev = new NodeSeq<>();
        NodeSeq<V> curr = this.root;

        while (Objects.nonNull(curr)) {
            if (curr.getValue().equals(v)) {
                break;
            } else {
                gPrev = prev;
                prev = curr;
                if (curr.getValue().compareTo(v) > 0) {
                    curr = curr.getLeft();
                } else {
                    curr = curr.getRight();
                }
            }
        }

        return Arrays.asList(gPrev, prev, curr);
    }

    @Override
    public boolean contains(V v) {
        List<NodeSeq<V>> traversal = traversal(v);
        NodeSeq<V> curr = traversal.get(2);
        return Objects.nonNull(curr) && curr.getState().equals(State.DATA);
    }

    @Override
    public boolean insert(V v) {
        List<NodeSeq<V>> traversal = traversal(v);
        NodeSeq<V> prev = traversal.get(1);
        NodeSeq<V> curr = traversal.get(2);

        if (Objects.nonNull(curr)) {
            if (curr.getState().equals(State.DATA)) {
                return false;
            }

            curr.setState(State.DATA);
        } else {
            NodeSeq<V> newNodeSeq = new NodeSeq<>();
            newNodeSeq.setValue(v);
            newNodeSeq.setState(State.DATA);
            if (prev.getValue().compareTo(v) > 0) {
                prev.setLeft(newNodeSeq);
            } else {
                prev.setRight(newNodeSeq);
            }
        }

        return true;
    }

    @Override
    public boolean delete(V v) {
        List<NodeSeq<V>> traversal = traversal(v);
        NodeSeq<V> gPrev = traversal.get(0);
        NodeSeq<V> prev = traversal.get(1);
        NodeSeq<V> curr = traversal.get(2);

        if (Objects.isNull(curr) || !curr.getState().equals(State.DATA)) {
            return false;
        }

        if (Objects.nonNull(curr.getLeft()) && Objects.nonNull(curr.getRight())) {
            curr.setState(State.ROUTING);
        } else if (Objects.nonNull(curr.getLeft()) || Objects.nonNull(curr.getRight())) {
            NodeSeq<V> child = Objects.nonNull(curr.getLeft()) ? curr.getLeft() : curr.getRight();

            if (curr.getValue().compareTo(prev.getValue()) < 0) {
                prev.setLeft(child);
            } else {
                prev.setRight(child);
            }
        } else {
            if (prev.getState().equals(State.DATA)) {
                if (curr == prev.getLeft()) {
                    prev.setLeft(null);
                } else {
                    prev.setRight(null);
                }
            } else {
                NodeSeq<V> child;
                if (curr == prev.getLeft()) {
                    child = prev.getRight();
                } else {
                    child = prev.getLeft();
                }

                if (prev == gPrev.getLeft()) {
                    gPrev.setLeft(child);
                } else {
                    gPrev.setRight(child);
                }
            }
        }

        return true;
    }

    @Override
    public List<V> inorderTraversal() {
        List<V> list = new ArrayList<>();
        Stack<NodeSeq<V>> stack = new Stack<>();
        NodeSeq<V> curr = root;
        while (curr != null || !stack.empty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.getLeft();
            }
            curr = stack.pop();
            if (curr.getState().equals(State.DATA)) {
                list.add(curr.getValue());
            }
            curr = curr.getRight();
        }
        return list;
    }
}
