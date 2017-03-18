package com.hdaheizi.base.stl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行榜
 * 线程安全
 * @param <K>
 * @param <V>
 * @author daheiz
 * @Date 2017年3月12日 下午10:30:17
 */
public class Chart<K, V> {

	/** 内部值排行榜 */
	private Rank<UniqueValue> rank;

	/** 存储<key, 唯一值> */
	private Map<K, UniqueValue> map;

	/** 比较器 */
	private Comparator<? super V> comparator;

	/** 顺序id生成器 */
	private transient long orderGenerator;

	/** 顺序id生成器的初值 */
	private static final long ORDER_GENERATOR_INIT_VALUE = (long)1 << 63;

	/**
	 * 构造函数
	 */
	public Chart() {
		this(null);
	}

	/**
	 * @param comparator
	 */
	public Chart(Comparator<? super V> comparator) {
		this.comparator = comparator;
		this.rank = new RBTreeRank<>();
		this.map = new HashMap<>();
		initOrderGenerator();
	}

	/**
	 * 初始化顺序id生成器
	 * @Date 2017年3月14日 下午11:20:17
	 */
	private void initOrderGenerator() {
		orderGenerator = ORDER_GENERATOR_INIT_VALUE;
	}

	/**
	 * 比较两个值的大小
	 * @param v1
	 * @param v2
	 * @return
	 * @Date 2017年3月14日 下午11:03:37
	 */
	@SuppressWarnings("unchecked")
	private final int compare(V v1, V v2) {
		return comparator == null ? ((Comparable<? super V>)v1).compareTo(v2)
				: comparator.compare(v1, v2);
	}


	/**
	 * 唯一值类，为每个对象赋予一个唯一id，保证所有实例不相等，
	 * 且对于真值相等的对象，具有生成时间上的排序稳定性
	 * @author daheiz
	 * @Date 2017年3月14日 下午11:03:59
	 */
	class UniqueValue implements Comparable<UniqueValue> {
		/** 关键字 */
		K key;
		/** 真值 */
		V value;
		/** 唯一id */
		final long order;

		/**
		 * 构造函数
		 * @param key
		 * @param value
		 */
		UniqueValue(K key, V value) {
			this(key, value, orderGenerator++);
		}

		/**
		 * 构造函数
		 * @param key
		 * @param value
		 * @param order
		 */
		UniqueValue(K key, V value, long order) {
			this.key = key;
			this.value = value;
			this.order = order;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UniqueValue o) {
			int vcmp = compare(value, o.value);
			if (vcmp == 0) {
				if (order == o.order) {
					return 0;
				}
				return order < o.order ? -1 : 1;
			}
			return vcmp;
		}
	}


	/**
	 * 更新
	 * @param key
	 * @param value
	 * @Date 2017年3月14日 下午11:02:37
	 */
	public synchronized void put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		UniqueValue uniValue = map.get(key);
		if (uniValue != null && compare(uniValue.value, value) == 0) {
			return;
		}
		if (uniValue != null) {
			rank.remove(uniValue);
		}
		uniValue = new UniqueValue(key, value);
		map.put(key, uniValue);
		rank.add(uniValue);
	}

	/**
	 * 查找
	 * @param key
	 * @return <名次，值>
	 * @Date 2017年3月14日 下午11:02:16
	 */
	public synchronized Tuple<Integer, V> search(K key) {
		UniqueValue uniValue = map.get(key);
		if (uniValue == null) {
			return new Tuple<>(-1, null);
		}
		int r = rank.getRank(uniValue);
		return new Tuple<>(r, uniValue.value);
	}

	/**
	 * 移除
	 * @param key
	 * @return 移除前的值
	 * @Date 2017年3月14日 下午11:01:51
	 */
	public synchronized V remove(K key) {
		UniqueValue uniValue = map.remove(key);
		if (uniValue != null) {
			rank.remove(uniValue);
			return uniValue.value;
		}
		return null;
	}

	/**
	 * 返回一段连续的<key，value>列表，[start, end]
	 * @param start 起始名次(包含)
	 * @param end 终止名次(包含)
	 * @return
	 * @Date 2017年3月14日 下午4:26:44
	 */
	public synchronized List<Tuple<K, V>> getSequenceList(int start, int end) {
		List<Tuple<K, V>> list = new ArrayList<>();
		int size = size();
		start--;
		start = start < 0 ? 0 : (start > size ? size : start);
		RankIterator<UniqueValue> it = rank.rankIterator(start);
		while (it.hasNext() && it.nextRank() <= end) {
			UniqueValue univ = it.next();
			list.add(new Tuple<>(univ.key, univ.value));
		}
		return list;
	}

	/**
	 * 返回指定范围内的<key，value>列表，[low, high]
	 * @param low 低值(>=low)
	 * @param high 高值(<=high)
	 * @return
	 * @Date 2017年3月15日 下午3:39:23
	 */
	public synchronized List<Tuple<K, V>> getRangeList(V low, V high) {
		int start = rank.getRank(new UniqueValue(null, low, ORDER_GENERATOR_INIT_VALUE));
		if (start < 0) {
			start = -start;
		}
		int end = rank.getRank(new UniqueValue(null, high, ~ORDER_GENERATOR_INIT_VALUE));
		if (end < 0) {
			end = -end - 1;
		}
		return getSequenceList(start, end);
	}

	/**
	 * 是否存在于排行榜内
	 * @param key
	 * @return
	 * @Date 2017年3月15日 上午12:00:16
	 */
	public synchronized boolean contains(K key) {
		return map.containsKey(key);
	}

	/**
	 * 存储数据的数量
	 * @return
	 * @Date 2017年3月14日 下午11:12:53
	 */
	public synchronized int size() {
		return map.size();
	}

	/**
	 * 清空排行榜
	 * @Date 2017年3月14日 下午11:21:46
	 */
	public synchronized void clear() {
		map.clear();
		rank.clear();
		initOrderGenerator();
	}

	/**
	 * 单元测试
	 * @param args
	 * @Date 2017年3月14日 下午11:13:47
	 */
	public static void main(String[] args) {
		Chart<Integer, Integer> r = new Chart<>();
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

		// *****测试效率
		int num = 10000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		r.clear();
		System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");
		long ns1, ns2;
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i * 10);
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
		for (int i = 1; i < r.size(); i++) {
			r.getSequenceList(i - 1, i);
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
	}
}
