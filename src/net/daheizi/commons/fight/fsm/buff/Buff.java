package net.daheizi.commons.fight.fsm.buff;

import net.daheizi.commons.fight.fsm.GameState;
import net.daheizi.commons.fight.skill.FightSkill;
import net.daheizi.commons.fight.unit.DefaultAIUnit;

/**
 * Buff
 * @author daheizi
 * @Date 2016年5月22日 下午5:11:07
 */
public interface Buff extends GameState{

    /**
     * 获取buff类型 1-增益 2-减益 3-控制
     * @return
     * @Date 2016年4月13日 下午11:11:46
     */
    int getType();

    /**
     * 获取产生buff的技能
     * @return
     * @Date 2016年8月18日 下午10:39:06
     */
    FightSkill getSourceSkill();

    /**
     * 获取拥有buff的英雄
     * @return
     * @Date 2016年8月27日 上午1:05:44
     */
    DefaultAIUnit getSourceHero();

    /**
     * 获取buff优先级
     * @return
     * @Date 2016年4月13日 下午11:11:43
     */
    float getPriority();

    /**
     * 融合buff处理
     * @return
     * @Date 2016年4月13日 下午11:51:21
     */
    boolean mixBuff(Buff buff);

    /**
     * 是否命中
     * @param att 攻击方
     * @param def 防守方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年8月28日 上午12:03:13
     */
    boolean succAttack(DefaultAIUnit att, DefaultAIUnit def, int attType, StringBuilder inner, Object source);

    /**
     * 计算伤害
     * @param dam 伤害大小
     * @param att 攻击方
     * @param def 防守方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return 计算结果
     * @Date 2016年8月28日 上午12:05:12
     */
    int calcDam(int dam, DefaultAIUnit att, DefaultAIUnit def, int attType, StringBuilder inner, Object source);

}
