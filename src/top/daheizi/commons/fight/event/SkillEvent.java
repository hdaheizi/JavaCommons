package top.daheizi.commons.fight.event;

import top.daheizi.commons.fight.skill.SkillAttribute;

/**
 * 技能事件
 * @author daheizi
 * @Date 2016年5月23日 上午1:18:56
 */
public class SkillEvent extends FightEvent {

    /** 技能属性配置 */
    public SkillAttribute skill;


    /**
     * 构造函数
     * @param skillId
     */
    public SkillEvent(SkillAttribute skill) {
        this.skill = skill;
    }
}
