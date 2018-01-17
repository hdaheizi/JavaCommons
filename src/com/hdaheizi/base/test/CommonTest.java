package com.hdaheizi.base.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonTest {
	
	public static int times = 5000;

	public static void main(String[] args) {
		List<Integer> queue = new LinkedList<Integer>();
		queue.addAll(Arrays.asList(1,2,3,4,5));
		List<String> list = new CopyOnWriteArrayList<>();
		list.addAll(Arrays.asList("a","b","c","d","e"));
		Thread t1 = new Thread(new Runnable() {
			
			int i = 0;
			
			@Override
			public void run() {
				while(i < 1){
					i++;
					System.out.println("t1 start at :" + i);
//					for(int j = 0, size = queue.size(); j < queue.size(); 
//							j += queue.size() - size + 1, size = queue.size()){
//						System.out.println(queue.get(j));
//						queue.remove(Integer.valueOf(5));
//						try {
//							Thread.sleep(1);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//					}
//					Iterator<Integer> it = queue.iterator();
//					while(it.hasNext()){
//						Integer cur = it.next();
//						if(cur == 2){
//							queue.add(88);
//							it.remove();
//						}else{
//							System.out.println(cur);
//						}
//					}
					for(String str : list){
						list.remove("c");
						list.add("p");
						System.out.println(str);
						
					}
					System.out.println(list.size());
					System.out.println("t1 end at :" + i);
					System.exit(0);
				}
			}
		});
		

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true){
					System.out.println("t2 start:----------------" );
					queue.add(9);
					queue.add(10);
					try {
						Thread.sleep(2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("t2 end:-----------------" );
				}
			}
		});

//		t1.start();
//		t2.start();

//		List<Integer> list1 = new ArrayList<>();
//		List<Integer> list2 = new LinkedList<>();
//		Queue<Integer> list3 = new ConcurrentLinkedQueue<>();
//		List<Integer> list4 = new CopyOnWriteArrayList<>();
//		Map<Integer, Integer> list5 = new HashMap<>();
//		Map<Integer, Integer> list6 = new ConcurrentHashMap<>();
//		testCollection(list1);
//		testCollection(list2);
//		testCollection(list3);
//		testCollection(list4);
//		testCollection(list5);
//		testCollection(list6);
		
//		System.exit(0);
		
		new M().method();
	}
	
	
	public static void testCollection(Collection<Integer> list){
		System.out.println(list.getClass().getTypeName());
		long curr = System.currentTimeMillis();
		for(int i = 0; i < times; i++){
			list.add(i);
		}
		System.out.println(System.currentTimeMillis() - curr);
		curr = System.currentTimeMillis();
		for(int i = 0; i < times; i++){
			list.contains(i);
		}
		System.out.println(System.currentTimeMillis() - curr);
		curr = System.currentTimeMillis();
		Iterator<Integer> it = list.iterator();
		while(it.hasNext()){
			it.next();
			it.remove();
		}
		System.out.println(System.currentTimeMillis() - curr);
		
	}
	
	
	public static void testCollection(Map<Integer, Integer> list){
		System.out.println(list.getClass().getTypeName());
		long curr = System.currentTimeMillis();
		for(int i = 0; i < times; i++){
			list.put(i, i);
		}
		System.out.println(System.currentTimeMillis() - curr);
		curr = System.currentTimeMillis();
		for(int i = 0; i < times; i++){
			list.containsKey(i);
		}
		System.out.println(System.currentTimeMillis() - curr);
		curr = System.currentTimeMillis();
		Iterator<Entry<Integer, Integer>> it = list.entrySet().iterator();
		while(it.hasNext()){
			it.next();
			it.remove();
		}
		System.out.println(System.currentTimeMillis() - curr);
		
	}
}



interface A {

    /**
     * 默认方法定义
     */
    default void method() {
        System.out.println("A's default method!");
    }

}

interface B extends A {

    /**
     * 默认方法定义
     */
//    default void method() {
//        System.out.println("B's default method!");
//    }
}

interface C extends B {

    /**
     * 默认方法定义
     */
//    default void method() {
//        System.out.println("C's default method!");
//    }
}

interface D {

    /**
     * 默认方法定义
     */
    default void method() {
        System.out.println("D's default method!");
    }
}


class L implements A, B, C, D{
	
	// C, D 距离为1， 冲突，所以C和D可以手动指定
	// B距离为 2， A距离为3, A和B在继承时method方法就已经被淘汰
	public void method() {
		System.out.println("L's default method!");
//		A.super.method();
//		B.super.method();
		C.super.method();
		D.super.method();
	}
}


class M extends L implements A, B, C, D {
	
	// L为父类,优先级最高
	// C, D 距离为1
	// B距离为 2， A距离为3
}


