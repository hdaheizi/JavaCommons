package org.daheiz.base.fight.skill;

import org.daheiz.base.fight.unit.DefaultAIUnit;

/**
 * 技能工厂
 * @author daheiz
 * @Date 2016年6月6日 下午11:25:02
 */
public class SkillFactory {


    /**
     * 创建技能
     * @param hero
     * @param skill
     * @return
     * @Date 2016年7月13日 上午12:14:05
     */
    public static FightSkill createSkill(DefaultAIUnit hero, SkillAttribute skill){
        switch (skill.skill.getName().toLowerCase()) {
        case "huoyanranshao":
            return new Huoyanranshao(hero, skill);
        case "liuxingchui":
            return new Liuxingchui(hero, skill);
        default:
            return new CommSkill(hero, skill);
        }
    }

}
