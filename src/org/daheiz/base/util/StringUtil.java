package org.daheiz.base.util;

import java.util.Map;

/**
 * 一些处理字符串的工具
 * @author daheiz
 * @Date 2018年1月27日 上午4:21:02
 */
public class StringUtil {

    /** EMPTY_STR */
    public static final String EMPTY_STR = "";
    
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     * @Date 2018年1月27日 上午4:33:49
     */
    public static final boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 将map连接成k1=v1&k2=v2...字符串
     * @param paramsMap
     * @return
     * @Date 2018年1月27日 上午4:26:07
     */
    public static String buildParamsMapStr(final Map<Object, Object> paramsMap) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return EMPTY_STR;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : paramsMap.entrySet()) {
            builder.append(entry.getKey())
                   .append(Symbols.EQUAL)
                   .append(entry.getValue())
                   .append(Symbols.AMP);
        }
        return builder.substring(0, builder.length() - 1);
    }
}
