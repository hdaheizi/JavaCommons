package com.hdaheizi.base.stl;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全的排行榜
 * 使用读写锁保护内部排行榜IChart
 * @author daheiz
 * @Date 2017年3月25日 下午6:08:47
 */
public class ConcurrentChart<K, V> implements IChart<K, V> {

	/** 内部排行榜Chart */
	IChart<K, V> chart;
	
	/** 读写锁 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock rl = lock.readLock();
	private final Lock wl = lock.writeLock();
	
	/**
	 * 构造函数
	 * @param chart
	 */
	public ConcurrentChart (IChart<K, V> chart) {
		this.chart = chart;
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		wl.lock();
		try {
			return chart.put(key, value);
		} finally {
			wl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#remove(java.lang.Object)
	 */
	@Override
	public V remove(K key) {
		wl.lock();
		try {
			return chart.remove(key);
		} finally {
			wl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#get(java.lang.Object)
	 */
	@Override
	public V get(K key) {
		rl.lock();
		try {
			return chart.get(key);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(K key) {
		rl.lock();
		try {
			return chart.containsKey(key);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#size()
	 */
	@Override
	public int size() {
		rl.lock();
		try {
			return chart.size();
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#clear()
	 */
	@Override
	public void clear() {
		rl.lock();
		try {
			chart.clear();
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getRank(java.lang.Object)
	 */
	@Override
	public int getRank(K key) {
		rl.lock();
		try {
			return chart.getRank(key);
		} finally {
			rl.unlock();
		}
	}
	
	/**
	 * @see com.hdaheizi.base.stl.IChart#search(java.lang.Object)
	 */
	@Override
	public Tuple<Integer, V> search(K key) {
		rl.lock();
		try {
			return chart.search(key);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getKth(int)
	 */
	@Override
	public Tuple<K, V> getKth(int kth) {
		rl.lock();
		try {
			return chart.getKth(kth);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getRankInfo(java.lang.Object)
	 */
	@Override
	public int[] getRankInfo(V value) {
		rl.lock();
		try {
			return chart.getRankInfo(value);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#iterator()
	 */
	@Override
	public Iterator<Tuple<K, V>> iterator() {
		return iterator(0);
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#iterator(int)
	 */
	@Override
	public Iterator<Tuple<K, V>> iterator(int kth) {
		throw new UnsupportedOperationException("iterator");
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getSequenceList(int, int)
	 */
	@Override
	public List<Tuple<K, V>> getSequenceList(int start, int end) {
		rl.lock();
		try {
			return chart.getSequenceList(start, end);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getListByPage(int, int)
	 */
	@Override
	public List<Tuple<K, V>> getListByPage(int pageSize, int page) {
		rl.lock();
		try {
			return chart.getListByPage(pageSize, page);
		} finally {
			rl.unlock();
		}
	}
	
	/**
	 * @see com.hdaheizi.base.stl.IChart#getRangeList(java.lang.Object, java.lang.Object)
	 */
	@Override
	public List<Tuple<K, V>> getRangeList(V low, V high) {
		rl.lock();
		try {
			return chart.getRangeList(low, high);
		} finally {
			rl.unlock();
		}
	}
	
	/**
	 * @see com.hdaheizi.base.stl.IChart#getSurroundedByKey(java.lang.Object, int, int)
	 */
	@Override
	public List<Tuple<K, V>> getSurroundedByKey(K key, int left, int right) {
		rl.lock();
		try {
			return chart.getSurroundedByKey(key, left, right);
		} finally {
			rl.unlock();
		}
	}
	
	/**
	 * 单元测试
	 * @param args
	 * @Date 2017年3月25日 下午6:26:04
	 */
	public static void main(String[] args) {
		IChart<Integer, Integer> r = new ConcurrentChart<>(new RBTreeChart<Integer, Integer>());
		
		// *****压力测试
		List<Long> timePool = new CopyOnWriteArrayList<>();
		int num = 5000000;
		int density = 64;
		int maxValue = num / density;
		System.out.println("****压力测试: 数据量 :" + num + ", 数据密度 :" + density + ", 线程数目：10 , 时间单位: (纳秒)");
		// 准备数据
		Random random = new Random();
		for (int i = 0; i < num; ++i) {
			r.put(i, random.nextInt(maxValue));
		}
		int times = num / 100;
		long tn1 = System.nanoTime();
		// 添加1
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					int y = random.nextInt(maxValue);
					ns1 = System.nanoTime();
					r.put(x, y);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("put1: " + ns / times);
			}
		});
		// 添加2
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					int y = random.nextInt(maxValue) + 23;
					ns1 = System.nanoTime();
					r.put(x, y);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("put2: " + ns / times);
			}
		});
		// 移除1
		Thread t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					ns1 = System.nanoTime();
					r.remove(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("remove1: " + ns / times);
			}
		});
		// 移除2
		Thread t4 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num) * 2;
					ns1 = System.nanoTime();
					r.remove(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("remove2: " + ns / times);
			}
		});
		// 查询1
		Thread t5 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					ns1 = System.nanoTime();
					r.getRank(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("getRank1: " + ns / times);
			}
		});
		// 查询2
		Thread t6 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					ns1 = System.nanoTime();
					r.getRank(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("getRank1: " + ns / times);
			}
		});
		// 名次1
		Thread t7 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					ns1 = System.nanoTime();
					r.getKth(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("getKth1: " + ns / times);
			}
		});
		// 添加3
		Thread t8 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num) * 2;
					ns1 = System.nanoTime();
					r.put(x, x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("put3: " + ns / times);
			}
		});
		// 添加4
		Thread t9 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num) * 4;
					ns1 = System.nanoTime();
					r.put(x, x + 50);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("put4: " + ns / times);
			}
		});
		// 名次2
		Thread t10 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int x = random.nextInt(num);
					ns1 = System.nanoTime();
					r.getKth(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				timePool.add(ns);
				System.out.println("getKth2: " + ns / times);
			}
		});
		
		t5.start();
		t6.start();
		t7.start();
		
		t1.start();
		t4.start();
		t2.start();
		t3.start();
		
		t10.start();
		t9.start();
		t8.start();
		
		while (timePool.size() < 10) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long tn2 = System.nanoTime();
		
		long total = 0;
		for (long t : timePool) {
			total += t;
		}
		long ave1 = total / (timePool.size() * times);
		long ave2 = (tn2 - tn1) / (timePool.size() * times);
		System.out.println("****平均处理单个请求用时(纳秒/次): " + ave1);
		System.out.println("****平均每秒处理请求数: " + Math.pow(10, 9) / ave2);
	}
}
