package ru.mipt.yakushkin.cw3.bst.par;

import ru.mipt.yakushkin.cw3.bst.Bst;
import ru.mipt.yakushkin.cw3.bst.common.State;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class BstPar<V extends Number & Comparable<V>> implements Bst<V> {
    private NodePar<V> root;
    private static ThreadLocal<Stack<Lock>> localLocks = ThreadLocal.withInitial(Stack::new);

    public BstPar(V initialVal) {
        this.root = new NodePar<>();
        this.root.setValue(initialVal);
        this.root.setState(State.DATA);
    }

    private List<NodePar<V>> traversal(V v) {
        while (true) {
            NodePar<V> gPrev = new NodePar<>();
            NodePar<V> prev = new NodePar<>();
            NodePar<V> curr = this.root;

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

                if (checkDeleted(gPrev)) break;
                if (checkDeleted(prev)) break;
                if (checkDeleted(curr)) break;
            }

            if (checkDeleted(gPrev)) continue;
            if (checkDeleted(prev)) continue;
            if (checkDeleted(curr)) continue;

            return Arrays.asList(gPrev, prev, curr);
        }
    }

    private boolean checkDeleted(NodePar<V> node) {
        if (Objects.nonNull(node) && node.getDeleted().get()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean contains(V v) {
        List<NodePar<V>> traversal = traversal(v);
        NodePar<V> curr = traversal.get(2);
        return Objects.nonNull(curr) && curr.getState().equals(State.DATA);
    }

    @Override
    public boolean insert(V v) {
        while (true) {
            try {
                List<NodePar<V>> traversal = traversal(v);
                NodePar<V> prev = traversal.get(1);
                NodePar<V> curr = traversal.get(2);

                if (Objects.nonNull(curr)) {
                    if (curr.getState().equals(State.DATA)) {
                        return false;
                    }


                    curr.tryWriteLockState(State.ROUTING, localLocks);
                    curr.setState(State.DATA);
                } else {
                    NodePar<V> newNodePar = new NodePar<>();
                    newNodePar.setValue(v);
                    newNodePar.setState(State.DATA);
                    if (prev.getValue().compareTo(v) > 0) {
                        prev.tryReadLockState(localLocks);
                        prev.tryLockLeftEdgeRef(null, localLocks);
                        prev.setLeft(newNodePar);
                    } else {
                        prev.tryReadLockState(localLocks);
                        prev.tryLockRightEdgeRef(null, localLocks);
                        prev.setRight(newNodePar);
                    }
                }

                return true;
            } catch (Exception e) {
//                System.out.println(e.toString());
            } finally {
                unlockAllLocks();
            }
        }
    }

    private void unlockAllLocks() {
        while (!localLocks.get().empty()) {
            localLocks.get().pop().unlock();
        }
    }

    @Override
    public boolean delete(V v) {
        while (true) {
            try {
                List<NodePar<V>> traversal = traversal(v);
                NodePar<V> gPrev = traversal.get(0);
                NodePar<V> prev = traversal.get(1);
                NodePar<V> curr = traversal.get(2);

                if (Objects.isNull(curr) || !curr.getState().equals(State.DATA)) {
                    return false;
                }

                if (Objects.nonNull(curr.getLeft()) && Objects.nonNull(curr.getRight())) {
                    curr.tryWriteLockState(State.DATA, localLocks);

                    if (Objects.isNull(curr.getLeft()) || Objects.isNull(curr.getRight())) {
                        throw new RuntimeException("curr does not have 2 children");
                    }
                    curr.setState(State.ROUTING);
                } else if (Objects.nonNull(curr.getLeft()) || Objects.nonNull(curr.getRight())) {
                    NodePar<V> child = Objects.nonNull(curr.getLeft()) ? curr.getLeft() : curr.getRight();

                    if (curr.getValue().compareTo(prev.getValue()) < 0) {
                        lockVertexWithOneChild(prev, curr, child);
                        curr.getDeleted().set(true);
                        prev.setLeft(child);
                    } else {
                        lockVertexWithOneChild(prev, curr, child);
                        curr.getDeleted().set(true);
                        prev.setRight(child);
                    }
                } else {
                    if (prev.getState().equals(State.DATA)) {
                        if (curr.getValue().compareTo(prev.getValue()) < 0) {
                            prev.tryReadLockState(State.DATA, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            curr.getDeleted().set(true);
                            prev.setLeft(null);
                        } else {
                            prev.tryReadLockState(State.DATA, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            curr.getDeleted().set(true);
                            prev.setRight(null);
                        }
                    } else {
                        NodePar<V> child;
                        if (curr.getValue().compareTo(prev.getValue()) < 0) {
                            child = prev.getRight();
                        } else {
                            child = prev.getLeft();
                        }

                        if (Objects.nonNull(gPrev.getLeft()) && prev == gPrev.getLeft()) {
                            gPrev.tryLockEdgeRef(prev, localLocks);
                            prev.tryWriteLockState(State.ROUTING, localLocks);
                            prev.tryLockEdgeRef(child, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            prev.getDeleted().set(true);
                            curr.getDeleted().set(true);
                            gPrev.setLeft(child);
                        } else if (Objects.nonNull(gPrev.getRight()) && prev == gPrev.getRight()) {
                            gPrev.tryLockEdgeRef(prev, localLocks);
                            prev.tryWriteLockState(State.ROUTING, localLocks);
                            prev.tryLockEdgeRef(child, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            prev.getDeleted().set(true);
                            curr.getDeleted().set(true);
                            gPrev.setRight(child);
                        }

                    }
                }

                return true;
            } catch (Exception e) {
//                System.out.println(e.toString());
            } finally {
                unlockAllLocks();
            }
        }
    }

    private NodePar<V> lockLeaf(V v, NodePar<V> prev, NodePar<V> curr) {
        prev.tryLockEdgeVal(curr, localLocks);

        if (v.compareTo(prev.getValue()) < 0) {
            curr = prev.getLeft();
        } else {
            curr = prev.getRight();
        }

        curr.tryWriteLockState(State.DATA, localLocks);

        if (Objects.nonNull(curr.getLeft()) || Objects.nonNull(curr.getRight())) {
            throw new RuntimeException("curr in not a leaf");
        }
        return curr;
    }

    private void lockVertexWithOneChild(NodePar<V> prev, NodePar<V> curr, NodePar<V> child) {
        prev.tryLockEdgeRef(curr, localLocks);
        curr.tryWriteLockState(State.DATA, localLocks);

        if (Objects.nonNull(curr.getLeft()) && Objects.nonNull(curr.getRight())) {
            throw new RuntimeException("curr has 2 children");
        }

        if (Objects.isNull(curr.getLeft()) && Objects.isNull(curr.getRight())) {
            throw new RuntimeException("curr has 0 children");
        }

        curr.tryLockEdgeRef(child, localLocks);
    }

    @Override
    public List<V> inorderTraversal() {
        List<V> list = new ArrayList<>();
        Stack<NodePar<V>> stack = new Stack<>();
        NodePar<V> curr = root;
        while (curr != null || !stack.empty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.getLeft();
            }
            curr = stack.pop();
            if (curr.getState().equals(State.DATA) && !curr.getDeleted().get()) {
                list.add(curr.getValue());
            }
            curr = curr.getRight();
        }
        return list;
    }
}
