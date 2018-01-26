package org.daheizi.commons.stl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 带有索引的排行榜接口
 * 可存入索引不同，但值相等的数据
 * @param <K> 索引
 * @param <V> 值
 * @author daheizi
 * @Date 2017年3月23日 下午3:12:07
 */
public interface IChart<K, V> extends Iterable<Tuple<K, V>> {

    /**
     * 添加新的键值对
     * 若存在旧值且与新值相等，则仅替换，结构不变
     * 若存在旧值且与新值不等，则移除后再添加
     * @param key
     * @param value
     * @return 更新前的值，不存在则返回null
     * @Date 2017年3月23日 下午3:25:32
     */
    V put(K key, V value);

    /**
     * 移除键值对
     * @param key
     * @return 移除前的值，不存在则返回null
     * @Date 2017年3月14日 下午11:01:51
     */
    V remove(K key);

    /**
     * 查找关键字对应的值
     * @param key
     * @return
     * @Date 2017年3月22日 下午11:15:49
     */
    V get(K key);

    /**
     * 是否位于排行榜内
     * @param key
     * @return
     * @Date 2017年3月15日 上午12:00:16
     */
    boolean containsKey(K key);

    /**
     * 返回当前排行榜内数据量
     * @return
     * @Date 2017年3月14日 下午11:12:53
     */
    int size();

    /**
     * 判断排行榜是否为空
     * @return
     * @Date 2017年3月29日 上午1:43:16
     */
    default boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * 清空排行榜
     * @Date 2017年3月14日 下午11:21:46
     */
    void clear();

    /**
     * 查询关键字的排名
     * 不在排行榜内时返回 -1
     * @param key
     * @return
     * @Date 2017年3月28日 下午4:22:25
     */
    int getRank(K key);

    /**
     * 返回第kth名的键值对
     * 名次越界时返回 null
     * @param kth
     * @return <key，value>
     * @Date 2017年3月23日 下午3:26:46
     */
    Tuple<K, V> getKth(int kth);

    /**
     * 返回给定值在排行榜内应处的排名位置
     * @param value
     * @return {值<value的数目，值<=value的数目}
     * @Date 2017年3月24日 下午7:43:16
     */
    int[] getRankInfo(V value);

    /**
     * 返回一个有序的<Key, Value>数据迭代器
     * @see java.lang.Iterable#iterator()
     */
    Iterator<Tuple<K, V>> iterator();

    /**
     * 返回一个指定起始名次且有序的<Key, Value>数据迭代器
     * @param kth [0,size]，调用next()时返回的第一个Entry<K, V>的名次为 kth+1
     * @return
     * @Date 2017年3月28日 下午5:04:21
     */
    Iterator<Tuple<K, V>> iterator(int kth);

    /**
     * 搜索关键字的排行榜数据(名次和值)
     * @param key
     * @return <名次，值> 不在排行榜内则返回<-1, null>
     * @Date 2017年3月14日 下午11:02:16
     */
    default Tuple<Integer, V> search(K key) {
        V value = get(key);
        if (value != null) {
            return new Tuple<>(getRank(key), value);
        }
        return new Tuple<>(-1, null);
    }

    /**
     * 返回一段连续的<key，value>数据列表，[start, end]
     * @param start 起始名次(包含)
     * @param end 终止名次(包含)
     * @return
     * @Date 2017年3月14日 下午4:26:44
     */
    default List<Tuple<K, V>> getSequenceList(int start, int end) {
        List<Tuple<K, V>> list = new ArrayList<>();
        if (start < end) {
            int size = size();
            start--;
            start = start < 0 ? 0 : (start > size ? size : start);
            Iterator<Tuple<K, V>> it = iterator(start);
            int kth = start;
            while (it.hasNext() && ++kth <= end) {
                list.add(it.next());
            }
        }
        return list;
    }

    /**
     * 分页获取<key，value>数据列表
     * @param pageSize 分页大小
     * @param page 页数
     * @return
     * @Date 2017年3月25日 下午4:06:38
     */
    default List<Tuple<K, V>> getListByPage(int pageSize, int page) {
        int end = pageSize * page;
        return getSequenceList(end - pageSize + 1, end);
    }

    /**
     * 返回指定范围内的<key，value>数据列表，[low, high]
     * @param low 低值(>=low)
     * @param high 高值(<=high)
     * @return
     * @Date 2017年3月15日 下午3:39:23
     */
    default List<Tuple<K, V>> getRangeList(V low, V high) {
        int[] info1 = getRankInfo(low);
        int[] info2 = getRankInfo(high);
        return getSequenceList(info1[0] + 1, info2[1]);
    }

    /**
     * 返回指定关键字周围的一段<key，value>数据列表
     * @param key
     * @param left 左偏移名次
     * @param right 右偏移名次
     * @return
     * @Date 2017年3月28日 下午4:46:42
     */
    default List<Tuple<K, V>> getSurroundedByKey(K key, int left, int right) {
        int rank = getRank(key);
        if (rank == -1) {
            return new ArrayList<>();
        }
        return getSequenceList(rank - left, rank + right);
    }
}
