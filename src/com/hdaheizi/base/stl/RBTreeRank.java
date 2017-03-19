package com.hdaheizi.base.stl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * 基于红黑树的排行榜
 * 所有涉及到比较关键字大小、相等的方法均依赖于其自然排序或给定的比较器的排序
 * @author daheiz
 * @Date 2017年3月3日 下午5:35:04
 */
public class RBTreeRank<K> implements IRank<K> {

	/** 颜色常量 */
	private static final boolean RED   = false;
	private static final boolean BLACK = true;

	/** 根 */
	private transient Node<K> root;

	/** 比较器 */
	private final Comparator<? super K> comparator;

	/** 树结构修改的次数 */
	private transient int modCount = 0;


	/**
	 * 节点类
	 * @param <K>
	 * @author daheiz
	 * @Date 2017年3月10日 下午11:37:11
	 */
	static final class Node<K> {
		/** 关键字 */
		K key;
		/** 父节点 */
		Node<K> parent;
		/** 左子树 */
		Node<K> left;
		/** 右子树 */
		Node<K> right;
		/** 颜色 */
		boolean color;
		/** 以自身为根的子树包含节点数目 */
		int size;

		/**
		 * 构造函数
		 * @param key
		 * @param parent
		 */
		Node(K key, Node<K> parent) {
			this.key = key;
			this.parent = parent;
			this.size = 1;
		}

