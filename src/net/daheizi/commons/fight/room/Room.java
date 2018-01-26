package net.daheizi.commons.fight.room;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 房间
 * @author daheizi
 * @Date 2016年7月10日 下午6:11:10
 */
public abstract class Room{

    /** 房间id生成器 */
    protected static AtomicInteger idGenerator = new AtomicInteger();

    /** 房间id */
    protected int roomId;


    /**
     * 构造函数
     */
    public Room(){
        this.roomId = idGenerator.incrementAndGet();
    }


    /**
     * 获取房间id
     * @return
     * @Date 2016年7月10日 下午6:11:59
     */
    public int getRoomId(){
        return roomId;
    }


}
