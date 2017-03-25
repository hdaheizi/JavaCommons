package com.hdaheizi.base.stl;

import java.util.ArrayList;
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
public class RBTreeChart<K, V> implements IChart<K, V> {

	/** 颜色常量 */
	private static final boolean RED   = false;
	private static final boolean BLACK = true;

	/** 根 */
	private transient Node<K, V> root;

	/** 比较器 */
	private final Comparator<? super V> comparator;

	/** 树结构修改的次数 */
	private transient int modCount = 0;

	/** 关键字key与所属节点映射表 */
	private Map<K, Node<K, V>> nodeMap;


	/**
	 * 节点类
	 * @param <K, V>
	 * @author daheiz
	 * @Date 2017年3月10日 下午11:37:11
	 */
	private static final class Node<K, V> {
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
		/** 存储相等数据的双循环链表的头指针 */
		Entry<K, V> first;
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

		void addEntry(Entry<K, V> e) {
			if (first == null) {
				e.prev = e.next = first = e;
			} else {
				e.next = first;
				e.prev = first.prev;
				first.prev.next = e;
				first.prev = e;
			}
			amount++;
		}

		void removeEntry(Entry<K, V> e) {
			if (e.prev == e) {
				first = null;
			} else {
				if (e == first) {
					first = e.next;
				}
				e.prev.next = e.next;
				e.next.prev = e.prev;
			}
			amount--;
		}

		Entry<K, V> getEntry(K key) {
			if (first != null) {
				Entry<K, V> e = first;
				do {
					if (objEquals(e.key, key)) {
						return e;
					}
					e = e.next;
				} while (e != first);
			}
			return null;
		}

		Tuple<Integer, V> search(K key) {
			int rank = 1;
			if (first != null) {
				Entry<K, V> e = first;
				do {
					if (objEquals(e.key, key)) {
						return new Tuple<>(rank, e.value);
					}
					e = e.next;
					rank++;
				} while (e != first);
			}
			return new Tuple<>(-1, null);
		}
		
		int getRank(K key) {
			int rank = 1;
			if (first != null) {
				Entry<K, V> e = first;
				do {
					if (objEquals(e.key, key)) {
						return rank;
					}
					e = e.next;
					rank++;
				} while (e != first);
			}
			return -1;
		}

