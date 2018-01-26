package net.daheizi.commons.stl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * 基于LinkedList的排行榜
 * @param <K>
 * @author daheizi
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
     * @see net.daheizi.commons.stl.IRank#add(java.lang.Object)
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
     * @see net.daheizi.commons.stl.IRank#remove(java.lang.Object)
     */
    @Override
    public boolean remove(K key) {
        return list.remove(key);
    }

    /**
     * @see net.daheizi.commons.stl.IRank#contains(java.lang.Object)
     */
    @Override
    public boolean contains(K key) {
        return list.contains(key);
    }

    /**
     * @see net.daheizi.commons.stl.IRank#getRank(java.lang.Object)
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
