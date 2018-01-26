package org.daheizi.commons.fight.event;

import org.daheizi.commons.fight.skill.FightSkill;

/**
 * 添加buff事件
 * @author daheizi
 * @Date 2016年7月10日 下午9:33:38
 */
public class BuffEvent extends FightEvent {

    /** buff名称 */
    public String buffName;

    /** 产生buff的来源 */
    public FightSkill skill;

    /** 相关参数 */
    public Object[] params;


    /**
     * 构造函数
     * @param buffName
     * @param skill
     * @param params
     */
    public BuffEvent(String buffName, FightSkill skill, Object... params){
        this.buffName = buffName;
        this.skill = skill;
        this.params = params;
    }

}
