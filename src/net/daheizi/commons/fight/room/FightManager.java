package net.daheizi.commons.fight.room;

import net.daheizi.commons.fight.common.FightSchedule;

/**
 * 战斗管理器
 * @author daheizi
 * @Date 2016年7月10日 下午11:42:54
 */
public class FightManager {

    /** 实例 */
    private static final FightManager instance = new FightManager();

    /**
     * 获取实例
     * @return
     * @Date 2016年7月10日 下午11:44:40
     */
    public static FightManager getInstance() {
        return instance;
    }

    /**
     * 构造函数
     */
    private FightManager() {

    }
    
    /**
     * 开始战斗
     * @param room
     * @Date 2016年7月10日 下午11:48:26
     */
    public void startFight(FightRoom room) {
        FightSchedule.getInstance().schedule(room);
    }
    
    /**
     * 结束战斗
     * @param room
     * @Date 2016年7月10日 下午11:48:28
     */
    public void endFight(FightRoom room) {
        FightSchedule.getInstance().unschedule(room);
    }

}
