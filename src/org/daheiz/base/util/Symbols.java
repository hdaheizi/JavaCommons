package org.daheiz.base.util;

/**
 * 常用符号集合
 * @author daheiz
 * @Date 2018年1月27日 上午4:49:17
 */
public final class Symbols {
    
    /**
     * 截止实例化
     */
    private Symbols() {}
    
    /** BLANK */
    public static final String BLANK = " ";
    /** TAB */
    public static final String TAB = "\t";
    /** WRAP */
    public static final String WRAP = "\r\n";
    /** ENTER */
    public static final String ENTER = "\r";
    /** NEW_LINE */
    public static final String NEW_LINE = "\n";
    /** | */
    public static final String SPLIT_VERTICAL = "\\|";
    /** & */
    public static final String AMP = "&";
    /** 冒号 */
    public static final String COLON = ":";
    /** 逗号 */
    public static final String COMMA = ",";
    /** 分号 */
    public static final String SEMICOLON = ";";
    /** 下划线 */
    public static final String UNDERLINE = "_";
    /** 等号 */
    public static final String EQUAL = "=";
    /** 加号 */
    public static final String ADD = "+";
    /** 减号 */
    public static final String MINUS = "-";
    /** 问号*/
    public static final String QUESTION = "?";
    /** 或 */
    public static final String OR = "\\|\\|";
    
    /** 冒号 */
    public static final char[] B_COLON = { ':' };
    /** 逗号 */
    public static final char[] B_COMMA = { ',' };
    /** { */
    public static final char[] B_L_BRACE = { '{' };
    /** } */
    public static final char[] B_R_BRACE = { '}' };
    /** [ */
    public static final char[] B_L_BRACKET = { '[' };
    /** ] */
    public static final char[] B_R_BRACKET = { ']' };
    /** " */
    public static final char[] B_QUOT = { '\"' };
    
    /** < */
    public static final String LT = "<";
       /** > */
    public static final String RT = ">";
    /** </ */
    public static final String LT_END = "</";
    /** /> */
    public static final String RT_END = "/>";
    /** " */
    public static final String QUOT = "\"";
    /** { */
    public static final String L_BRACE = "{";
    /** } */
    public static final String R_BRACE = "}";
    /** ( */
    public static final String L_ROUND_BRACKET = "(";
    /** ) */
    public static final String R_ROUND_BRACKET = ")";
    /** [ */
    public static final String L_BRACKET = "[";
    /** ] */
    public static final String R_BRACKET = "]";

    /** 中文乘号*/
    public static final String CHINESE_MULT = "×";
    /** 中文逗号*/
    public static final String CHINESE_COMMA = "，";
    /** 方块点*/
    public static final String CHINESE_DOT = "▪";
}
