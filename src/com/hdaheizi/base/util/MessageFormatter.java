package com.hdaheizi.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdaheizi.base.stl.Tuple;

/**
 * 消息格式化器
 * @author daheiz
 * @Date 2016年3月17日 上午1:43:09
 */
public class MessageFormatter {

	/** 标准字串模板 */
	private String pattern;

	/** 安装列表 <安装位置, 参数索引>*/
	private List<Tuple<Integer, Integer>> offsetList = new ArrayList<>(8);

	/** 格式化器缓存 <原始字符串, 消息格式化器> */
	private static Map<String, MessageFormatter> cacheMap = new HashMap<>();

	/** 格式化器缓存锁 */
	private static Object lock = new Object();

	/** 左标记 */
	private static char MARK_START = '{';

	/** 右标记 */
	private static char MARK_END = '}';

	/**
	 * 状态枚举类
	 * @author daheiz
	 * @Date 2016年3月17日 上午2:01:38
	 */
	private static enum PatternState {
		/** 普通状态 */
		NORMAL, 
		/** 开启状态 */
		OPEN, 
		/** 关闭状态 */
		CLOSE, 
		/** 填充状态 */
		FILLING, 
	}


	/**
	 * 格式化字符串
	 * @param format
	 * @param params
	 * @return 
	 * @example format("{0}#{2}#{{1}}#{1}", "obj0", "obj1", "obj2" )
	 * 			--> return obj0#obj2#{1}#obj1 
	 * @Date 2016年3月17日 上午2:03:49
	 */
	public static String format(String format, Object... params) {
		MessageFormatter formatter = cacheMap.get(format);
		if(null == formatter) {
			synchronized (lock) {
				formatter = cacheMap.get(format);
				if(null == formatter) {
					// 从缓存中读取格式化器，双重检查锁机制确保同步
					try {
						formatter = new MessageFormatter(format);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					cacheMap.put(format, formatter);
				}
			}
		}

		return formatter.format(params);
	}


	/**
	 * 对标记位置进行参数安装
	 * @param params
	 * @return
	 * @Date 2016年3月17日 上午2:04:18
	 */
	private String format(Object... params) {
		StringBuilder builder = new StringBuilder(this.pattern.length());
		int lastOffset = 0;
		for(Tuple<Integer, Integer> offset : offsetList) {
			builder.append(this.pattern.substring(lastOffset, offset.left));
			if(offset.right < params.length) {
				builder.append(params[offset.right]);
			}
			lastOffset = offset.left;
		}
		builder.append(this.pattern.substring(lastOffset));

		return builder.toString();
	}


	/**
	 * 构造函数
	 * @param format
	 */
	public MessageFormatter(String format) {
		applyFormat(format);
	}


	/**
	 * 进行格式化
	 * @param format
	 * @Date 2016年3月17日 上午2:04:37
	 */
	private void applyFormat(String format) {
		StringBuilder patternBuilder = new StringBuilder();
		StringBuilder indexBuilder = new StringBuilder();
		boolean error = null == format;	// 记录解析过程中是否发现错误
		PatternState currState = PatternState.NORMAL;	// 当前状态
		for(int i = 0; !error && i < format.length(); i++) {
			char ch = format.charAt(i);		
			switch (currState) {	
			case NORMAL:
				if(ch == MARK_START) {
					currState = PatternState.OPEN;
				}else if(ch == MARK_END) {
					currState = PatternState.CLOSE;
				}else {
					patternBuilder.append(ch);
				}
				break;		
			case OPEN:
				if(ch == MARK_START) {
					patternBuilder.append(MARK_START);
					currState = PatternState.NORMAL;	
				}else if(Character.isDigit(ch)) {
					indexBuilder.append(ch);
					currState = PatternState.FILLING;
				}else {
					error = true;
				}
				break;		
			case CLOSE:
				if(ch == MARK_END) {
					patternBuilder.append(MARK_END);
					currState = PatternState.NORMAL;
				}else {
					error = true;
				}
				break;	
			case FILLING:
				if(Character.isDigit(ch)) {
					indexBuilder.append(ch);
				}else if(ch == MARK_END) {
					offsetList.add(new Tuple<Integer, Integer>(patternBuilder.length(), 
							Integer.parseInt(indexBuilder.toString())));
					indexBuilder.setLength(0);
					currState = PatternState.NORMAL;
				}else {
					error = true;
				}
				break;
			default:
				error = true;
				break;
			}
		}
		if(error || currState != PatternState.NORMAL) {
			// 格式化失败
			throw new RuntimeException("message format failed on: " + format);
		}
		this.pattern = patternBuilder.toString();	
	}
}

