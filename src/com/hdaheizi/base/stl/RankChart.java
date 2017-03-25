package com.hdaheizi.base.stl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 底层为IRank支持的带有索引的排行榜
 * 通过对相等的数据附加不同orderId并封装成UniqueValue，
 * 以保证存入IRank内的数据互不相等
 * 线程安全
 * @param <K>
 * @param <V>
 * @author daheiz
 * @Date 2017年3月12日 下午10:30:17
 */
public class RankChart<K, V> implements IChart<K, V> {

	/** 内部排行榜 */
	private IRank<UniqueValue> rank;

	/** 存储<k, UniqueValue> 的map */
	private Map<K, UniqueValue> map;

	/** 比较器 */
	private Comparator<? super V> comparator;

	/** 顺序id的最值 */
	private static final int MIN_ORDER_ID = 1 << 31;
	private static final int MAX_ORDER_ID = ~ (1 << 31);

	/** 读写锁 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock rl = lock.readLock();
	private final Lock wl = lock.writeLock();


	/**
	 * 构造函数
	 */
	public RankChart() {
		this(null);
	}

	/**
	 * @param comparator
	 */
	public RankChart(Comparator<? super V> comparator) {
		this.comparator = comparator;
		this.rank = new RBTreeRank<>();
		this.map = new HashMap<>();
	}

	/**
	 * 比较两个值的大小
	 * @param v1
	 * @param v2
	 * @return
	 * @Date 2017年3月14日 下午11:03:37
	 */
	@SuppressWarnings("unchecked")
	final int compare(V v1, V v2) {
		return comparator == null ? ((Comparable<? super V>)v1).compareTo(v2)
				: comparator.compare(v1, v2);
	}


	/**
	 * 唯一值类，为每个对象赋予一个唯一id，保证所有实例不相等，
	 * 且对于真值相等的对象，具有生成时间上的排序稳定性
	 * @author daheiz
	 * @Date 2017年3月14日 下午11:03:59
	 */
	final class UniqueValue implements Comparable<UniqueValue> {
		/** 关键字 */
		K key;
		/** 真值 */
		V value;
		/** 唯一id */
		int orderId;

		/**
		 * 构造函数
		 * @param key
		 * @param value
		 */
		UniqueValue(K key, V value) {
			this(key, value, MIN_ORDER_ID);
		}

