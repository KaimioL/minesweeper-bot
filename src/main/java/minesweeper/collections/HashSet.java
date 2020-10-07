package minesweeper.collections;

import java.util.Iterator;

import java.lang.Iterable;

public class HashSet<T> implements Iterable<T>{
    private static final int INITIAL_CAPACITY = 16;

    private Node<T>[] buckets;

    private int size;

    public HashSet(final int capacity) {
        this.buckets = new Node[capacity];
        this.size = 0;
    }

    public HashSet() {
        this(INITIAL_CAPACITY);
    }

    public HashSet(HashSet<T> hs) {
        this.buckets = hs.buckets;
        this.size = hs.size;
    }

    public HashSet(T[] arr) {
        this.buckets = new Node[arr.length];
        this.size = arr.length;

        for(T t : arr) {
            this.add(t);
        }
    }

    public void add(T t) {
        int index = t.hashCode() % buckets.length;

        Node bucket = buckets[index];

        Node<T> newNode = new Node<>(t);

        if(bucket == null) {
            buckets[index] = newNode;
            size++;
            return;
        }

        while(bucket.next != null) {
            if(bucket.data.equals(t)) {
                return;
            }

            bucket = bucket.next;
        }
    }

    public T remove (T t) {
        int index = t.hashCode() % buckets.length;

        Node bucket = buckets[index];

        if(bucket.data.equals(t)) {
            buckets[index] = bucket.next;
            size--;
            return t;
        }

        Node prev = bucket;

        while(bucket != null) {
            if(bucket.data.equals(t)) {
                prev.next = bucket.next;
                size--;
                return t;
            }

            prev = bucket;
            bucket = bucket.next;
        }
        
        return null;
    }

    public boolean contains(T t) {
        int index = t.hashCode() % buckets.length;

        Node bucket = buckets[index];

        while(bucket != null) {
            if(bucket.data.equals(t)) {
                return true;
            }

            bucket = bucket.next;
        }

        return false;
    }
    
    @Override
    public Iterator iterator() {
        return new HashSetIterator();
    }

    class HashSetIterator<T> implements Iterator<T> {
        private int currentBucket;
        private int previousBucket;
        private Node currentNode;
        private Node previousNode;

        public HashSetIterator() {
            currentNode = null;
            previousNode = null;
            currentBucket = -1;
            previousBucket = -1;
        }

        @Override
        public boolean hasNext() {
            if(currentNode != null && currentNode.next != null) {
                return true;
            }

            for(int i = currentBucket+1; i < buckets.length; i++) {
                if(buckets[i] != null) return true;
            }

            return false;
        }

        @Override
        public T next() {
            previousNode = currentNode;
            previousBucket = currentBucket;

            if(currentNode == null || currentNode.next == null) {
                currentBucket++;

                while(currentBucket < buckets.length && buckets[currentBucket] == null) {
                    currentBucket++;
                }

                if(currentBucket < buckets.length) {
                    currentNode = buckets[currentBucket];
                }
            } 
            else {
                currentNode = currentNode.next;
            }

            return (T) currentNode.data;
        }
    }

    private class Node<T> {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
}