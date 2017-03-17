package com.hdaheizi.base.fight.fsm.buff;

import com.hdaheizi.base.fight.skill.FightSkill;
import com.hdaheizi.base.fight.unit.DefaultAIUnit;
import com.hdaheizi.base.fight.common.FightConstants;
import com.hdaheizi.base.fight.fsm.buff.CommBuff;
import com.hdaheizi.base.util.MessageFormatter;

/**
 * 持续伤害buff
 * @author daheiz
 * @Date 2016年5月22日 下午5:20:51
 */
public class DDamBuff extends CommBuff {

	/** 全局cd */
	private int globalCd;
	
	/** 下一次伤害cd */
	private int intervalCd;
	
	/** 两次伤害间隔 */
	private int interval;
	
	/** 每次伤害值 */
	private int eachDam;
	
	
	/**
	 * 构造函数
	 * @param buffName
	 * @param hero
	 * @param skill
	 * @param type
	 * @param priority
	 * @param params
	 */
	public DDamBuff(String buffName, DefaultAIUnit hero, FightSkill skill, int type, float priority, Object... params) {
		super(buffName, hero, skill, type, priority);
		globalCd = doubleToInt(params[0]) * 1000;
		intervalCd = doubleToInt(params[1]) * 1000; // 首次攻击cd
		interval = doubleToInt(params[2]) * 1000;
		int times = (globalCd - intervalCd) / interval + 1; // 伤害次数
		eachDam = doubleToInt(params[3]) / times; // 每次伤害值
	}
	
	
	/**
	 * @see com.hdaheizi.base.fight.fsm.GameState#update(long)
	 */
	@Override
	public void update(long dt) {
		globalCd -= dt;
		intervalCd -= dt;
		if(intervalCd <= 0){
			// 造成伤害
			StringBuilder inner = new StringBuilder();
			int dam = hero.handleDam(eachDam, FightConstants.ATT_TYPE_BUFF, hero, inner, this);	
			hero.room.notifyMsg(MessageFormatter.format("{0}|dam|{1}|{2}|{3}", hero.id, name, dam, inner));
			intervalCd += interval;
		}
		if(globalCd <= 0){
			hero.removeBuff(this);
		}
 	}
}
