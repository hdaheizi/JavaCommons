/*
 * $Header: DefaultLogger.java
 * $Revision: 1.0.0.0 
 * $CreateDate: 2014-1-3
 * $ModifyDate: 2014-1-3 上午11:40:35
 * $Owner: 
 *
 * Copyright (c) 2011-2012 Shanghai Reign Co. Ltd.
 * All Right Reserved.
 */
package com.hdaheizi.base.log;

import com.hdaheizi.base.util.MessageFormatter;

/**
 * DefaultLogger
 * @author   wangys
 * @version  1.0.0.0  2014-1-3 上午11:40:35
 */
public class DefaultLogger extends AbstractLogger {
	public DefaultLogger(String name) {
		super(name);
	}

	/**
	 * @see com.reign.framework.log.Logger#error(java.lang.String)
	 */
	@Override
	public void error(String msg) {
		System.out.println(name + "##error##" + msg);
	}

	/**
	 * @see com.reign.framework.log.Logger#error(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void error(String format, Object... arg) {
		error(MessageFormatter.format(format, arg));
	}

	/**
	 * @see com.reign.framework.log.Logger#error(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void error(String msg, Throwable t) {
		error(msg);
		t.printStackTrace();
	}

	/**
	 * @see com.reign.framework.log.Logger#error(java.lang.String, java.lang.Throwable, java.lang.Object[])
	 */
	@Override
	public void error(String format, Throwable t, Object... arg) {
		error(format, arg);
		t.printStackTrace();
	}

	/**
	 * @see com.hdaheizi.base.log.Logger#info(java.lang.String)
	 */
	@Override
	public void info(String msg) {
		System.out.println(name + "##info##" + msg);
		
	}

	/**
	 * @see com.hdaheizi.base.log.Logger#info(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void info(String format, Object... arg) {
		info(MessageFormatter.format(format, arg));
		
	}

}
