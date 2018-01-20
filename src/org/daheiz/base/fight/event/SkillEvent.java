package org.daheiz.base.fight.event;

import org.daheiz.base.fight.skill.SkillAttribute;

/**
 * 技能事件
 * @author daheiz
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
