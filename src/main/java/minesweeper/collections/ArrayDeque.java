package minesweeper.collections;

public class ArrayDeque<T> {
    private T[] data;
    private static final int INITIAL_CAPACITY = 5;
    private int front = 0;
    private int back = 0;

    private int size = 0;


    public ArrayDeque(){
        this(INITIAL_CAPACITY);
    }

    public ArrayDeque(int capacity){
        data = (T[]) new Object[capacity];
    }

    public ArrayDeque(ArrayDeque deque) {
        data = (T[]) deque.data;
        front = deque.front;
        back = deque.back;
        size = deque.size;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void push(T t) {
        if (isEmpty()) {
			data[front] = t;
		} else {
			front = (front - 1 + data.length)  % data.length;
			data[front] = t;
		}
		size++;
    }

    public T pop() {
        T t = data[front];

        front = (front + 1) % data.length;
        System.out.println(front);
        size--;

        return t;
    }
}