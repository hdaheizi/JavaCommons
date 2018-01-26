package net.daheizi.commons.stl;

import java.util.Iterator;

/**
 * 排行榜迭代器
 * @param <E>
 * @author daheizi
 * @Date 2017年3月13日 下午9:12:11
 */
public interface RankIterator<E> extends Iterator<E> {

    /**
     * 是否存在前一个元素
     * @return
     * @Date 2017年3月13日 下午9:12:19
     */
    boolean hasPrevious();

    /**
     * 移动到前一个元素
     * @return
     * @Date 2017年3月13日 下午9:12:31
     */
    E previous();

    /**
     * 返回后一个元素的名次
     * @return
     * @Date 2017年3月13日 下午10:13:53
     */
    int nextRank();

    /**
     * 返回前一个元素的名次
     * @return
     * @Date 2017年3月13日 下午10:13:50
     */
    int previousRank();

}
