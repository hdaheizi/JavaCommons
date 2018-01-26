package org.daheizi.commons.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.daheizi.commons.stl.Tuple;

/**
 * 带有缓存的消息格式化器
 * @author daheizi
 * @Date 2016年3月17日 上午1:43:09
 */
public class MessageFormatter {

    /** 标准字串模板 */
    private String pattern;

    /** 安装列表 <安装位置, 参数索引>*/
    private List<Tuple<Integer, Integer>> offsetList;

    /** 格式化器缓存 <原始字符串, 消息格式化器> */
    private static Map<String, MessageFormatter> cacheMap = new ConcurrentHashMap<>();

    /** 左标记 */
    private static char MARK_START = '{';

    /** 右标记 */
    private static char MARK_END = '}';

    /**
     * 状态枚举类
     * @author daheizi
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
     * 私有构造函数
     */
    private MessageFormatter() {}
    
    /**
     * 格式化字符串
     * @param format
     * @param params
     * @return 
     * @example format("{0}#{2}#{{1}}#{1}", "obj0", "obj1", "obj2" )
     *             --> return obj0#obj2#{1}#obj1 
     * @Date 2016年3月17日 上午2:03:49
     */
    public static String format(String format, Object... params) {
        MessageFormatter formatter = cacheMap.get(format);
        if (null == formatter) {
            // 双重检查锁机制确保唯一性
            synchronized (cacheMap) {
                formatter = cacheMap.get(format);
                if (null == formatter) {
                    formatter = createFormatter(format);
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
        StringBuilder builder = new StringBuilder(pattern.length());
        int lastOffset = 0;
        for(Tuple<Integer, Integer> offset : offsetList) {
            builder.append(pattern.substring(lastOffset, offset.left));
            if (offset.right < params.length) {
                builder.append(params[offset.right]);
            }
            lastOffset = offset.left;
        }
        builder.append(pattern.substring(lastOffset));
        return builder.toString();
    }
    
    /**
     * 创建一个格式化器
     * @param format
     * @return
     * @Date 2018年1月17日 下午11:33:21
     */
    private static MessageFormatter createFormatter(String format) {
        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder indexBuilder = new StringBuilder();
        List<Tuple<Integer, Integer>> offsetList = new ArrayList<>(8);
        boolean error = null == format;    // 记录解析过程中是否发现错误
        PatternState currState = PatternState.NORMAL;    // 当前状态
        for(int i = 0; !error && i < format.length(); i++) {
            char ch = format.charAt(i);        
            switch (currState) {    
            case NORMAL:
                if (ch == MARK_START) {
                    currState = PatternState.OPEN;
                } else if (ch == MARK_END) {
                    currState = PatternState.CLOSE;
                } else {
                    patternBuilder.append(ch);
                }
                break;        
            case OPEN:
                if (ch == MARK_START) {
                    patternBuilder.append(MARK_START);
                    currState = PatternState.NORMAL;    
                } else if (Character.isDigit(ch)) {
                    indexBuilder.append(ch);
                    currState = PatternState.FILLING;
                } else {
                    error = true;
                }
                break;        
            case CLOSE:
                if (ch == MARK_END) {
                    patternBuilder.append(MARK_END);
                    currState = PatternState.NORMAL;
                } else {
                    error = true;
                }
                break;    
            case FILLING:
                if (Character.isDigit(ch)) {
                    indexBuilder.append(ch);
                } else if (ch == MARK_END) {
                    offsetList.add(new Tuple<>(patternBuilder.length(), 
                            Integer.parseInt(indexBuilder.toString())));
                    indexBuilder.setLength(0);
                    currState = PatternState.NORMAL;
                } else {
                    error = true;
                }
                break;
            default:
                error = true;
                break;
            }
        }
        if (error || currState != PatternState.NORMAL) {
            // 格式化失败
            throw new RuntimeException("Message format failed on: " + format);
        }
        // 创建格式化器
        MessageFormatter formatter = new MessageFormatter();
        formatter.pattern = patternBuilder.toString();
        formatter.offsetList = offsetList;
        return formatter;
    }
}

