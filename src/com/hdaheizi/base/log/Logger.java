package com.hdaheizi.base.log;

/**
 * Logger 通用标准接口类
 * @author zhaodi
 * @version 1.0.0.0 2013-12-12 下午08:36:35
 */
public interface Logger {
	/**
	 * 获取日志的名称
	 */
	String name();

	/**
	 * 输出ERROR级别日志
	 * @param msg
	 * @version 1.0.0.0 2013-12-12 下午08:38:01
	 */
	void error(String msg);

	/**
	 * 输出ERROR级别日志
	 * @param format
	 * @param arg
	 * @version 1.0.0.0 2013-12-12 下午08:38:43
	 */
	void error(String format, Object... arg);

	/**
	 * 输出ERROR级别日志
	 * @param msg
	 * @param t
	 * @version 1.0.0.0 2013-12-12 下午08:39:51
	 */
	void error(String msg, Throwable t);

	/**
	 * 输出ERROR级别日志
	 * @param format
	 * @param t
	 * @param arg
	 * @version 1.0.0.0 2013-12-12 下午08:39:51
	 */
	void error(String format, Throwable t, Object... arg);

	/**
	 * 输出INFO级别日志
	 * @param msg
	 * @version 1.0.0.0 2013-12-12 下午08:38:01
	 */
	void info(String msg);

	/**
	 * 输出INFO级别日志
	 * @param format
	 * @param arg
	 * @version 1.0.0.0 2013-12-12 下午08:38:43
	 */
	void info(String format, Object... arg);
}
