package com.hdaheizi.base.stl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 抽象List排行榜
 * @param <K>
 * @author daheiz
 * @Date 2017年3月14日 下午7:01:01
 */
public abstract class AbstractListRank<K> implements IRank<K> {

	/** list */
	protected List<K> list;

	/** comparator */
	protected Comparator<? super K> comparator;

	/**
	 * @param comparator
	 */
	public AbstractListRank(Comparator<? super K> comparator) {
		this.comparator = comparator;
	}

	@SuppressWarnings("unchecked")
	final int compare(K k1, K k2) {
		return comparator == null ? ((Comparable<? super K>)k1).compareTo(k2)
				: comparator.compare(k1, k2);
	}

	/**
	 * @see com.hdaheizi.base.stl.IRank#getKth(int)
	 */
	@Override
	public K getKth(int kth) {
		if (!(kth > 0 && kth <= size())) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(kth));
		}
		return list.get(kth - 1);
	}

	/**
	 * @see com.hdaheizi.base.stl.IRank#size()
	 */
	@Override
	public int size() {
		return list.size();
	}

	/**
	 * @see com.hdaheizi.base.stl.IRank#clear()
	 */
	@Override
	public void clear() {
		list.clear();
	}


	/**
	 * 内部排行榜迭代器
	 * @author daheiz
	 * @Date 2017年3月14日 下午6:58:40
	 */
	protected class RankItr implements RankIterator<K> {

		/** listItr */
		protected ListIterator<K> listItr;

		/**
		 * 构造函数
		 * @param listItr
		 */
		public RankItr(ListIterator<K> listItr) {
			this.listItr = listItr;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return listItr.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public K next() {
			return listItr.next();
		}

		/**
		 * @see com.hdaheizi.base.stl.RankIterator#hasPrevious()
		 */
		@Override
		public boolean hasPrevious() {
			return listItr.hasPrevious();
		}

		/**
		 * @see com.hdaheizi.base.stl.RankIterator#previous()
		 */
		@Override
		public K previous() {
			return listItr.previous();
		}

		/**
		 * @see com.hdaheizi.base.stl.RankIterator#nextRank()
		 */
		@Override
		public int nextRank() {
			return listItr.nextIndex() + 1;
		}

		/**
		 * @see com.hdaheizi.base.stl.RankIterator#previousRank()
		 */
		@Override
		public int previousRank() {
			return listItr.previousIndex() + 1;
		}
	}


	/**
	 * 生成越界信息
	 * @param rank
	 * @return
	 * @Date 2017年3月13日 下午11:01:01
	 */
	private String outOfBoundsMsg(int rank) {
		return "Rank: " + rank + ", Size: " + size();
	}

	/**
	 * @see com.hdaheizi.base.stl.IRank#iterator()
	 */
	@Override
	public Iterator<K> iterator() {
		return list.iterator();
	}

	/**
	 * @see com.hdaheizi.base.stl.Rank#rankIterator()
	 */
	@Override
	public RankIterator<K> rankIterator() {
		return rankIterator(0);
	}

	/**
	 * @see com.hdaheizi.base.stl.Rank#rankIterator(int)
	 */
	@Override
	public RankIterator<K> rankIterator(int rank) {
		return new RankItr(list.listIterator(rank));
	}

}
