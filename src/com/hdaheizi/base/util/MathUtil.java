package com.hdaheizi.base.util;

public class MathUtil {
	
	/**
	 * 精确到n位小数
	 * @param number
	 * @param digit
	 * @return
	 * @Date 2017年2月27日 下午6:35:19
	 */
	public static double demical(double number, int digit){
		double salt = Math.pow(10, digit);
		return (int)(number * salt + 0.5) / salt;
	}

}
