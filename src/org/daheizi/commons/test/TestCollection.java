package org.daheizi.commons.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.daheizi.commons.stl.ArrayListRank;
import org.daheizi.commons.stl.IRank;
import org.daheizi.commons.stl.RBTreeRank;
import org.daheizi.commons.test.unit.IAny;

public class TestCollection {
    
    public static void main(String[] args) {
        
//        List<Integer> list = new LinkedList<>();
//        Queue<Integer> statck = new LinkedList<>();
//        list.addAll(Arrays.asList(10, 20, 30, 40, 50));
//        List<Integer> list2 = new ArrayList<>(Arrays.asList(1, 3, 5, 7, 9, 11));
//        System.out.println(list.remove(0));
//        System.out.println(list.remove(new Integer(20)));
        
//        Comparator<Integer> cmp = Collections.reverseOrder();
//        Collections.sort(list, cmp);
        
//        Collections.rotate(list, 3);
        
//        Collections.copy(list2, list);
        
//        System.out.println(list);
//        System.out.println(list2);

//        list.forEach(x -> System.out.println(x));
        
//        for(Object obj : new Iterable<Object>(){
//
//            @Override
//            public Iterator<Object> iterator() {
//                return new Iterator<Object>() {
//
//                    @Override
//                    public boolean hasNext() {
//                        // TODO Auto-generated method stub
//                        return false;
//                    }
//
//                    @Override
//                    public Object next() {
//                        // TODO Auto-generated method stub
//                        return null;
//                    }
//                };
//            }
//            
//        }){
//            
//        }
        
//        System.out.println((1 << 7) - 1);
//        System.out.println(~(1 << 7) + 1);
//        int x = 7, y = 19;
//        y ^= x;
//        x ^= y;
//        y ^= x;
//        System.out.println(x);
//        System.out.println(y);
        
//        Queue<Integer> queue = new PriorityQueue<>();
//        queue.offer(3);
//        queue.offer(5);
//        queue.offer(1);
//        
//        queue.forEach(x -> System.out.println(x));
//        Map<Integer, Integer> map = new HashMap<>();
//        Map<Integer, Integer> map2 = new TreeMap<>();
        int[] a = new int[]{1, 2, 3, 4, 5, 6};
        System.arraycopy(a, 1, a, 0, 5);
        System.out.println(Arrays.toString(a));
        
        
        IRank<Integer> rank = new RBTreeRank<>();
        rank.add(3);
        rank.add(2);
        rank.add(4);
        Iterator<Integer> it = rank.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        System.exit(0);
    }

}
