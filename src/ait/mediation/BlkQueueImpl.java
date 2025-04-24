package ait.mediation;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxSize;
    private final Lock mutex = new ReentrantLock();
    private final Condition senderQueue = mutex.newCondition();
    private final Condition reseiverQueue = mutex.newCondition();

    public BlkQueueImpl(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void push(T message) {
        mutex.lock();
        try {
            while (queue.size() >= maxSize) {
                try {
                   senderQueue.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.add(message);
            reseiverQueue.signal();
        } finally {
            mutex.unlock();
        }


    }

    @Override
    public T pop() {
        mutex.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    reseiverQueue.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            T result = queue.removeFirst();
            senderQueue.signal();
            return result;
        } finally {
            mutex.unlock();
        }

    }
}