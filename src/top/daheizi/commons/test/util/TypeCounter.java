package top.daheizi.commons.test.util;

import java.util.HashMap;

/**
 * 类型计数器
 * @author daheiz
 * @Date 2017年1月9日 下午3:27:16
 */
public class TypeCounter extends HashMap<Class<?>, Integer> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Class<?> baseType;
    
    public TypeCounter(Class<?> baseType) {
        if(baseType == null){
            throw new RuntimeException("The baseType can not be null !");
        }
        this.baseType = baseType;
    }
    
    public void count(Object obj) {
        Class<?> type = obj.getClass();
        countClass(type);
    }

    private void countClass(Class<?> type) {
        if(!baseType.isAssignableFrom(type)) {
            return;
        }
        Integer quantity = get(type);
        put(type, null == quantity ? 1 : quantity + 1);
        Class<?> superClass = type.getSuperclass();
        if(superClass != null) {
            countClass(superClass);
        }
        for(Class<?> eachInterface : type.getInterfaces()) {
            countClass(eachInterface);
        }
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for(java.util.Map.Entry<Class<?>, Integer> pair : entrySet()) {
            result.append(pair.getKey().getSimpleName())
                .append("=")
                .append(pair.getValue())
                .append(", ");
        }
        if(result.length() > 1){
            result.delete(result.length() - 2, result.length());
        }
        result.append("}");
        return result.toString();
    }
    
}