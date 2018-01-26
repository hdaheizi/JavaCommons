package org.daheiz.base.fight.event;


/**
 * 事件处理器
 * @author daheiz
 * @Date 2016年4月5日 上午2:40:15
 */
public interface EventHandler {

    /**
     * 添加事件, 处理时机由事件延迟决定
     * @param event
     * @Date 2016年4月5日 上午10:32:52
     */
    void addEvent(FightEvent event);


    /**
     * 处理事件, 无视延迟，立即处理
     * @param event
     * @Date 2016年4月5日 上午10:32:50
     */
    void handleEvent(FightEvent event);

}