		/**
		 * 修复节点的size属性
		 * @Date 2017年3月10日 下午11:39:36
		 */
		void maintain(){
			size = 1;
			if (left != null) {
				size += left.size;
			}
			if (right != null) {
				size += right.size;
			}
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Node)) {
				return false;
			}
			Node<?> e = (Node<?>) o;
			return key == null ? e == null : key.equals(e);
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return key == null ? 0 : key.hashCode();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return key == null ? "" : key.toString();
		}
	}


	/**
	 * 构造函数
	 * 使用关键字的自然排序，需要 K implements Comparable<K>
	 */
	public RBTreeRank() {
		this(null);
	}

	/**
	 * 构造函数
	 * 使用给定的比较器
	 * @param comparator
	 */
	public RBTreeRank(Comparator<K> comparator) {
		this.comparator = comparator;
	}

	/**
	 * 比较两个关键字的大小
	 * @param k1
	 * @param k2
	 * @return
	 * @Date 2017年3月10日 下午11:45:22
	 */
	@SuppressWarnings("unchecked")
	final int compare(K k1, K k2) {
		return comparator == null ? ((Comparable<? super K>)k1).compareTo(k2)
				: comparator.compare(k1, k2);
	}

	/**
	 * 查找关键字与key相等的节点
	 * @param key
	 * @return 未找到时返回null
	 * @throws NullPointerException 不接受参数key为null
	 * @Date 2017年3月10日 下午11:47:08
	 */
	private Node<K> search(K key) {
		if (key == null) {
			throw new NullPointerException();
		}
		Node<K> p = root;
		int cmp;
		if(comparator != null) {
			while(p != null) {
				cmp = comparator.compare(key, p.key);
				if(cmp < 0) {
					p = p.left;
				} else if(cmp > 0) {
					p = p.right;
				} else {
					return p;
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			Comparable<? super K> k = (Comparable<? super K>) key;
			while (p != null) {
				cmp = k.compareTo(p.key);
				if (cmp < 0) {
					p = p.left;
				} else if (cmp > 0) {
					p = p.right;
				} else {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * 返回第一个关键字
	 * @return
	 * @Date 2017年3月10日 下午11:56:57
	 */
	public final K getFirst() {
		return keyOf(minimum(root));
	}

	/**
	 * 返回最后一个关键字
	 * @return
	 * @Date 2017年3月11日 上午12:18:15
	 */
	public final K getLast() {
		return keyOf(maximum(root));
	}

	/**
	 * 返回子树中关键字最小的节点
	 * @param p
	 * @return
	 * @Date 2017年3月10日 下午11:57:49
	 */
	private static <K> Node<K> minimum(Node<K> p) {
		if (p == null) {
			return null;
		}
		while (p.left != null) {
			p = p.left;
		}
		return p;
	}

	/**
	 * 返回子树中关键字最大的节点
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午12:20:19
	 */
	private static <K> Node<K> maximum(Node<K> p) {
		if (p == null) {
			return null;
		}
		while (p.right != null) {
			p = p.right;
		}
		return p;
	}

	/**
	 * 返回给定节点的后继节点
	 * @param x
	 * @return
	 * @Date 2017年3月11日 上午12:20:39
	 */
	private static <K> Node<K> predecessor(Node<K> x) {
		if (x == null) {
			return null;
		} else if (x.left != null) {
			return maximum(x.left);
		} else {
			Node<K> y = x.parent;
			while (y != null && x == y.left) {
				x = y;
				y = y.parent;
			}
			return y;
		}
	}

	/**
	 * 返回给定节点的前驱节点
	 * @param x
	 * @return
	 * @Date 2017年3月11日 上午12:21:10
	 */
	private static <K> Node<K> successor(Node<K> x) {
		if (x == null) {
			return null;
		} else if (x.right != null) {
			return minimum(x.right);
		} else {
			Node<K> y = x.parent;
			while (y != null && x == y.right) {
				x = y;
				y = y.parent;
			}
			return y;
		}
	}

	/**
	 * 左旋
	 * @param x
	 * @Date 2017年3月11日 上午12:22:36
	 */
	private void leftRotate(Node<K> x) {
		if (x != null && x.right != null) {
			Node<K> y = x.right;
			x.right = y.left;
			if (y.left != null) {
				y.left.parent = x;
			}
			y.parent = x.parent;
			if (x.parent == null) {
				root = y;
			} else if (x == x.parent.left) {
				x.parent.left = y;
			} else {
				x.parent.right = y;
			}
			y.left = x;
			x.parent = y;
			// 修复x，y
			x.maintain();
			y.maintain();
		}
	}

	/**
	 * 右旋
	 * @param y
	 * @Date 2017年3月11日 上午12:22:44
	 */
	private void rightRotate(Node<K> y) {
		if (y != null && y.left != null) {
			Node<K> x = y.left;
			y.left = x.right;
			if (x.right != null) {
				x.right.parent = y;
			}
			x.parent = y.parent;
			if (y.parent == null) {
				root = x;
			} else if (y == y.parent.left) {
				y.parent.left = x;
			} else {
				y.parent.right = x;
			}
			x.right = y;
			y.parent = x;
			// 修复x，y
			y.maintain();
			x.maintain();
		}
	}

	/**
	 * 添加关键字
	 * 如果已经包含相等的关键字，则添加失败
	 * @param key
	 * @return 是否添加成功
	 * @throws NullPointerException 不接受参数key为null
	 * @Date 2017年3月11日 上午12:22:52
	 */
	public boolean add(K key) {
		if (key == null) {
			throw new NullPointerException();
		}
		Node<K> x = root, y = null;
		int cmp;
		if(comparator != null) {
			while(x != null) {
				y = x;
				cmp = comparator.compare(key, x.key);
				if(cmp < 0) {
					x = x.left;
				} else if(cmp > 0) {
					x = x.right;
				} else {
					return false;
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			Comparable<? super K> k = (Comparable<? super K>) key;
			while (x != null) {
				y = x;
				cmp = k.compareTo(x.key);
				if (cmp < 0) {
					x = x.left;
				} else if (cmp > 0) {
					x = x.right;
				} else {
					return false;
				}
			}
		}
		Node<K> z = new Node<>(key, y);
		if (y == null){
			root = z;
		} else if (compare(z.key, y.key) < 0) {
			y.left = z;
		} else {
			y.right = z;
		}
		z.color = RED;
		// 修复z->root路径上所有节点的size属性
		fixNodeSizeUpward(z);
		// 维护红黑树的性质
		insertFixUp(z);
		modCount++;
		return true;
	}

	/**
	 * 自下而上修复给定节点到根节点路径上所有节点的size属性
	 * @param p
	 * @Date 2017年3月11日 上午12:24:23
	 */
	private void fixNodeSizeUpward(Node<K> p) {
		while (p != null) {
			p.maintain();
			p = p.parent;
		}
	}

	/**
	 * 插入节点后的修复，以维护红黑树的性质
	 * @param z
	 * @Date 2017年3月11日 上午12:26:09
	 */
	private void insertFixUp(Node<K> z) {
		Node<K> y;
		while (z != null && z != root && z.parent.color == RED) {
			if (z.parent == z.parent.parent.left) {
				// z的父节点是左孩子
				y = z.parent.parent.right;	// y可能为null
				if (colorOf(y) == RED) {
					// case 1：z的叔节点是红色
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.right){
						// case 2：z的叔节点是黑色，且z是右孩子
						z = z.parent;
						leftRotate(z);
					}
					// case 3：z的叔节点是黑色，且z是左孩子
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					rightRotate(z.parent.parent);
				}
			} else {
				// z的父节点是右孩子
				y = z.parent.parent.left;	// y可能为null
				if (colorOf(y) == RED) {
					// case 1: z的叔节点是红色
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.left){
						// case 2：z的叔节点是黑色，且z是左孩子
						z = z.parent;
						rightRotate(z);
					}
					// case 3：z的叔节点是黑色，且z是右孩子
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					leftRotate(z.parent.parent);
				}
			}
		}
		// 将根节点染为黑色
		root.color = BLACK;
	}

	/**
	 * 移除与给定关键字相等的关键字
	 * @param key
	 * @return 是否成功移除
	 * @throws NullPointerException 不接受参数key为null
	 * @Date 2017年3月11日 上午12:27:21
	 */
	public boolean remove(K key) {
		Node<K> z = search(key);
		if (z == null) {
			return false;
		}
		deleteNode(z);
		return true;
	}

	/**
	 * 删除给定节点
	 * @param z
	 * @Date 2017年3月11日 上午12:30:09
	 */
	private void deleteNode(Node<K> z) {
		modCount++;
		// y为占据原z位置的节点，x为占据原y位置的节点，xp为x的父节点
		Node<K> y, x, xp;
		boolean color;
		if (z.left == null || z.right == null) {
			y = z;
			x = z.left == null ? z.right : z.left;
			color = y.color;
			transplant(z, x);
			xp = z.parent;
		} else {
			y = minimum(z.right);
			color = colorOf(y);
			x = y.right;
			if (y.parent == z) {
				xp = y;
			} else {
				transplant(y, x);
				xp = y.parent;
				y.right = z.right;
				setParent(y.right, y);
			}
			transplant(z, y);
			y.left = z.left;
			setParent(y.left, y);
			y.color = z.color;
		}
		// 修复xp->root路径上所有节点的size属性
		fixNodeSizeUpward(xp);
		// 维护红黑树的性质
		if (color == BLACK) {
			deteleFixUp(x, xp);
		}
	}

	/**
	 * 用新树替换原树所在位置
	 * @param u 原树
	 * @param v 新树
	 * @Date 2017年3月11日 上午12:31:08
	 */
	private void transplant(Node<K> u, Node<K> v) {
		if (u.parent == null) {
			root = v;
		} else if (u == u.parent.left) {
			u.parent.left = v;
		} else {
			u.parent.right = v;
		}
		setParent(v, u.parent);
	}

	/**
	 * 删除节点后的修复，以维护红黑树的性质
	 * @param x
	 * @param xp
	 * @Date 2017年3月11日 上午12:33:04
	 */
	private void deteleFixUp(Node<K> x, Node<K> xp) {
		Node<K> w;	// x的兄弟节点
		while (x != root && colorOf(x) == BLACK) {
			if (x == xp.left) {
				// x是左孩子
				w = xp.right;	// 可推断w不为null
				if (w.color == RED) {
					// case 1：w是红色
					w.color = BLACK;
					xp.color = RED;
					leftRotate(xp);
					w = xp.right;
				}
				if (colorOf(w.left) == BLACK && colorOf(w.right) == BLACK) {
					// case 2：w是黑色，且其两个孩子均为黑色
					w.color = RED;
					x = xp;
					xp = parentOf(xp);
				} else {
					if (colorOf(w.right) == BLACK) {
						// case 3：w是黑色，且其左孩子为红色，右孩子为黑色
						setColor(w.left, BLACK);
						w.color = RED;
						rightRotate(w);
						w = xp.right;
					}
					// case 4：w是黑色，且其右孩子为红色
					w.color = xp.color;
					xp.color = BLACK;
					setColor(w.right, BLACK);
					leftRotate(xp);
					x = root;
				}
			} else {
				// x是右孩子
				w = xp.left;	// 可推断w不为null
				if (w.color == RED) {
					// case 1：w是红色
					w.color = BLACK;
					xp.color = RED;
					rightRotate(xp);
					w = xp.left;
				}
				if (colorOf(w.left) == BLACK && colorOf(w.right) == BLACK) {
					// case 2：w是黑色，且其两个孩子均为黑色
					w.color = RED;
					x = xp;
					xp = parentOf(xp);
				} else {
					if (colorOf(w.left) == BLACK) {
						// case 3：w是黑色，且其左孩子为黑色，右孩子为红色
						setColor(w.right, BLACK);
						w.color = RED;
						leftRotate(w);
						w = xp.left;
					}
					// case 4：w是黑色，且其左孩子为红色
					w.color = xp.color;
					xp.color = BLACK;
					setColor(w.left, BLACK);
					rightRotate(xp);
					x = root;
				}
			}
		}
		// 将x设置为黑色
		setColor(x, BLACK);
	}

	/**
	 * 返回对应名次的关键字
	 * @param rank
	 * @return
	 * @throws IndexOutOfBoundsException 当名次rank越界时抛出异常
	 * @Date 2017年3月11日 下午3:02:32
	 */
	public K getKth(int rank) {
		if (!(rank > 0 && rank <= size())) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(rank));
		}
		return keyOf(getKthNode(rank));
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
	 * 返回对应名次的节点
	 * @param rank
	 * @return 
	 * @Date 2017年3月11日 上午12:34:18
	 */
	private Node<K> getKthNode(int kth) {
		Node<K> p = root;
		int ls;
		while (p != null) {
			ls = sizeOf(p.left);
			if (kth == ls + 1) {
				break;
			} else if (kth <= ls) {
				p = p.left;
			} else {
				p = p.right;
				kth -= ls + 1;
			}
		}
		return p;
	}

	/**
	 * 返回给定关键字的名次
	 * @param key
	 * @return 如果包含该关键字，则返回一个正数，即当前名次
	 *         如果不包含该关键字，则返回一个负数，其绝对值为插入该关键字后的名次
	 * @Date 2017年3月11日 上午12:37:05
	 */
	public int getRank(K key) {
		int rank = 1, cmp;
		Node<K> p = root;
		while (p != null) {
			cmp = compare(key, p.key);
			if (cmp == 0) {
				return rank + sizeOf(p.left);
			} else if (cmp < 0) {
				p = p.left;
			} else {
				rank += sizeOf(p.left) + 1;
				p = p.right;
			}
		}
		return -rank;
	}

	/**
	 * 是否包含与给定关键字相等的关键字
	 * @param key
	 * @return
	 * @throws NullPointerException 不接受参数key为null
	 * @Date 2017年3月11日 上午12:42:31
	 */
	public boolean contains(K key) {
		return search(key) != null;
	}

	/**
	 * 返回存储关键字的数量
	 * @return
	 * @Date 2017年3月11日 上午12:45:48
	 */
	public int size() {
		return sizeOf(root);
	}

	/**
	 * 判断是否为空
	 * @return
	 * @Date 2017年3月11日 上午12:52:14
	 */
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * 清空
	 * @Date 2017年3月11日 上午12:52:51
	 */
	public void clear() {
		modCount++;
		root = null;
	}

	/**
	 * 内部的排行榜迭代器
	 * @author daheiz
	 * @Date 2017年3月14日 上午12:56:17
	 */
	private class RankItr implements RankIterator<K> {
		/** 前一个节点 */
		private Node<K> last;
		/** 后一个节点 */
		private Node<K> next;
		/** 前一个元素的名次 */
		private int lastRank;
		/** 期待的被修改次数 */
		private int expectedModCount;

		/**
		 * 构造函数
		 * @param 起始名次
		 */
		RankItr(int rank) {
			expectedModCount = modCount;
			next = rank == size() ? null : getKthNode(rank + 1);
			lastRank = rank;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public final boolean hasNext() {
			return lastRank < size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public K next() {
			checkForComodification();
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			last = next;
			next = successor(next);
			lastRank++;
			return last.key;
		}

		/**
		 * 是否存在前一个关键字
		 * @return
		 * @Date 2017年3月13日 下午9:12:19
		 */
		public boolean hasPrevious() {
			return lastRank > 0;
		}

		/**
		 * 移动到前一个关键字
		 * @return
		 * @Date 2017年3月13日 下午9:12:31
		 */
		public K previous() {
			checkForComodification();
			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}
			last = next = (next == null ? maximum(root) : predecessor(next));
			lastRank--;
			return last.key;
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			checkForComodification();
			if (last == null) {
				throw new IllegalStateException();
			}
			Node<K> lastNext = successor(last);
			deleteNode(last);
			if (next == last) {
				next = lastNext;
			} else {
				lastRank--;
			}
			last = null;
			expectedModCount = modCount;
		}

		/**
		 * 返回后一个关键字的名次
		 * @return
		 * @Date 2017年3月13日 下午10:13:53
		 */
		public int nextRank() {
			return lastRank + 1;
		}

		/**
		 * 返回前一个关键字的名次
		 * @return
		 * @Date 2017年3月13日 下午10:13:53
		 */
		public int previousRank() {
			return lastRank;
		}

		/**
		 * 检查树结构是否被修改
		 * @Date 2017年3月14日 上午12:59:20
		 */
		private final void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * 返回一个迭代器
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	public Iterator<K> iterator() {
		return rankIterator();
	}

	/**
	 * 返回一个RankIterator
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	public RankIterator<K> rankIterator() {
		return rankIterator(0);
	}

	/**
	 * 返回一个指定起始名次的RankIterator
	 * @param rank [0,size]，调用previous()时返回的第一个关键字的名次为 rank
	 *                       调用next()时返回的第一个关键字的名次为 rank+1
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	public RankIterator<K> rankIterator(int rank) {
		if (!(rank >= 0 && rank <= size())) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(rank));
		}
		return new RankItr(rank);
	}

	/**
	 * 返回节点的颜色
	 * 空节点为黑色
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午1:00:01
	 */
	private static <K> boolean colorOf(Node<K> p) {
		return p == null ? BLACK : p.color;
	}

	/**
	 * 返回给定节点的关键字
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午3:29:25
	 */
	private static <K> K keyOf(Node<K> p) {
		return p == null ? null : p.key;
	}

	/**
	 * 返货给定节点的size值
	 * 如果节点为null则返回0
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午1:03:00
	 */
	private static <K> int sizeOf(Node<K> p) {
		return p == null ? 0 : p.size;
	}

	/**
	 * 返回给定节点的父节点
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午1:00:55
	 */
	private static <K> Node<K> parentOf(Node<K> p) {
		return p == null ? null: p.parent;
	}

	/**
	 * 为给定节点设定父节点
	 * @param p 给定节点
	 * @param pp 父节点
	 * @Date 2017年3月11日 上午1:01:33
	 */
	private static <K> void setParent(Node<K> p, Node<K> pp) {
		if (p != null) {
			p.parent = pp;
		}
	}

	/**
	 * 为给定节点设定颜色
	 * @param p
	 * @param c
	 * @Date 2017年3月11日 上午1:02:47
	 */
	private static <K> void setColor(Node<K> p, boolean c) {
		if (p != null)
			p.color = c;
	}





	/****************** 以下为一些不发布的辅助和测试方法 ***************************/

	/**
	 * 单元测试
	 * @param args
	 * @Date 2017年3月11日 下午5:07:33
	 */
	public static void main(String[] args) {
		RBTreeRank<Integer> r = new RBTreeRank<>();
		int num = 10000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		List<Integer> li2 = li.subList(1, 31);

		// *****测试正确性
		System.out.println("****test correctness, num :" + li2.size());
		Collections.shuffle(li2);
		for (Integer i : li2) {
			r.add(i);
			// 检查结构是否被破坏
			r.check();
		}
		System.out.println(r.outputTree());
		System.out.println(Arrays.toString(r.toArray()));

		RankIterator<Integer> it = r.rankIterator();
		while (it.hasNext()) {
			Integer x = it.next();
			if (x == 24) {
				it.remove();
				it.previous();
				it.remove();
			}
		}
		System.out.println("the 23th is : " + r.getKth(23));
		r.add(8);
		r.add(9);
		System.out.println(r.outputTree());
		System.out.println(Arrays.toString(r.toArray()));


		System.out.println("23 is at the rank: " + r.getRank(23));
		System.out.println("25 is at the rank: " + r.getRank(25));
		System.out.println("0 is at the rank: " + r.getRank(0));
		System.out.println("100 is at the rank: " + r.getRank(100));

		Collections.shuffle(li2);
		for (Integer i : li2) {
			r.remove(i);
			// 检查结构是否被破坏
			r.check();
		}

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
		System.out.println("height: " + r.getHeight());
		System.out.println("black height: " + r.getBlackHeight());
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

	/**
	 * 生成红黑树的内部结构字符串
	 * @return
	 * @Date 2017年3月11日 下午5:10:34
	 */
	private final String outputTree() {
		StringBuilder sb = new StringBuilder();
		if (root != null) {
			outputTreeImpl(sb, root.right, false, "");
			sb.append(root.toString());
			sb.append("\n");
			outputTreeImpl(sb, root.left, true, "");
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 构建红黑树内部结构字符串
	 * @param sb
	 * @param p
	 * @param left
	 * @param indent
	 * @Date 2017年3月11日 下午5:11:04
	 */
	private final void outputTreeImpl(StringBuilder sb, Node<K> p, boolean left, String indent) {
		if (p != null) {
			outputTreeImpl(sb, p.right, false, indent + (left ? "|" : " ") + "    ");
			sb.append(indent)
			.append(left ? "\\" : "/")
			.append("----")
			.append(p.toString());
			sb.append("\n");
			outputTreeImpl(sb, p.left, true, indent + (left ? " " : "|") + "    ");
		}
	}

	/**
	 * 验证RBTreeRank的正确性
	 * @Date 2017年3月11日 下午5:11:39
	 */
	private void check() {
		if (!isSortTree(root)) {
			// 不是排序树
			throw new RuntimeException("Not BinaryTree !\n" + outputTree());
		}
		if (!isRankTree(root)) {
			// 不是名次树
			throw new RuntimeException("Not RBTree !\n" + outputTree());
		}
		if (colorOf(root) == RED || !isRBTree(root)) {
			// 不是红黑树
			throw new RuntimeException("Not RBTree !\n" + outputTree());
		}
	}

	/**
	 * 判断是否为排序树
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:12:01
	 */
	private boolean isSortTree(Node<K> p) {
		if (p != null) {
			if (!isSortTree(p.left) || !isSortTree(p.right)) {
				return false;
			}
			if (p.left != null && compare(p.left.key, p.key) >= 0 
					|| p.right != null && compare(p.right.key, p.key) <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为名次树
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:12:15
	 */
	private boolean isRankTree(Node<K> p) {
		if (p != null) {
			if (!isRankTree(p.left) || !isRankTree(p.right)) {
				return false;
			}
			if (sizeOf(p) != sizeOf(p.left) + sizeOf(p.right) + 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为红黑树
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:12:34
	 */
	private boolean isRBTree(Node<K> p){
		if (p != null) {
			if (p.color == RED && colorOf(p.parent) == RED) {
				return false;
			}
			if (!isRBTree(p.left) || !isRBTree(p.right)) {
				return false;
			}
			if (getBlackHeight(p.left) != getBlackHeight(p.right)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 返回树的高度
	 * @return
	 * @Date 2017年3月11日 下午5:12:45
	 */
	private int getHeight() {
		return getHeight(root);
	}

	/**
	 * 返回子树的高度
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:13:03
	 */
	private int getHeight(Node<K> p) {
		if(p == null) {
			return 0;
		}
		return 1 + Math.max(getHeight(p.left), getHeight(p.right));
	}

	/**
	 * 返回树的黑高
	 * @return
	 * @Date 2017年3月11日 下午5:13:11
	 */
	private int getBlackHeight() {
		return getBlackHeight(root);
	}

	/**
	 * 返回子树的黑高
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	private int getBlackHeight(Node<K> p) {
		int bh = 0;
		while (p != null) {
			if (p.color == BLACK) {
				bh++;
			}
			p = p.right;
		}
		return bh;
	}
}
