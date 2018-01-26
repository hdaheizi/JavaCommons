package top.daheizi.commons.fight.skill;

import top.daheizi.commons.fight.unit.DefaultAIUnit;

/**
 * 流星锤 打断目标技能，并使其眩晕
 * @author daheizi
 * @Date 2016年7月17日 下午4:51:41
 */
public class Liuxingchui extends CommSkill {

    /**
     * 构造函数
     * @param hero
     * @param skillAttribute
     */
    public Liuxingchui(DefaultAIUnit hero, SkillAttribute skillAttribute) {
        super(hero, skillAttribute);
    }
}
