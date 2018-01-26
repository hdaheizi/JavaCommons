package org.daheizi.commons.stl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 基于ArrayList的排行榜
 * @param <K>
 * @author daheizi
 * @Date 2017年3月11日 下午9:46:51
 */
public class ArrayListRank<K> extends AbstractListRank<K> {

    /**
     * 构造函数
     */
    public ArrayListRank() {
        this(null);
    }

    /**
     * @param comparator
     */
    public ArrayListRank(Comparator<? super K> comparator) {
        super(comparator);
        this.list = new ArrayList<>();
    }


    /**
     * @see org.daheizi.commons.stl.IRank#add(java.lang.Object)
     */
    @Override
    public boolean add(K key) {
        int index = search(key);
        if (index >= 0) {
            return false;
        } else {
            list.add(-index - 1, key);
            return true;
        }
    }

    /**
     * 查找关键字所在位置
     * @param key
     * @return 如果找到，则返回关键字所在位置的索引，
     *              如果未找到，则返回((-插入点索引) - 1)
     * @Date 2017年3月12日 下午9:19:36
     */
    private int search(K key) {
        if (comparator == null) {
            @SuppressWarnings("unchecked")
            List<Comparable<? super K>> _list = (List<Comparable<? super K>>) list;
            return Collections.binarySearch(_list, key);
        } else {
            return Collections.binarySearch(list, key, comparator);
        }
    }

    /**
     * @see org.daheizi.commons.stl.IRank#remove(java.lang.Object)
     */
    @Override
    public boolean remove(K key) {
        int index = search(key);
        if (index >= 0) {
            list.remove(index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see org.daheizi.commons.stl.IRank#contains(java.lang.Object)
     */
    @Override
    public boolean contains(K key) {
        return search(key) >= 0;
    }

    /**
     * @see org.daheizi.commons.stl.IRank#getRank(java.lang.Object)
     */
    @Override
    public int getRank(K key) {
        int index = search(key);
        return index >= 0 ? index + 1 : index;
    }


    /**
     * 单元测试
     * @param args
     * @Date 2017年3月11日 下午9:47:38
     */
    public static void main(String[] args) {
        ArrayListRank<Integer> r = new ArrayListRank<>();
        int num = 100000;
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
        // 准备数据
        Collections.shuffle(li);
        for (Integer i : li) {
            r.add(i);
        }
        int times = num / 1000;
        Random random = new Random();
        System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");
        long ns1, ns2;
        // 插入
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.add(random.nextInt(num * 2));
        }
        ns2 = System.nanoTime();
        System.out.println("add: " + (ns2 - ns1) / times);
        // 查找
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.contains(random.nextInt(num * 2));
        }
        ns2 = System.nanoTime();
        System.out.println("contains: " + (ns2 - ns1) / times);
        // 名次
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.getRank(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("rank:" + (ns2 - ns1) / times);
        // 顺次
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.getKth(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("kth:" + (ns2 - ns1) / times);
        // 删除
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.remove(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("delete:" + (ns2 - ns1) / times);
    }
}
