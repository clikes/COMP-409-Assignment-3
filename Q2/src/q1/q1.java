package q1;

 
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;





class Node<T>{
	T data;
	Node<T> next;
	public Node() {
		this.data = null;
		next = null;
		//head
	}
	
	public Node(T data) {
		this.data = data;
		next = null;
	}
	
	public void addTail(Node<T> next) {
		this.next = next;
	}
}

class UnboundedQueue<T> implements Queue<T>{
	private volatile Node<T> head,tail;
	private ReentrantLock enqLock, deqLock;
	public UnboundedQueue() {
		head = new Node<T>();
		tail = head;
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
	}
	
	public long enq(T x) {
		enqLock.lock();
		try {
			Node<T> element = new Node<T>(x);
			tail.addTail(element);
			tail = element;
		} finally {
			enqLock.unlock();
		}
		return System.nanoTime();
		 
	} 
	
	public T deq() throws EmptyQueueException{
		T result;
		deqLock.lock();
		try {
			if (head.next == null) {
				throw new EmptyQueueException();
			}
			result = head.next.data;
			head = head.next;
		} finally {
			deqLock.unlock();
		}
		
		return result;
	}
	
	
}

class LFnode<T>{
	T data;
	long inquene;
	long outquene;
	public volatile AtomicReference<LFnode<T>> next; 
	
	public LFnode(T data) {
		
		this.data = data;
		next = new AtomicReference<LFnode<T>>(null);
	}
}

class LockFreeQueue<T> implements Queue<T>{
	private volatile AtomicReference<LFnode<T>> head, tail;
	
	public LockFreeQueue() {
		head = new AtomicReference<LFnode<T>>(new LFnode<T>(null));
		tail = new AtomicReference<LFnode<T>>();
		tail.set(head.get());
	}
	
	public long enq(T data) {
		LFnode<T> node = new LFnode<T>(data);
		while(true) {
			LFnode<T> last = tail.get();
			LFnode<T> next = last.next.get();
			if (last == tail.get()) {
				if (next == null) {
					if (last.next.compareAndSet(next, node)) {
						tail.compareAndSet(last, node);
						//System.out.println(data);
						return System.nanoTime();
					}
				}else {
					tail.compareAndSet(last, next);
				}
			} 
			
		}
	}
	
	public T deq() throws EmptyQueueException{
		//System.out.println(3);
		while(true) {
			LFnode<T> first = head.get();
			LFnode<T> last = tail.get();
			LFnode<T> next = first.next.get();
			if (first == head.get()) {
				
				if (first == last) {
					//System.out.println(1);
					if (next == null) {
//						try {
//							Thread.currentThread().sleep(10);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						throw new EmptyQueueException();
					} 
					tail.compareAndSet(last, next);
				} else {
					T data = next.data;
					//System.out.println(1);
					if (head.compareAndSet(first, next)) {
						
						return data;
					}
				}
				
			} 
		}
	}
}

class inqueueThread extends Thread{
	AtomicInteger data;//data getter
	Queue<Long[]> queue;
	int n;
	public inqueueThread(AtomicInteger data, Queue<Long[]> queue, int n) {
		this.data = data;
		this.queue = queue;
		this.n = n;
	}
	@Override
	public void run() {
		while(true) {
			Long[] item = new Long[3];
			item[0] = (long) data.getAndIncrement();
			
			item[1] = queue.enq(item);//in queue time
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (q1.finishFlag) {
//				item[0] = (long) data.getAndIncrement();// insert one more prevent lock free queue bug
//				item[1] = queue.enq(item);//in queue time
//				try {
//					sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				break;
			}
		}
		
	}
}

