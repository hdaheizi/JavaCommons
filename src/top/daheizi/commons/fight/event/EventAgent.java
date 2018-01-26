package top.daheizi.commons.fight.event;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 事件代理器
 * @author daheizi
 * @Date 2016年4月5日 上午9:58:21
 */
public class EventAgent{

    /** 委托者 */
    private EventHandler handler;

    /** 事件缓存列表 */
    private Queue<FightEvent> eventList = new ConcurrentLinkedQueue<>();


    /**
     * 构造函数
     * @param handler
     */
    public EventAgent(EventHandler handler){
        this.handler = handler;
    }


    /**
     * 添加事件
     * @param event
     * @Date 2016年4月5日 上午2:56:14
     */
    public void addEvent(FightEvent event){
        eventList.add(event);
    }


    /**
     * 更新事件缓存
     * @param dt
     * @param dispatchNum
     * @Date 2016年4月5日 上午2:48:57
     */
    public void update(long dt){
        Iterator<FightEvent> it = eventList.iterator();
        while(it.hasNext()){
            FightEvent event = it.next();
            event.delay -= dt;
            if(event.delay <= 0){
                it.remove();
                handler.handleEvent(event);
            }
        }
    }

}
