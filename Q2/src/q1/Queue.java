package q1;

public interface Queue<T>{
	public long enq(T data);
	public T deq() throws EmptyQueueException;
}
