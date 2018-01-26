package org.daheizi.commons.test.unit;

/**
 * 用户实体类
 * @author zhaodi
 * @Date 2015年8月23日 下午6:16:54
 */
public class User {

    /** id */
    private String id;
    /** name */
    private String name;
    /** password */
    private String password;
    

    /**
     * @return
     * @Date 2015年8月23日 下午8:51:56
     */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
