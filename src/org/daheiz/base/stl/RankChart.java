package org.daheiz.base.stl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 底层为IRank支持的带有索引的排行榜
 * 通过对相等数据附加不同orderId封装成UnequalValue，
 * 以保证存入IRank内的数据互不相等
 * 因此更加适用于可能存入大量相等的数据于排行榜内时
 * 非线程安全
 * @param <K>
 * @param <V>
 * @author daheiz
 * @Date 2017年3月12日 下午10:30:17
 */
public class RankChart<K, V> implements IChart<K, V> {

    /** 内部排行榜 */
    private IRank<UnequalValue> rank;

    /** 存储<k, UnequalValue> 的map */
    private Map<K, UnequalValue> map;

    /** 比较器 */
    private Comparator<? super V> comparator;

    /** 顺序id的最值 */
    private static final int MIN_ORDER_ID = 1 << 31;
    private static final int MAX_ORDER_ID = ~ (1 << 31);

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
     * 不等值类，为具有相等value值的对象赋予一个不等的id，
     * 保证存入排行榜IRank内的实例互不相等，
     * 且对于value值相等的对象，具有相对生成时间的排序稳定性
     * @author daheiz
     * @Date 2017年3月14日 下午11:03:59
     */
    final class UnequalValue implements Comparable<UnequalValue> {
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
        UnequalValue(K key, V value) {
            this(key, value, MIN_ORDER_ID);
        }

        /**
         * 构造函数
         * @param key
         * @param value
         * @param orderId
         */
        public UnequalValue(K key, V value, int orderId) {
            this.key = key;
            this.value = value;
            this.orderId = orderId;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(UnequalValue o) {
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
     * @see org.daheiz.base.stl.IChart#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException();
        }
        V preValue = null;
        UnequalValue uneValue = map.get(key);
        if (uneValue != null) {
            // 存在原记录
            preValue = uneValue.value;
            if (compare(value, uneValue.value) == 0) {
                // 原记录与新纪录相等，只需要替换value值
                uneValue.value = value;
                return preValue;
            } else {
                // 原记录与新纪录不等，移除原记录
                rank.remove(uneValue);
                uneValue.value = value;
            }
        } else {
            // 不存在原记录
            uneValue = new UnequalValue(key, value);
            map.put(key, uneValue);
        }
        // 设置合适的orderId，以保证排行榜内不存在相等的uneValue
        // 这里存在一些冗余的查询可能会影响效率
        uneValue.orderId = MAX_ORDER_ID;
        int lastPos = rank.getRank(uneValue);
        uneValue.orderId = MIN_ORDER_ID;
        if (lastPos > 0) {
            // orderId已达上限，需要重新按序调整
            int firstPos = rank.getRank(uneValue);
            RankIterator<UnequalValue> it = rank.rankIterator(Math.abs(firstPos) - 1);
            int newOrderId = MIN_ORDER_ID;
            while (it.nextRank() <= lastPos) {
                it.next().orderId = newOrderId++;
            }
            uneValue.orderId = newOrderId;
        } else if (lastPos < -1) {
            // 将orderId设置为现有与value值相等的所有数据中(最大的orderId) + 1
            UnequalValue preUniValue = rank.getKth(-lastPos - 1);
            if (compare(preUniValue.value, value) == 0) {
                uneValue.orderId = preUniValue.orderId + 1;
            }
        }
        rank.add(uneValue);
        return preValue;
    }

    /**
     * @see org.daheiz.base.stl.IChart#remove(java.lang.Object)
     */
    @Override
    public V remove(K key) {
        UnequalValue uneValue = map.remove(key);
        if (uneValue != null) {
            rank.remove(uneValue);
            return uneValue.value;
        }
        return null;
    }

    /**
     * @see org.daheiz.base.stl.IChart#get(java.lang.Object)
     */
    @Override
    public V get(K key) {
        UnequalValue uneValue = map.get(key);
        return uneValue == null ? null : uneValue.value;
    }

    /**
     * @see org.daheiz.base.stl.IChart#getRank(java.lang.Object)
     */
    @Override
    public int getRank(K key) {
        UnequalValue uneValue = map.get(key);
        return uneValue == null ? -1 : rank.getRank(uneValue);
    }

    /**
     * @see org.daheiz.base.stl.IChart#getKth(int)
     */
    @Override
    public Tuple<K, V> getKth(int kth) {
        if (kth > 0 && kth <= size()) {
            UnequalValue uneValue = rank.getKth(kth);
            return new Tuple<>(uneValue.key, uneValue.value);
        }
        return null;
    }

    /**
     * @see org.daheiz.base.stl.IChart#getRankInfo(java.lang.Object)
     */
    @Override
    public int[] getRankInfo(V value) {
        int[] info = new int[2];
        UnequalValue testValue = new UnequalValue(null, value, MIN_ORDER_ID);
        int start = rank.getRank(testValue);
        info[0] = start > 0 ? start - 1 : -start - 1;
        testValue.orderId = MAX_ORDER_ID;
        int end = rank.getRank(testValue);
        info[1] = end > 0 ? end : -end - 1;
        return info;
    }

