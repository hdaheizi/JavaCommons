package top.daheizi.commons.test.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 替换型代理
 * @author daheizi
 * @Date 2017年3月14日 下午5:42:12
 */
public class SimulateProxyHandler implements InvocationHandler {
    
    /** 被代理的对象 */
    Object proxied;
    
    /**
     * 构造函数
     * @param proxied
     * @param realHandler
     */
    public SimulateProxyHandler(Object proxied) {
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
            Method realMethod = proxied.getClass().getMethod(method.getName(), method.getParameterTypes());
            realMethod.setAccessible(true);
            result = realMethod.invoke(proxied, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {}
        
        return result;
    }
    
    /**
     * 返回默认的代理对象
     * @param proxied
     * @param simulateInterface 要模拟的接口
     * @return
     * @Date 2017年3月14日 下午5:53:03
     */
    public static Object getDefaultProxyHandler(Object proxied, Class<?> simulateInterface) {
        return Proxy.newProxyInstance(simulateInterface.getClassLoader(), 
                new Class<?>[]{simulateInterface}, 
                new SimulateProxyHandler(proxied));
    }

}
