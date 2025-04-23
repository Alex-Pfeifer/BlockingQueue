package ait.mediation;

import java.util.LinkedList;

public class BlkQueueImpl<T> implements BlkQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxSize;

    public BlkQueueImpl(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void push(T message) {
        while (queue.size() >= maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.add(message);
        notifyAll();
    }

    @Override
    public T pop() {
        while (queue.size() < maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        T result = queue.removeFirst();
        notifyAll();
        return result;
    }
}