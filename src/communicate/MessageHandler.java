package communicate;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Klasa opakowuje współdzieloną kolejkę wiadomości.
 */


public class MessageHandler {
    private ConcurrentLinkedQueue<Message> queue;

    MessageHandler() {
        queue=new ConcurrentLinkedQueue<>();
    }

    public synchronized boolean isEmpty() {
        return queue.peek()==null;
    }

    public synchronized void addToQueue(Message msg) {
        queue.offer(msg);
    }

    public synchronized Message removeFromQueue() {
        return queue.poll();
    }

    public synchronized Message getMessage() {
        return queue.peek();
    }
}
