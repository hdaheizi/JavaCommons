package net.daheizi.commons.stl;

import java.io.Serializable;

/**
 * 二元组
 * @param <L>
 * @param <R>
 * @author daheizi
 * @Date 2016年3月15日 下午10:28:52
 */
public class Tuple<L, R> implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** left */
    public L left;

    /** right */
    public R right;

    /**
     * 构造函数
     */
    public Tuple() {}

    /**
     * 构造函数
     * @param left
     * @param right
     */
    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int leftCode = left == null ? 0 : left.hashCode();
        int rigthCode = right == null ? 0 : right.hashCode();
        return leftCode ^ rigthCode;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Tuple){
            Tuple<?, ?> other = (Tuple<?, ?>) obj;
            return objEquals(other.left, left) && objEquals(other.right, right);
        }
        return false;
    }

    /**
     * 比较两个对象是否相等
     * @param o1
     * @param o2
     * @return
     * @Date 2017年3月12日 下午9:58:11
     */
    private static final boolean objEquals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[left=" + left + ", right=" + right + "]";
    }
}
