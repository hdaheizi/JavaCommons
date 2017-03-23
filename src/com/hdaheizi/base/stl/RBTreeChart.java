package com.hdaheizi.base.stl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 基于红黑树的排行榜
 * 所有涉及到比较值大小、相等的方法均依赖于其自然排序或给定的比较器的排序
 * @author daheiz
 * @Date 2017年3月3日 下午5:35:04
 */
public class RBTreeChart<K, V> {

	/** 颜色常量 */
	private static final boolean RED   = false;
	private static final boolean BLACK = true;

	/** 根 */
	private transient Node<K, V> root;

	/** 比较器 */
	private final Comparator<? super V> comparator;

	/** 树结构修改的次数 */
	private transient int modCount = 0;
	
	/** nodeMap */
	private Map<K, Node<K, V>> nodeMap;


	/**
	 * 节点类
	 * @param <K, V>
	 * @author daheiz
	 * @Date 2017年3月10日 下午11:37:11
	 */
	static final class Node<K, V> {
		/** 存储数据的实体 */
		Entry<K, V> entry;
		/** 父节点 */
		Node<K, V> parent;
		/** 左子树 */
		Node<K, V> left;
		/** 右子树 */
		Node<K, V> right;
		/** 颜色 */
		boolean color;
		/** 以自身为根的子树包含节点数目 */
		int size;
		/** 内部包含相等数据的数量 */
		int amount;

		/**
		 * 构造函数
		 * @param value
		 * @param parent
		 */
		Node(Node<K, V> parent) {
			this.parent = parent;
			this.size = 1;
			this.amount = 0;
		}

		/**
		 * 修复节点的size属性
		 * @Date 2017年3月10日 下午11:39:36
		 */
		void maintain(){
			size = amount;
			if (left != null) {
				size += left.size;
			}
			if (right != null) {
				size += right.size;
			}
		}
		
		Entry<K, V> getKth(int rank) {
			if (rank <= 0 || rank > amount) {
				return null;
			} else if (rank > amount >>> 1) {
				for (Entry<K, V> e = entry.prev; ; e = e.prev) {
					if (rank++ == amount) {
						return e;
					}
				}
			} else {
				for (Entry<K, V> e = entry; ; e = e.next) {
					if (--rank == 0) {
						return e;
					}
				}
			}
		}
		
		Entry<K, V> getEntry(K key) {
			if (entry != null) {
				Entry<K, V> e = entry;
				do {
					if (objEquals(e.key, key)) {
						return e;
					}
					e = e.next;
				} while (e != entry);
			}
			return null;
		}
		
		Tuple<Integer, V> search(K key) {
			int rank = 1;
			if (entry != null) {
				Entry<K, V> e = entry;
				do {
					if (objEquals(e.key, key)) {
						return new Tuple<>(rank, e.value);
					}
					e = e.next;
					rank++;
				} while (e != entry);
			}
			return new Tuple<>(-1, null);
		}
		
		V put(K key, V value) {
			V pre = null;
			if (entry == null) {
				entry = new Entry<K, V>(key, value);
				amount++;
			} else if (objEquals(entry.key, key)) {
				pre = entry.value;
				entry.value = value;
			} else {
				Entry<K, V> e = entry;
				for (; e.next != null; e = e.next) {
					if (objEquals(e.next.key, key)) {
						pre = e.next.value;
						e.next.value = value;
						return pre;
					}
				}
				e.next = new Entry<K, V>(key, value);
				amount++;
			}
			return pre;
		}
		
