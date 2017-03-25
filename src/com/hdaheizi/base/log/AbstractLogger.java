package com.hdaheizi.base.log;

/**
 * AbstractLogger
 * @author zhaodi
 * @version 1.0.0.0 2013-12-12 下午08:53:55
 */
public abstract class AbstractLogger implements Logger {
	/** 日志名称 */
	protected final String name;

	/**
	 * 构造函数
	 * @param name
	 */
	public AbstractLogger(String name) {
		this.name = name;
	}

	/**
	 * @see com.reign.framework.log.Logger#name()
	 */
	@Override
	public String name() {
		return name;
	}
}