    /**
     * 排行榜数据迭代器
     * @author daheiz
     * @Date 2017年3月28日 下午6:08:46
     */
    private class Itr implements Iterator<Tuple<K, V>> {

        /** 内部排行榜迭代器 */
        private RankIterator<UnequalValue> rankItr;

        /** 前一个UniValue */
        private UnequalValue lastValue;

        /**
         * 构造函数
         * @param kth
         */
        Itr(int kth) {
            this.rankItr = rank.rankIterator(kth);
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return rankItr.hasNext();
        }

        /**
         * @see java.util.Iterator#next()
         */
        @Override
        public Tuple<K, V> next() {
            lastValue = rankItr.next();
            return new Tuple<>(lastValue.key, lastValue.value);
        }

        /**
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            rankItr.remove();
            if (lastValue != null) {
                map.remove(lastValue.key);
                lastValue = null;
            }
        }
    }

    /**
     * @see org.daheiz.base.stl.IChart#iterator()
     */
    @Override
    public Iterator<Tuple<K, V>> iterator() {
        return iterator(0);
    }

    /**
     * @see org.daheiz.base.stl.IChart#iterator(int)
     */
    @Override
    public Iterator<Tuple<K, V>> iterator(int kth) {
        return new Itr(kth);
    }

    /**
     * @see org.daheiz.base.stl.IChart#getRangeList(java.lang.Object, java.lang.Object)
     */
    @Override
    public List<Tuple<K, V>> getRangeList(V low, V high) {
        int start = rank.getRank(new UnequalValue(null, low, MIN_ORDER_ID));
        start = start > 0 ? start : -start;
        int end = rank.getRank(new UnequalValue(null, high, MAX_ORDER_ID));
        end = end > 0 ? end : -end - 1;
        return getSequenceList(start, end);
    }

    /**
     * @see org.daheiz.base.stl.IChart#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * @see org.daheiz.base.stl.IChart#size()
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * @see org.daheiz.base.stl.IChart#clear()
     */
    @Override
    public void clear() {
        map.clear();
        rank.clear();
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
        System.out.println(r2.put(50, new Tuple<>(50, 90)));
        System.out.println(r2.put(60, new Tuple<>(50, 90)));
        System.out.println(r2.put(150, new Tuple<>(20, 90)));
        System.out.println(r2.put(3, new Tuple<>(10, 90)));
        System.out.println(Arrays.toString(r2.getSequenceList(-3, 6).toArray()));
        System.out.println(r2.remove(2));
        System.out.println(r2.remove(16));
        Iterator<Tuple<Integer, Tuple<Integer, Integer>>> it = r2.iterator(15);
        while (it.hasNext()) {
            Tuple<Integer, Tuple<Integer, Integer>> e = it.next();
            if (e.left >= 15 && e.left < 98) {
                it.remove();
            }
        }
        System.out.println(r2.remove(99));
        System.out.println(r2.getKth(0));
        System.out.println(r2.getKth(1));
        System.out.println(r2.getKth(2));
        System.out.println(r2.getKth(3));
        System.out.println(r2.getKth(15));
        System.out.println(r2.getKth(30));
        System.out.println(r2.size());
        System.out.println(Arrays.toString(r2.getSequenceList(0, 100).toArray()));
        System.out.println(Arrays.toString(r2.getRangeList(new Tuple<>(80, 0), new Tuple<>(100, 0)).toArray()));
        for (int i = 5; i < 15; i++) {
            r2.put(i, new Tuple<>(i, i * 10 - i));
        }

        //        System.exit(0);

        // *****测试效率
        IChart<Integer, Integer> r = new RankChart<>();
        int num = 5000000;
        Random random = new Random();
        for (int i = 0; i < num; ++i) {
            r.put(i, random.nextInt(num));
        }
        int times = num / 100;
        System.out.println("****test speed, num :" + num + " ,time unit: (ns)");

        long ns1, ns2;
        // 插入
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            int x = random.nextInt(num);
            int y = random.nextInt(num) / 100;
            r.put(x, y);
        }
        ns2 = System.nanoTime();
        System.out.println("put: " + (ns2 - ns1) / times);

        // 查找
        ns1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            r.getRank(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("getRank: " + (ns2 - ns1) / times);

        // 名次
        ns1 = System.nanoTime();
        for (int i = 1; i <= times; i++) {
            r.getKth(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("getKth:" + (ns2 - ns1) / times);

        // 删除
        ns1 = System.nanoTime();
        for (int i = 1; i <= times; i++) {
            r.remove(random.nextInt(num));
        }
        ns2 = System.nanoTime();
        System.out.println("delete:" + (ns2 - ns1) / times);

        System.exit(0);
    }
}
