package com.hdaheizi.base.stl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 基于LinkedList的排行榜
 * @param <K>
 * @author daheiz
 * @Date 2017年3月11日 下午9:46:51
 */
public class LinkedListRank<K> extends AbstractListRank<K> {
	
	/**
	 * 构造函数
	 */
	public LinkedListRank() {
		this(null);
	}
	
	/**
	 * 构造函数
	 * @param comparator
	 */
	public LinkedListRank(Comparator<? super K> comparator) {
		super(comparator);
		this.list = new LinkedList<>();
	}
    
    /**
     * @see com.hdaheizi.base.stl.IRank#add(java.lang.Object)
     */
    @Override
    public boolean add(K key) {
    	ListIterator<K> it = list.listIterator();
    	int cmp;
    	while (it.hasNext()) {
    		K k = it.next();
    		cmp = compare(k, key);
    		if (cmp == 0){
    			return false;
    		} else if (cmp > 0) {
    			it.set(key);
    			it.add(k);
    			return true;
    		}
    	}
    	it.add(key);
    	return true;
    }
    
    /**
     * @see com.hdaheizi.base.stl.IRank#remove(java.lang.Object)
     */
    @Override
    public boolean remove(K key) {
    	return list.remove(key);
	}
    
    /**
     * @see com.hdaheizi.base.stl.IRank#contains(java.lang.Object)
     */
    @Override
    public boolean contains(K key) {
    	return list.contains(key);
    }
    
    /**
     * @see com.hdaheizi.base.stl.IRank#getKth(int)
     */
    @Override
    public K getKth(int kth) {
    	if (kth > 0 && kth <= list.size()) {
    		return list.get(kth - 1);
    	}
    	return null;
    }
    
    /**
     * @see com.hdaheizi.base.stl.IRank#getRank(java.lang.Object)
     */
    @Override
    public int getRank(K key) {
    	int cmp;
    	ListIterator<K> it = list.listIterator();
    	while (it.hasNext()) {
    		cmp = compare(key, it.next());
    		if (cmp == 0) {
    			return it.nextIndex();
    		} else if (cmp < 0) {
    			return -it.nextIndex();
    		}
    	}
    	return -it.nextIndex() - 1;
    }
    
    
    /**
     * 单元测试
     * @param args
     * @Date 2017年3月11日 下午9:47:38
     */
    public static void main(String[] args) {
    	LinkedListRank<Integer> r = new LinkedListRank<>();
		int num = 10000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		List<Integer> li2 = li.subList(1, 31);
		
		// *****测试正确性
		System.out.println("****test correctness, num :" + li2.size());
		// 添加
		Collections.shuffle(li2);
		for (Integer i : li2) {
			r.add(i);
		}
		System.out.println(Arrays.toString(r.toArray()));
		System.out.println("the 23th is : " + r.getKth(23));
		r.remove(23);
		r.remove(24);
		r.add(8);
		r.add(9);
		
		System.out.println(Arrays.toString(r.toArray()));
		
		System.out.println("23 is at the rank: " + r.getRank(23));
		System.out.println("25 is at the rank: " + r.getRank(25));
		System.out.println("0 is at the rank: " + r.getRank(0));
		System.out.println("100 is at the rank: " + r.getRank(100));
		// 移除
		Collections.shuffle(li2);
		for (Integer i : li2) {
			r.remove(i);
		}
		System.out.println(Arrays.toString(r.toArray()));
		
		
		// *****测试效率
		r.clear();
		System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");
		long ns1, ns2;
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.add(i);
		}
		ns2 = System.nanoTime();
		System.out.println("add: " + (ns2 - ns1) / num);
		// 查找
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.contains(i);
		}
		ns2 = System.nanoTime();
		System.out.println("contains: " + (ns2 - ns1) / num);
		// 名次
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.getRank(i);
		}
		ns2 = System.nanoTime();
		System.out.println("rank:" + (ns2 - ns1) / num);
		// 顺次
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (int i = 1; i < r.size(); i++) {
			r.getKth(i);
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
