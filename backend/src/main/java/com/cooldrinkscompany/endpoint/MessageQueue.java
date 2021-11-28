package com.cooldrinkscompany.endpoint;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {

    private static final MessageQueue INSTANCE = new MessageQueue();

    private Queue<String> queue = new ConcurrentLinkedQueue<>();

    public static MessageQueue instance() {
        return INSTANCE;
    }

    private MessageQueue() {
    }

    public void push(String s) {
        queue.add(s);
    }

    public String pop() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public String peek() {
        return queue.peek();
    }
}