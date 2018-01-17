package com.daheiz.base.util;

/**
 * 数学工具类
 * @author daheiz
 * @Date 2017年3月18日 下午7:58:04
 */
public class MathUtil {

	/**
	 * 四舍五入精确到n位小数
	 * @param number
	 * @param digit
	 * @return
	 * @Date 2017年2月27日 下午6:35:19
	 */
	public static double demical(double number, int digit){
		double multiples = Math.pow(10, digit);
		return Math.round(number * multiples) / multiples;
	}
}
