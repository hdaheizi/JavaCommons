package com.daheiz.base.util;

import java.util.Random;

import com.reign.util.random.RandomUtils;

/**
 * 随机工具类
 * @author daheiz
 * @Date 2016年6月16日 上午2:33:49
 */
public class RandomUtil {

	private static final Random random = new Random();

	/**
	 * 获得一个[0,1)的随机数
	 * @return 
	 */
	public static double nextDouble() {
		return random.nextDouble();
	}

	/**
	 * 获得一个[0,max)的随机数
	 * @param max
	 * @return
	 */
	public static double nextDouble(double max) {
		return random.nextDouble() * max;
	}

	/**
	 * 获得一个[min, max)的随机数
	 * @param min
	 * @param max
	 * @return
	 */
	public static double nextDouble(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}

	/**
	 * 获得一个随机整数
	 * @return
	 */
	public static int nextInt() {
		return random.nextInt();
	}

	/**
	 * 获得[0,n)随机整数
	 * @return
	 */
	public static int nextInt(int n) {
		return random.nextInt(n);
	}

	/**
	 * 获得[min,max)的随机整数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int nextInt(int min, int max) {
		return min + random.nextInt(max - min);
	}

	/**
	 * 获得[0,1)的随机float
	 * @return
	 */
	public static float nextFloat() {
		return random.nextFloat();
	}

	/**
	 * 随机获得true或false
	 * @return
	 */
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	/**
	 * 随机数组
	 * @param array
	 * @param start
	 * @param end
	 */
	public static <T> void randomArray(T[] array, int start, int end) {
		for (int i = start; i < end ; i++) {
			int index = nextInt(end - i) + i;
			swap(array, i, index);
		}
	}

	/**
	 * 交换数据
	 * @param <T>
	 * @param array
	 * @param index1
	 * @param index2
	 */
	public static <T> void swap(T[] array, int index1, int index2) {
		T temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}

	/**
	 * 获取大中小
	 * 概率相加之和必须为100!
	 * @param probList 
	 * @return
	 */
	public static int getRandomIndex(int[] probList) {
		int randomNum = RandomUtils.nextInt(100);
		int num = 0;
		for (int index = 0; index < probList.length; index++) {
			num += probList[index];
			if (randomNum < num) {
				return index;
			}
		}
		return -1;
	}

}
