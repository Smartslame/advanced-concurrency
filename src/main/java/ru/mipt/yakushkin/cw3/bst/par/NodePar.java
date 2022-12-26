package ru.mipt.yakushkin.cw3.bst.par;

import ru.mipt.yakushkin.cw3.bst.common.State;

import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NodePar<V extends Number & Comparable<V>> {
    private V value;
    private NodePar<V> left;
    private NodePar<V> right;
    private State state;

    private final AtomicBoolean deleted = new AtomicBoolean();
    private final ReentrantReadWriteLock leftLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock rightLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

    void tryLockLeftEdgeRef(NodePar<V> expRef, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.leftLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.deleted.get() || this.left != expRef) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryLockRightEdgeRef(NodePar<V> expRef, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.rightLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.deleted.get() || this.right != expRef) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryLockEdgeRef(NodePar<V> expRef, ThreadLocal<Stack<Lock>> localLocks) {
        if (value.compareTo(expRef.getValue()) < 0) {
            tryLockRightEdgeRef(expRef, localLocks);
        } else {
            tryLockLeftEdgeRef(expRef, localLocks);
        }
    }

    void tryLockLeftEdgeVal(V expVal, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.leftLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.deleted.get() || Objects.isNull(left) || this.left.getValue().compareTo(expVal) != 0) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryLockRightEdgeVal(V expVal, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.rightLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.deleted.get() || Objects.isNull(right) || this.right.getValue().compareTo(expVal) != 0) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryLockEdgeVal(NodePar<V> expRef, ThreadLocal<Stack<Lock>> localLocks) {
        if (value.compareTo(expRef.getValue()) < 0) {
            tryLockRightEdgeVal(expRef.getValue(), localLocks);
        } else {
            tryLockLeftEdgeVal(expRef.getValue(), localLocks);
        }
    }

    void tryReadLockState(ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.ReadLock lock = stateLock.readLock();

        lock.lock();
        localLocks.get().push(lock);

        if (deleted.get()) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryReadLockState(State expState, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.ReadLock lock = stateLock.readLock();

        lock.lock();
        localLocks.get().push(lock);

        if (deleted.get() || !expState.equals(state)) {
            throw new RuntimeException("Bad lock");
        }
    }

    void tryWriteLockState(State expState, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = stateLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (deleted.get() || !expState.equals(state)) {
            throw new RuntimeException("Bad lock");
        }
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public NodePar<V> getLeft() {
        return left;
    }

    public void setLeft(NodePar<V> left) {
        this.left = left;
    }

    public NodePar<V> getRight() {
        return right;
    }

    public void setRight(NodePar<V> right) {
        this.right = right;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public AtomicBoolean getDeleted() {
        return deleted;
    }
}
