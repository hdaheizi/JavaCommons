package com.hdaheizi.base.stl;

import java.util.List;

/**
 * 带有索引的排行榜接口
 * 可存入相等的数据
 * @param <K>
 * @param <V>
 * @author daheiz
 * @Date 2017年3月23日 下午3:12:07
 */
public interface IChart<K, V> {

	/**
	 * 更新
	 * @param key
	 * @param value
	 * @return 更新前的值
	 * @Date 2017年3月23日 下午3:25:32
	 */
	V put(K key, V value);

	/**
	 * 移除
	 * @param key
	 * @return 更新前的值
	 * @Date 2017年3月14日 下午11:01:51
	 */
	V remove(K key);

	/**
	 * 查找
	 * @param key
	 * @return
	 * @Date 2017年3月22日 下午11:15:49
	 */
	V get(K key);

	/**
	 * 是否存在于排行榜内
	 * @param key
	 * @return
	 * @Date 2017年3月15日 上午12:00:16
	 */
	boolean containsKey(K key);

	/**
	 * 当前排行榜内数据量
	 * @return
	 * @Date 2017年3月14日 下午11:12:53
	 */
	int size();

	/**
	 * 清空排行榜
	 * @Date 2017年3月14日 下午11:21:46
	 */
	void clear();

	/**
	 * 搜索
	 * @param key
	 * @return <名次，值> 若不在排行榜内，则返回<-1, null>
	 * @Date 2017年3月14日 下午11:02:16
	 */
	Tuple<Integer, V> search(K key);

	/**
	 * 返回第kth名的数据,名次越界时返回null
	 * @param rank
	 * @return <key，value>
	 * @Date 2017年3月23日 下午3:26:46
	 */
	Tuple<K, V> getKth(int rank);

	/**
	 * 返回一段连续的<key，value>数据列表，(start, end]
	 * @param start 起始名次(不包含)
	 * @param end 终止名次(包含)
	 * @return
	 * @Date 2017年3月14日 下午4:26:44
	 */
	List<Tuple<K, V>> getSequenceList(int start, int end);
	
	/**
	 * 分页获取<key，value>数据列表
	 * @param pageSize 分页大小
	 * @param page 页数
	 * @return
	 * @Date 2017年3月25日 下午4:06:38
	 */
	default List<Tuple<K, V>> getListByPage(int pageSize, int page) {
		int end = pageSize * page;
		return getSequenceList(end - pageSize, end);
	}

	/**
	 * 返回指定范围内的<key，value>数据列表，[low, high]
	 * @param low 低值(>=low)
	 * @param high 高值(<=high)
	 * @return
	 * @Date 2017年3月15日 下午3:39:23
	 */
	List<Tuple<K, V>> getRangeList(V low, V high);
}