		V remove(K key) {
			V pre = null;
			if (entry != null) {
				if (objEquals(entry.key, key)) {
					pre = entry.value;
					entry = entry.next;
					amount--;
				} else {
					for (Entry<K, V> e = entry; e.next != null; e = e.next) {
						if (objEquals(e.next.key, key)) {
							pre = e.next.value;
							e.next = e.next.next;
							amount--;
							break;
						}
					}
				}
			}
			return pre;
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Node)) {
				return false;
			}
			Node<?, ?> e = (Node<?, ?>) o;
			return objEquals(entry, e.entry);
		}

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return entry == null ? 0 : entry.hashCode();
        }
	}
	
	/**
	 * 存储数据的实体类
	 * @param <K>
	 * @param <V>
	 * @author daheiz
	 * @Date 2017年3月20日 下午7:50:22
	 */
	static final class Entry<K, V> {
		/** 键 */
		K key;
		/** 值 */
		V value;
		/** 位于同一个Node的前一个entry，第一个entry的prev指向最后一个 */
		Entry<K, V> prev;
		/** 位于同一个Node的后一个entry，最后一个entry的next指向第一个*/
		Entry<K, V> next;
		
		/**
		 * 构造函数
		 * @param key
		 * @param value
		 */
		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * 获取键
		 * @return
		 * @Date 2017年3月19日 下午6:46:57
		 */
		public K getKey() {
			return key;
		}
		
		/**
		 * 获取值
		 * @return
		 * @Date 2017年3月19日 下午6:46:59
		 */
		public V getValue() {
			return value;
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> e = (Entry<?, ?>) o;
			return objEquals(key, e.key) && objEquals(value, e.value);
		}
		
        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            int keyHash = (key == null ? 0 : key.hashCode());
            int valueHash = (value == null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return key + "=" + value;
        }
	}

	/**
	 * 构造函数
	 * 使用值的自然排序，需要 V implements Comparable<K, V>
	 */
	public RBTreeChart() {
		this(null);
	}

	/**
	 * 构造函数
	 * 使用给定的比较器
	 * @param comparator
	 */
	public RBTreeChart(Comparator<V> comparator) {
		this.comparator = comparator;
		this.nodeMap = new HashMap<>();
	}

	/**
	 * 比较两个节点值的大小
	 * @param v1
	 * @param v2
	 * @return
	 * @Date 2017年3月10日 下午11:45:22
	 */
	@SuppressWarnings("unchecked")
	final int compare(V v1, V v2) {
		return comparator == null ? ((Comparable<? super V>)v1).compareTo(v2)
				: comparator.compare(v1, v2);
	}
	
	/**
	 * 比较两个对象是否相等
	 * @param o1
	 * @param o2
	 * @return
	 * @Date 2017年3月19日 下午6:37:59
	 */
	private static final boolean objEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	/**
	 * 查找值与value相等的节点
	 * @param value
	 * @return 未找到时返回null
	 * @throws NullPointerException 不接受参数value为null
	 * @Date 2017年3月10日 下午11:47:08
	 */
	public Tuple<Integer, V> search(K key) {
		Node<K, V> node = nodeMap.get(key);
		if (node == null) {
			return new Tuple<>(-1, null);
		}
		Node<K, V> p = node;
		Tuple<Integer, V> result = node.search(key);
		if (result.right != null) {
			int extraNum = sizeOf(p.left);
			while (p != root) {
				if (p == p.parent.right) {
					extraNum += sizeOf(p.parent.left) + p.parent.amount;
				}
				p = p.parent;
			}
			result.left += extraNum;
		}
		return result;
	}

	/**
	 * 返回子树中值最小的节点
	 * @param p
	 * @return
	 * @Date 2017年3月10日 下午11:57:49
	 */
	private static <K, V> Node<K, V> minimum(Node<K, V> p) {
		if (p == null) {
			return null;
		}
		while (p.left != null) {
			p = p.left;
		}
		return p;
	}

	/**
	 * 返回子树中值最大的节点
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午12:20:19
	 */
	private static <K, V> Node<K, V> maximum(Node<K, V> p) {
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
	private static <K, V> Node<K, V> predecessor(Node<K, V> x) {
		if (x == null) {
			return null;
		} else if (x.left != null) {
			return maximum(x.left);
		} else {
			Node<K, V> y = x.parent;
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
	private static <K, V> Node<K, V> successor(Node<K, V> x) {
		if (x == null) {
			return null;
		} else if (x.right != null) {
			return minimum(x.right);
		} else {
			Node<K, V> y = x.parent;
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
	private void leftRotate(Node<K, V> x) {
		if (x != null && x.right != null) {
			Node<K, V> y = x.right;
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
	private void rightRotate(Node<K, V> y) {
		if (y != null && y.left != null) {
			Node<K, V> x = y.left;
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
	 * 添加值
	 * 如果已经包含相等的值，则添加失败
	 * @param value
	 * @return 是否添加成功
	 * @throws NullPointerException 不接受参数value为null
	 * @Date 2017年3月11日 上午12:22:52
	 */
	public V put(K key, V value) {
		// 结构修改次数 +1
		modCount++;
		V preValue = null;
		Node<K, V> preNode = nodeMap.get(key);
		if (preNode != null) {
			Entry<K, V> entry = preNode.getEntry(key);
			preValue = entry.value;
			if (compare(value, entry.value) == 0) {
				entry.value = value;
				modCount--;
				return preValue;
			} else {
				if (entry.prev == entry) {
					preNode.entry = null;
					preNode.amount--;
					deleteNode(preNode);
					nodeMap.remove(key);
				} else {
					entry.prev.next = entry.next;
					entry.next.prev = entry.prev;
					if (preNode.entry == entry) {
						preNode.entry = entry.next;
					}
					preNode.amount--;
					fixNodeSizeUpward(preNode);
				}
			}
		}
		int cmp;
		Node<K, V> x = root, y = null;
		Entry<K, V> newEntry = new Entry<>(key, value);
		if(comparator != null) {
			while(x != null) {
				y = x;
				cmp = comparator.compare(value, x.entry.value);
				if (cmp < 0) {
					x = x.left;
				} else if (cmp > 0) {
					x = x.right;
				} else {
					newEntry.next = x.entry;
					newEntry.prev = x.entry.prev;
					x.entry.prev.next = newEntry;
					x.entry.prev = newEntry;
					x.amount++;
					fixNodeSizeUpward(x);
					nodeMap.put(key, x);
					return preValue;
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			Comparable<? super V> k = (Comparable<? super V>) value;
			while (x != null) {
				y = x;
				cmp = k.compareTo(x.entry.value);
				if (cmp < 0) {
					x = x.left;
				} else if (cmp > 0) {
					x = x.right;
				} else {
					newEntry.next = x.entry;
					newEntry.prev = x.entry.prev;
					x.entry.prev.next = newEntry;
					x.entry.prev = newEntry;
					x.amount++;
					fixNodeSizeUpward(x);
					nodeMap.put(key, x);
					return preValue;
				}
			}
		}
		Node<K, V> z = new Node<>(y);
		newEntry.next = newEntry;
		newEntry.prev = newEntry;
		z.entry = newEntry;
		z.amount++;
		if (y == null){
			root = z;
		} else if (compare(z.entry.value, y.entry.value) < 0) {
			y.left = z;
		} else {
			y.right = z;
		}
		z.color = RED;
		// 修复z->root路径上所有节点的size属性
		fixNodeSizeUpward(z);
		// 维护红黑树的性质
		insertFixUp(z);
		// 将新的节点存入nodeMap
		nodeMap.put(key, z);
		return preValue;
	}

	/**
	 * 自下而上修复给定节点到根节点路径上所有节点的size属性
	 * @param p
	 * @Date 2017年3月11日 上午12:24:23
	 */
	private void fixNodeSizeUpward(Node<K, V> p) {
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
	private void insertFixUp(Node<K, V> z) {
		Node<K, V> y;
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
	 * 移除与给定值相等的值
	 * @param value
	 * @return 是否成功移除
	 * @throws NullPointerException 不接受参数value为null
	 * @Date 2017年3月11日 上午12:27:21
	 */
	public V remove(K key) {
		V preValue = null;
		Node<K, V> preNode = nodeMap.remove(key);
		if (preNode != null) {
			modCount++;
			Entry<K, V> entry = preNode.getEntry(key);
			preValue = entry.value;
			if (entry.prev == entry) {
//				preNode.entry = null;
//				preNode.amount--;
				deleteNode(preNode);
			} else {
				entry.prev.next = entry.next;
				entry.next.prev = entry.prev;
				if (preNode.entry == entry) {
					preNode.entry = entry.next;
				}
				preNode.amount--;
				fixNodeSizeUpward(preNode);
			}
		}
		return preValue;
	}

	/**
	 * 删除给定节点
	 * @param z
	 * @Date 2017年3月11日 上午12:30:09
	 */
	private void deleteNode(Node<K, V> z) {
		// y为占据原z位置的节点，x为占据原y位置的节点，xp为x的父节点
		Node<K, V> y, x, xp;
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
	private void transplant(Node<K, V> u, Node<K, V> v) {
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
	private void deteleFixUp(Node<K, V> x, Node<K, V> xp) {
		Node<K, V> w;	// x的兄弟节点
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
	 * 返回对应名次的值
	 * @param rank
	 * @return
	 * @throws IndexOutOfBoundsException 当名次rank越界时抛出异常
	 * @Date 2017年3月11日 下午3:02:32
	 */
	public Entry<K, V> getKth(int rank) {
		if (!(rank > 0 && rank <= size())) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(rank));
		}
		return getKthNodeEntry(rank).right;
	}
	
	private Tuple<Node<K, V>, Entry<K, V>> getKthNodeEntry(int rank) {
		Node<K, V> p = root;
		int ls;
		while (p != null) {
			ls = sizeOf(p.left);
			if (rank <= ls) {
				p = p.left;
			} else if (rank > ls + p.amount) {
				p = p.right;
				rank -= ls + p.amount;
			} else {
				break;
			}
		}
		return new Tuple<>(p, p == null ? null : p.getKth(rank));
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
	 * 是否包含与给定值相等的值
	 * @param value
	 * @return
	 * @throws NullPointerException 不接受参数value为null
	 * @Date 2017年3月11日 上午12:42:31
	 */
	public boolean containsKey(K key) {
		return nodeMap.containsKey(key);
	}

	/**
	 * 返回存储值的数量
	 * @return
	 * @Date 2017年3月11日 上午12:45:48
	 */
	public int size() {
		return nodeMap.size();
	}

	/**
	 * 判断是否为空
	 * @return
	 * @Date 2017年3月11日 上午12:52:14
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * 清空
	 * @Date 2017年3月11日 上午12:52:51
	 */
	public void clear() {
		modCount++;
		root = null;
		nodeMap.clear();
	}

	/**
	 * 内部的排行榜迭代器
	 * @author daheiz
	 * @Date 2017年3月14日 上午12:56:17
	 */
	private class RankItr implements RankIterator<Node<K, V>> {
		/** 前一个节点 */
		private Node<K, V> last;
		/** 后一个节点 */
		private Node<K, V> next;
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
			next = rank == size() ? null : getKthNodeEntry(rank + 1).left;
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
		public Node<K, V> next() {
			checkForComodification();
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			last = next;
			next = successor(next);
			lastRank++;
			return last;
		}

		/**
		 * 是否存在前一个值
		 * @return
		 * @Date 2017年3月13日 下午9:12:19
		 */
		public boolean hasPrevious() {
			return lastRank > 0;
		}

		/**
		 * 移动到前一个值
		 * @return
		 * @Date 2017年3月13日 下午9:12:31
		 */
		public Node<K, V> previous() {
			checkForComodification();
			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}
			last = next = (next == null ? maximum(root) : predecessor(next));
			lastRank--;
			return last;
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
			Node<K, V> lastNext = successor(last);
			deleteNode(last);
			if (next == last) {
				next = lastNext;
			} else {
				lastRank--;
			}
			last = null;
			expectedModCount = ++modCount;
		}

		/**
		 * 返回后一个值的名次
		 * @return
		 * @Date 2017年3月13日 下午10:13:53
		 */
		public int nextRank() {
			return lastRank + 1;
		}

		/**
		 * 返回前一个值的名次
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
	public Iterator<Node<K, V>> iterator() {
		return rankIterator();
	}

	/**
	 * 返回一个RankIterator
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	public RankIterator<Node<K, V>> rankIterator() {
		return rankIterator(0);
	}

	/**
	 * 返回一个指定起始名次的RankIterator
	 * @param rank [0,size]，调用previous()时返回的第一个值的名次为 rank
	 *                       调用next()时返回的第一个值的名次为 rank+1
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	public RankIterator<Node<K, V>> rankIterator(int rank) {
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
	private static <K, V> boolean colorOf(Node<K, V> p) {
		return p == null ? BLACK : p.color;
	}

	/**
	 * 返货给定节点的size值
	 * 如果节点为null则返回0
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午1:03:00
	 */
	private static <K, V> int sizeOf(Node<K, V> p) {
		return p == null ? 0 : p.size;
	}

	/**
	 * 返回给定节点的父节点
	 * @param p
	 * @return
	 * @Date 2017年3月11日 上午1:00:55
	 */
	private static <K, V> Node<K, V> parentOf(Node<K, V> p) {
		return p == null ? null: p.parent;
	}

	/**
	 * 为给定节点设定父节点
	 * @param p 给定节点
	 * @param pp 父节点
	 * @Date 2017年3月11日 上午1:01:33
	 */
	private static <K, V> void setParent(Node<K, V> p, Node<K, V> pp) {
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
	private static <K, V> void setColor(Node<K, V> p, boolean c) {
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
		RBTreeChart<Integer, Tuple<Integer, Integer>> r2 = new RBTreeChart<>(
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
		System.out.println(r2.put(2, new Tuple<>(10, 90)));
		System.out.println(r2.put(3, new Tuple<>(300, 0)));
		System.out.println(r2.put(50, new Tuple<>(50, 51)));
		System.out.println(r2.put(3, new Tuple<>(10, 90)));
//		System.out.println(Arrays.toString(r2.getSequenceList(1, 100).toArray()));
		
		System.exit(0);
		
		RBTreeChart<Integer, Integer> r = new RBTreeChart<>();
		for (int i = 1; i < 100; i++) {
			r.put(i, i / 3);
		}
		r.remove(58);
		r.remove(59);
		r.check();
		System.out.println(r.getKth(90));
		System.out.println(r.outputTree());
		
		
		// *****测试效率
		r.clear();
		int num = 100000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");
		
		long ns1, ns2;
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 100);
		}
		ns2 = System.nanoTime();
		System.out.println("put: " + (ns2 - ns1) / num);
		
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 100 + 1);
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
//		// 顺次
//		Collections.shuffle(li);
//		ns1 = System.nanoTime();
//		for (int i = 1; i <= num; i++) {
//			r.getSequenceList(i, i);
//		}
//		ns2 = System.nanoTime();
//		System.out.println("kth:" + (ns2 - ns1) / num);
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
			sb.append(output(root));
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
	private final void outputTreeImpl(StringBuilder sb, Node<K, V> p, boolean left, String indent) {
		if (p != null) {
			outputTreeImpl(sb, p.right, false, indent + (left ? "|" : " ") + "    ");
			sb.append(indent)
			.append(left ? "\\" : "/")
			.append("----")
			.append(output(p));
			sb.append("\n");
			outputTreeImpl(sb, p.left, true, indent + (left ? " " : "|") + "    ");
		}
	}
	
    private String output(Node<K, V> p) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("[");
		if (p.entry != null) {
			Entry<K, V> e = p.entry;
			do {
	    		sb.append(e.toString())
	    		  .append(",");
	    		e = e.next;
			} while (e != p.entry);
		}
    	int lastCommaIndex = sb.lastIndexOf(",");
    	if (lastCommaIndex == -1) {
    		sb.append("]");
    	} else {
    		sb.setCharAt(lastCommaIndex, ']');
    	}
        return sb.toString();
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
		if (!isRankTree(root) || nodeMap.size() != sizeOf(root)) {
			// 不是名次树
			throw new RuntimeException("Not RankTree !\n" + outputTree());
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
	private boolean isSortTree(Node<K, V> p) {
		if (p != null) {
			if (!isSortTree(p.left) || !isSortTree(p.right)) {
				return false;
			}
			if (p.left != null && compare(p.left.entry.value, p.entry.value) >= 0 
					|| p.right != null && compare(p.right.entry.value, p.entry.value) <= 0) {
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
	private boolean isRankTree(Node<K, V> p) {
		if (p != null) {
			if (!isRankTree(p.left) || !isRankTree(p.right)) {
				return false;
			}
			if (p.size != sizeOf(p.left) + sizeOf(p.right) + p.amount) {
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
	private boolean isRBTree(Node<K, V> p){
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
	 * 返回子树的黑高
	 * @param p
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	private int getBlackHeight(Node<K, V> p) {
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
