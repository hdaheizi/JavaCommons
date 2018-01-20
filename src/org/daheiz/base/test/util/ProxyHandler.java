package org.daheiz.base.test.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 一个简单的代理控件
 * @author daheiz
 * @Date 2017年3月14日 下午5:04:33
 */
public class ProxyHandler implements InvocationHandler {
	
	/** 被代理的对象 */
	private Object proxied;
	
	/**
	 * 构造函数
	 * @param proxied
	 */
	public ProxyHandler(Object proxied) {
		this.proxied = proxied;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		try {
			result = method.invoke(proxied, args);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			System.out.println("**** proxy: {" + 
//					"class: " + proxied.getClass().getSimpleName() + 
//					", method: " + method.getName() + 
//					", args: " + args + 
//					", result: " + result
//					+ "}");
		}

		return result;
	}
	
	/**
	 * 返回默认的代理对象
	 * @param proxied 被代理的对象
	 * @return
	 * @Date 2017年3月14日 下午5:02:35
	 */
	public static Object getDefaultProxyHandler(Object proxied) {
		Class<?> clazz = proxied.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), 
				clazz.getInterfaces(), 
				new ProxyHandler(proxied));
	}
	
}
