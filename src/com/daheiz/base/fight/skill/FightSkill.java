package com.daheiz.base.fight.skill;

import com.daheiz.base.fight.event.BreakEvent;
import com.daheiz.base.fight.unit.DefaultAIUnit;

/**
 * 技能接口
 * @author daheiz
 * @Date 2016年5月22日 下午10:31:08
 */
public interface FightSkill {

	/**
	 * 获取技能属性配置
	 * @return
	 * @Date 2016年5月22日 下午10:31:45
	 */
	SkillAttribute getSkillAttribute();

	/**
	 * 获取相关属性参数
	 * @param key
	 * @param defaultValue
	 * @return
	 * @Date 2016年6月16日 下午8:27:42
	 */
	double getAttribute(String key, double defaultValue);

	/**
	 * 查找作用对象
	 * @param method
	 * @return
	 * @Date 2016年7月16日 上午2:51:22
	 */
	DefaultAIUnit[] findTargets(String method);

	/**
	 * 释放近身攻击
	 * @param times 施法次数
	 * @param ratio 伤害比例
	 * @Date 2016年7月16日 下午4:43:28
	 */
	void playSkill(int times, double ratio);

	/**
	 * 释放弹道攻击
	 * @param times 施法次数
	 * @param flyId 弹道id
	 * @Date 2016年8月18日 下午10:26:24
	 */
	void flySkill(int times, int flyId);

	/**
	 * 处理技能效果
	 * @param key
	 * @param param
	 * @Date 2016年5月22日 下午10:32:06
	 */
	void handleEffect(String key, int param);

	/**
	 * 是否可以被打断
	 * @param event
	 * @return
	 * @Date 2016年7月19日 下午9:48:21
	 */
	boolean canBeBreak(BreakEvent event);

	/**
	 * 技能被打断时触发
	 * @Date 2016年7月17日 上午1:42:01
	 */
	void onBreak();

	/**
	 * 技能释放完成后回调
	 * @Date 2016年7月19日 下午9:33:40
	 */
	void onOver();

	/**
	 * 获取技能释放者
	 * @return
	 * @Date 2016年8月18日 下午10:37:47
	 */
	DefaultAIUnit getSourceHero();

	/**
	 * 进行技能攻击
	 * @param target
	 * @param ratio 伤害比例
	 * @param inner
	 * @return
	 * @Date 2016年8月28日 上午3:44:33
	 */
	int doSkillAttack(DefaultAIUnit target, double ratio, StringBuilder inner);

}
