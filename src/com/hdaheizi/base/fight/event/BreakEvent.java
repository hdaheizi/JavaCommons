package com.hdaheizi.base.fight.event;

import com.hdaheizi.base.fight.skill.FightSkill;

/**
 * 打断事件
 * @author daheiz
 * @Date 2016年7月17日 上午1:09:28
 */
public class BreakEvent extends FightEvent {
	
	/** 打断级别 */
	public int breakLv;
	
	/** 打断来源 */
	public FightSkill skill;
	
	
	/**
	 * 构造函数
	 * @param breakLv
	 * @param skill
	 */
	public BreakEvent(int breakLv, FightSkill skill){
		this.breakLv = breakLv;
		this.skill = skill;
	}
	
}