class dequeueThread extends Thread{
	Queue<Long[]> queue;
	AtomicInteger dequearrayIndex;
	int n;
	public dequeueThread( Queue<Long[]> queue, AtomicInteger dequearrayIndex,int n) {
		//this.data = data;
		this.queue = queue;
		this.n = n;
		this.dequearrayIndex = dequearrayIndex;
	}
	@Override
	public void run() {
		int dequeued = 0;
		while(dequeued <n) {
			try {
				
				sleep(10);
				Long[] item = queue.deq();
				item[2] = System.nanoTime();
				//System.out.println(1);
				dequeued++;
				
				int index;
				
				index = dequearrayIndex.getAndIncrement();
				//System.out.println(index);
				q1.dequearray.compareAndSet(index, null, item);
			} catch (EmptyQueueException e) {
				//sleep(10);
				continue;
			} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

public class q1 {
	
	static volatile UnboundedQueue<Long[]> blockingQueue;
	static volatile LockFreeQueue<Long[]> lockFreeQueue;
	static AtomicReferenceArray<Long[]> dequearray;
	public static volatile boolean finishFlag;
	public static void main(String[] args) {
		int p = 0,q = 0,n = 0;
		try {
			p = Integer.parseInt(args[0]);
			q = Integer.parseInt(args[1]);
			n = Integer.parseInt(args[2]);
			if (p<0||q<0||n<0) {
				System.err.println("Please enter correct command!");
				return;
			}
		} catch (NumberFormatException e1) {
			System.err.println("Please enter correct command!");
		}
		System.out.println(p+" "+q+" "+n);
		
		blockingQueue = new UnboundedQueue<Long[]>();
		lockFreeQueue = new LockFreeQueue<Long[]>();
		ArrayList<Queue<Long[]>> testQueues = new ArrayList<Queue<Long[]>>();
		testQueues.add(blockingQueue);
		testQueues.add(lockFreeQueue);
		
//		long avaragelockfree = 0;
//		long avarageBlocking = 0;
		//for (int i1 = 0; i1 < 5; i1++) {
			
		
		for (Queue<Long[]> queue : testQueues) {
			//System.out.println("!");
			System.out.println(queue.getClass());
			finishFlag = false;
			dequearray = new AtomicReferenceArray<Long[]>(n*q);
			for (int i = 0; i < n ; i++) {
				dequearray.set(i, null);
			}
			AtomicInteger data = new AtomicInteger(0);
			AtomicInteger dequearrayIndex = new AtomicInteger(0);
			
			long time = System.currentTimeMillis();
			for (int i = 0; i < p; i++) {
				new inqueueThread(data, queue, n).start();
			}
			ArrayList<dequeueThread> dqThreads = new ArrayList<dequeueThread>();
			for (int i = 0; i < q; i++) {
				dequeueThread dequeueThread = new dequeueThread( queue, dequearrayIndex, n);
				dequeueThread.start();
				dqThreads.add(dequeueThread);
			}
			try {
				for (dequeueThread dequeueThread : dqThreads) {
					dequeueThread.join();
				}
				finishFlag = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time = System.currentTimeMillis()-time;
			
			System.out.println(time);
			if (queue.getClass() == blockingQueue.getClass()) {
				avarageBlocking+=time;
			} else {
				avaragelockfree+=time;
			}
			
			//sort and output
			boolean outputFlag = false;
			if (!outputFlag) {
				continue;
			}
			long[] enqTimes = new long[n];
			LinkedList<Long[]> sortList  = new LinkedList<Long[]>();
			boolean insertFlag = false;
			sortList.addFirst(dequearray.get(0));
			for (int i = 1; i < n ; i++) {
				
				Long[] item = dequearray.get(i);
				enqTimes[i] = item[1];
				//System.out.println("id: "+item[0]+" enqueT: "+item[1]+" dequeT: "+item[2]);
				//sorting the item
				for (int j = sortList.size() -1; j >=0 ; j--) {
					Long[] Item = sortList.get(j);
					if (Item[1]>item[1]) {
						sortList.add(j, item);
						insertFlag = true;
						break;
					}
				}
				if (insertFlag) {
					insertFlag = false;
					continue;
				}
				sortList.add(item);
			}
			
			for (Long[] item : sortList) {
				System.out.println("id: "+item[0]+" enqueT: "+item[1]+" dequeT: "+item[2] +" inqueuetime: "+(item[2]-item[1]));
			}
		}
	//	}
//		System.out.println(blockingQueue.getClass());
//		System.out.println(avarageBlocking/5);
//		System.out.println(lockFreeQueue.getClass());
//		System.out.println(avaragelockfree);
	}
}