		Entry<K, V> getKth(int kth) {
			if (kth <= 0 || kth > amount) {
				return null;
			} else if (kth > amount >>> 1) {
				for (Entry<K, V> e = first.prev; ; e = e.prev) {
					if (kth++ == amount) {
						return e;
					}
				}
			} else {
				for (Entry<K, V> e = first; ; e = e.next) {
					if (--kth == 0) {
						return e;
					}
				}
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
			Node<?, ?> e = (Node<?, ?>) o;
			return objEquals(first, e.first);
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return first == null ? 0 : first.hashCode();
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

		/**
		 * 生成Tuple结构的数据
		 * @return
		 * @Date 2017年3月23日 下午7:17:38
		 */
		Tuple<K, V> tuple() {
			return new Tuple<>(key, value);
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
		int extraNum = sizeOf(p.left);
		while (p != root) {
			if (p == p.parent.right) {
				extraNum += sizeOf(p.parent.left) + p.parent.amount;
			}
			p = p.parent;
		}
		result.left += extraNum;
		return result;
	}
	
	public int getRank(K key) {
		Node<K, V> node = nodeMap.get(key);
		if (node != null) {
			int rank = sizeOf(node.left) + node.getRank(key);
			Node<K, V> p = node;
			while (p != root) {
				if (p == p.parent.right) {
					rank += sizeOf(p.parent.left) + p.parent.amount;
				}
				p = p.parent;
			}
			return rank;
		}
		return -1;
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
	@SuppressWarnings("unused")
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
				preNode.removeEntry(entry);
				if (preNode.first == null) {
					deleteNode(preNode);
					nodeMap.remove(key);
				} else {
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
				cmp = comparator.compare(value, x.first.value);
				if (cmp < 0) {
					x = x.left;
				} else if (cmp > 0) {
					x = x.right;
				} else {
					x.addEntry(newEntry);
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
				cmp = k.compareTo(x.first.value);
				if (cmp < 0) {
					x = x.left;
				} else if (cmp > 0) {
					x = x.right;
				} else {
					x.addEntry(newEntry);
					fixNodeSizeUpward(x);
					nodeMap.put(key, x);
					return preValue;
				}
			}
		}
		Node<K, V> z = new Node<>(y);
		z.addEntry(newEntry);
		if (y == null){
			root = z;
		} else if (compare(z.first.value, y.first.value) < 0) {
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
			preNode.removeEntry(entry);
			if (preNode.first == null) {
				deleteNode(preNode);
			} else {
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
	 * 返回排名信息
	 * @param value
	 * @return {小于value的数目，等于value的数目}
	 * @Date 2017年3月24日 下午7:43:16
	 */
	private int[] getRankInfo(V value) {
		int[] info = new int[]{0, 0};
		int cmp;
		Node<K, V> p = root;
		while (p != null) {
			cmp = compare(value, p.first.value);
			if (cmp == 0) {
				info[0] += sizeOf(p.left);
				info[1] = p.amount;
				break;
			} else if (cmp < 0) {
				p = p.left;
			} else {
				info[0] += sizeOf(p.left) + p.amount;
				p = p.right;
			}
		}
		return info;
	}

	/**
	 * 返回对应名次的值
	 * @param kth
	 * @return
	 * @Date 2017年3月11日 下午3:02:32
	 */
	public Tuple<K, V> getKth(int kth) {
		if (kth > 0 && kth <= size()) {
			return getKthEntry(kth).tuple();
		}
		return null;
	}

	/**
	 * 返回第k个数据
	 * @param rank
	 * @return
	 * @Date 2017年3月23日 下午7:24:35
	 */
	private Entry<K, V> getKthEntry(int kth) {
		Node<K, V> p = root;
		int ls;
		while (p != null) {
			ls = sizeOf(p.left);
			if (kth <= ls) {
				p = p.left;
			} else if (kth > ls + p.amount) {
				p = p.right;
				kth -= ls + p.amount;
			} else {
				kth -= ls;
				return p.getKth(kth);
			}
		}
		return null;
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#get(java.lang.Object)
	 */
	@Override
	public V get(K key) {
		Node<K, V> node = nodeMap.get(key);
		return node == null ? null : node.getEntry(key).value;
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
	private class Itr implements Iterator<Entry<K, V>> {
		/** 下一个node */
		private Node<K, V> nextNode;
		/** 下一个entry */
		private Entry<K, V> nextEntry;
		/** 上一个entry */
		private Entry<K, V> lastEntry;
		/** 期待的被修改次数 */
		private int expectedModCount;

		/**
		 * 构造函数
		 * @param 起始名次
		 */
		Itr(int kth) {
			expectedModCount = modCount;
			nextEntry = kth == size() ? null : getKthEntry(kth + 1);
			nextNode = nextEntry == null ? null : nodeMap.get(nextEntry.key);
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public final boolean hasNext() {
			return nextNode != null;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Entry<K, V> next() {
			checkForComodification();
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			lastEntry = nextEntry;
			if (nextEntry.next == nextNode.first) {
				// 当前Node中最后一个entry
				nextNode = successor(nextNode);
				nextEntry = nextNode == null ? null : nextNode.first;
			} else {
				// 当前Node中的下一个Entry
				nextEntry = nextEntry.next;
			}
			return lastEntry;
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			checkForComodification();
			if (lastEntry == null) {
				throw new IllegalStateException();
			}
			Node<K, V> lastNode = nodeMap.remove(lastEntry.key);
			lastNode.removeEntry(lastEntry);
			if (lastNode.first == null) {
				deleteNode(lastNode);
			} else {
				fixNodeSizeUpward(lastNode);
			}
			lastEntry = null;
			expectedModCount = ++modCount;
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
	 * 返回一个指定起始名次kth的迭代器Iterator<Entry<K, V>>
	 * @param kth [0,size]，调用next()时返回的第一个Entry<K, V>的名次为 kth+1  
	 * @return
	 * @Date 2017年3月11日 下午5:13:25
	 */
	private Iterator<Entry<K, V>> iterator(int kth) {
		return new Itr(kth);
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

	/**
	 * @see com.hdaheizi.base.stl.IChart#getSequenceList(int, int)
	 */
	@Override
	public List<Tuple<K, V>> getSequenceList(int start, int end) {
		List<Tuple<K, V>> list = new ArrayList<>();
		int size = size();
		int kth = start < 0 ? 0 : (start > size ? size : start);
		Iterator<Entry<K, V>> it = iterator(kth);
		while (it.hasNext() && ++kth <= end) {
			list.add(it.next().tuple());
		}
		return list;
	}

	/**
	 * @see com.hdaheizi.base.stl.IChart#getRangeList(java.lang.Object, java.lang.Object)
	 */
	@Override
	public List<Tuple<K, V>> getRangeList(V low, V high) {
		int[] info1 = getRankInfo(low);
		int start = info1[0];
		int[] info2 = getRankInfo(high);
		int end = info2[0] + info2[1];
		return getSequenceList(start, end);
	}


	/****************** 以下为一些非必需的辅助和测试方法 ***************************/

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
		Iterator<Entry<Integer, Tuple<Integer, Integer>>> it = r2.iterator(15);
		while (it.hasNext()) {
			Entry<Integer, Tuple<Integer, Integer>> e = it.next();
			if (e.key >= 15 && e.key < 98) {
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
		r2.check();
		System.out.println(Arrays.toString(r2.getSequenceList(1, 100).toArray()));
		System.out.println(Arrays.toString(r2.getRangeList(new Tuple<>(80, 0), new Tuple<>(100, 0)).toArray()));
		for (int i = 5; i < 15; i++) {
			r2.put(i, new Tuple<>(i, i * 10 - i));
		}
		System.out.println(r2.outputTree());
		System.exit(0);

		
		IChart<Integer, Integer> r = new RBTreeChart<>();
		r.clear();
		int num = 1000000;
		Integer[] a = new Integer[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		List<Integer> li = Arrays.asList(a);
		// *****测试效率
		System.out.println("****test speed, num :" + li.size() + " ,time unit: (ns)");

		long ns1, ns2;
		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 10);
		}
		ns2 = System.nanoTime();
		System.out.println("put: " + (ns2 - ns1) / num);

		// 插入
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (Integer i : li) {
			r.put(i, i / 10 + 1);
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
		// 顺次
		Collections.shuffle(li);
		ns1 = System.nanoTime();
		for (int i = 1; i <= num; i++) {
			r.getSequenceList(i, i);
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

		System.exit(0);
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
		if (p.first != null) {
			Entry<K, V> e = p.first;
			do {
				sb.append(e.toString())
				.append(",");
				e = e.next;
			} while (e != p.first);
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
	 * 验证RBTree结构的正确性
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
			if (p.left != null && compare(p.left.first.value, p.first.value) >= 0 
					|| p.right != null && compare(p.right.first.value, p.first.value) <= 0) {
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
