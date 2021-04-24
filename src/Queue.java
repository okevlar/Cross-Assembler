/**
 * SOEN 341    W21
 * GAMESTOP - GROUP 13
 * Filename: Queue.java
 * Creation: 2021-02-24
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Queue<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of queue
    private Node<Item> last;     // end of queue
    private int n;               // number of elements on queue

    
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    public Iterator<Item> iterator()  { return new LinkedIterator(first); }

    // an iterator, doesn't implement remove() since it's optional
    private class LinkedIterator implements Iterator<Item> {
        private Node<Item> current;

        public LinkedIterator(Node<Item> first) { current = first; }

        public boolean hasNext() { return current != null; }

        public void remove() { throw new UnsupportedOperationException(); }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public Queue() {
        first = null;
        last  = null;
        n = 0;
    }


    public boolean isEmpty() { return first == null; }

    public int size() { return n; }

    public Item peek() {
        if (isEmpty())
            throw new NoSuchElementException("Queue underflow");

        return first.item;
    }

    public void enqueue(Item item) {
        Node<Item> oldlast = last;
        last = new Node<Item>();
        last.item = item;
        last.next = null;

        if (isEmpty())
            first = last;
        else
            oldlast.next = last;

        n++;
    }

    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Queue underflow");

        Item item = first.item;
        first = first.next;
        n--;

        if (isEmpty())
            last = null;   // to avoid loitering

        return item;
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this) {
            s.append(item);
            s.append(',');
            s.append(' ');
        }
        String stringer = s.substring(0,s.lastIndexOf(","));
        return stringer;
    }
}