		/**
		 * 构造函数
		 * @param key
		 * @param value
		 * @param orderId
		 */
		public UniqueValue(K key, V value, int orderId) {
			this.key = key;
			this.value = value;
			this.orderId = orderId;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UniqueValue o) {
			int vcmp = compare(value, o.value);
			if (vcmp == 0) {
				if (orderId == o.orderId) {
					return 0;
				}
				return orderId < o.orderId ? -1 : 1;
			}
			return vcmp;
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		wl.lock();
		try {
			V preValue = null;
			UniqueValue uniValue = map.get(key);
			if (uniValue != null) {
				// 存在原记录
				preValue = uniValue.value;
				if (compare(value, uniValue.value) == 0) {
					// 原记录与新纪录相等，只需要替换value值
					uniValue.value = value;
					return preValue;
				} else {
					// 原记录与新纪录不等，移除原记录
					rank.remove(uniValue);
					uniValue.value = value;
				}
			} else {
				// 不存在原记录
				uniValue = new UniqueValue(key, value);
				map.put(key, uniValue);
			}
			// 设置合适的orderId，以保证排行榜内不存在相等的uniValue
			// 这里存在一些冗余的查询可能会影响效率
			uniValue.orderId = MAX_ORDER_ID;
			int lastPos = rank.getRank(uniValue);
			uniValue.orderId = MIN_ORDER_ID;
			if (lastPos > 0) {
				// orderId已达上限，需要重新按序调整
				int firstPos = rank.getRank(uniValue);
				RankIterator<UniqueValue> it = rank.rankIterator(Math.abs(firstPos) - 1);
				int newOrderId = MIN_ORDER_ID;
				while (it.nextRank() <= lastPos) {
					it.next().orderId = newOrderId++;
				}
				uniValue.orderId = newOrderId;
			} else if (lastPos < -1) {
				// 将orderId设置为现有与value值相等的所有数据中(最大的orderId) + 1
				UniqueValue preUniValue = rank.getKth(-lastPos - 1);
				if (compare(preUniValue.value, value) == 0) {
					uniValue.orderId = preUniValue.orderId + 1;
				}
			}
			rank.add(uniValue);
			return preValue;
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
			UniqueValue uniValue = map.remove(key);
			if (uniValue != null) {
				rank.remove(uniValue);
				return uniValue.value;
			}
			return null;
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
			UniqueValue uniValue = map.get(key);
			return uniValue == null ? null : uniValue.value;
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
			UniqueValue uniValue = map.get(key);
			if (uniValue == null) {
				return new Tuple<>(-1, null);
			}
			int r = rank.getRank(uniValue);
			return new Tuple<>(r, uniValue.value);
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
			if (kth > 0 && kth <= size()) {
				UniqueValue uniValue = rank.getKth(kth);
				return new Tuple<>(uniValue.key, uniValue.value);
			}
			return null;
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getSequenceList(int, int)
	 */
	@Override
	public List<Tuple<K, V>> getSequenceList(int start, int end) {
		rl.lock();
		try {
			List<Tuple<K, V>> list = new ArrayList<>();
			int size = size();
			start = start < 0 ? 0 : (start > size ? size : start);
			RankIterator<UniqueValue> it = rank.rankIterator(start);
			while (it.hasNext() && it.nextRank() <= end) {
				UniqueValue univ = it.next();
				list.add(new Tuple<>(univ.key, univ.value));
			}
			return list;
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
			int start = rank.getRank(new UniqueValue(null, low, MIN_ORDER_ID));
			start = start > 0 ? start - 1 : -start - 1;
			int end = rank.getRank(new UniqueValue(null, high, MAX_ORDER_ID));
			end = end > 0 ? end : -end - 1;
			return getSequenceList(start, end);
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
			return map.containsKey(key);
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
			return map.size();
		} finally {
			rl.unlock();
		}
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#clear()
	 */
	@Override
	public void clear() {
		wl.lock();
		try {
			map.clear();
			rank.clear();
		} finally {
			wl.unlock();
		}
	}


	/**
	 * 单元测试
	 * @param args
	 * @Date 2017年3月14日 下午11:13:47
	 */
	public static void main(String[] args) {
		// *****测试正确性
		IChart<Integer, Tuple<Integer, Integer>> r2 = new RankChart<>(
				new Comparator<Tuple<Integer, Integer>>() {

					@Override
					public int compare(Tuple<Integer, Integer> o1,
							Tuple<Integer, Integer> o2) {
						return o1.left + o1.right - o2.left - o2.right;
					}
				});

		for (int i = 1; i < 100; i++) {
			r2.put(i, new Tuple<>(i, 100 - i));
		}
		System.out.println(r2.put(1, new Tuple<>(8, 5)));
		System.out.println(r2.put(2, new Tuple<>(10, 80)));
		System.out.println(r2.put(3, new Tuple<>(300, 0)));
		System.out.println(r2.put(50, new Tuple<>(50, 51)));
		System.out.println(r2.put(3, new Tuple<>(10, 90)));
		System.out.println(Arrays.toString(r2.getSequenceList(-3, 6).toArray()));

		IChart<Integer, Integer> r = new RankChart<>();
		r.put(1, 30);
		r.put(2, 40);
		r.put(3, 30);
		r.put(2, 40);
		r.put(2, 15);
		for (int id = 4; id < 30; id++) {
			r.put(id, id * 10);
		}

		System.out.println(Arrays.toString(r.getSequenceList(9, 12).toArray()));
		System.out.println(Arrays.toString(r.getRangeList(30, 60).toArray()));

		int num = 1000000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		// *****测试效率
		r.clear();
		System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");
		long ns1, ns2;
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 10);
		}
		ns2 = System.nanoTime();
		System.out.println("put: " + (ns2 - ns1) / num);

		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 10 + 1);
		}
		ns2 = System.nanoTime();
		System.out.println("put: " + (ns2 - ns1) / num);

		// 查找
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.search(i);
		}
		ns2 = System.nanoTime();
		System.out.println("search: " + (ns2 - ns1) / num);
		// 顺次
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (int i = 1; i <= num; i++) {
			r.getSequenceList(i, i);
		}
		ns2 = System.nanoTime();
		System.out.println("kth:" + (ns2 - ns1) / num);
		// 删除
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.remove(i);
		}
		ns2 = System.nanoTime();
		System.out.println("delete:" + (ns2 - ns1) / num);

		System.exit(0);

		// *****压力测试
		System.out.println("****test synchronization, num :" + li.size() + " ,time unit: (ns)");
		// 准备数据
		r.clear();
		Collections.shuffle(li);
		for (Integer i : li) {
			r.put(i, i);
		}
		Random random = new Random();
		int times = num / 1000;
		// 添加
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.put(x, x / 50);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("put1: " + ns / times);
			}
		});
		// 添加
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.put(x, x + 1);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("put2: " + ns / times);
			}
		});
		// 移除
		Thread t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.remove(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("remove1: " + ns / times);
			}
		});
		// 移除
		Thread t4 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.remove(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("remove2: " + ns / times);
			}
		});
		// 查询
		Thread t5 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.search(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("search1: " + ns / times);
			}
		});
		// 查询
		Thread t6 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.search(x);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("search2: " + ns / times);
			}
		});
		// 获取
		Thread t7 = new Thread(new Runnable() {
			@Override
			public void run() {
				long ns1, ns2, ns = 0;
				for (int i = 0; i < times; i++) {
					int index = random.nextInt(a.length);
					int x = a[index];
					ns1 = System.nanoTime();
					r.getSequenceList(x, x + 50);
					ns2 = System.nanoTime();
					ns += ns2 - ns1;
				}
				System.out.println("sequenece1: " + ns / times);
			}
		});

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
	}
}
