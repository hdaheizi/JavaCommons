package org.daheiz.base.fight.skill;

import org.daheiz.base.fight.common.FightConstants;
import org.daheiz.base.fight.common.FightFormula;
import org.daheiz.base.fight.event.BreakEvent;
import org.daheiz.base.fight.event.BuffEvent;
import org.daheiz.base.fight.unit.DefaultAIUnit;
import org.daheiz.base.util.MessageFormatter;

/**
 * 通用技能类
 * @author daheiz
 * @Date 2016年6月6日 下午11:21:50
 */
public class CommSkill implements FightSkill {

    /** 攻击的目标 */
    protected DefaultAIUnit[] targets;

    /** 对目标造成的伤害 */
    protected int[] dams;

    /** 消息存储器 */
    protected StringBuilder inners[];

    /** 释放技能的AI */
    protected DefaultAIUnit hero;

    /** 技能属性类 */
    protected SkillAttribute skillAttribute;



    /**
     * 构造函数
     * @param hero
     * @param skillAttribute
     */
    public CommSkill(DefaultAIUnit hero, SkillAttribute skillAttribute){
        this.hero = hero;
        this.skillAttribute = skillAttribute;
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#getSkillAttribute()
     */
    @Override
    public SkillAttribute getSkillAttribute() {
        return skillAttribute;
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#getAttribute(java.lang.String, double)
     */
    @Override
    public double getAttribute(String key, double defaultValue){
        Double value = skillAttribute.attributeMap.get(key);
        if(null == value){
            return defaultValue;
        }
        return value;
    }


    /**
     * 查找作用目标
     * @return
     * @Date 2016年7月16日 上午2:54:49
     */
    protected DefaultAIUnit[] doFindTargets(){
        return hero.room.findTargets(hero, skillAttribute.skill.getFindMethod());
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#findTargets(java.lang.String)
     */
    @Override
    public final DefaultAIUnit[] findTargets(String method){
        if("prep".equals(method) 
                && !hero.hasAttTargets(FightConstants.ATT_TYPE_SKILL, targets) 
                || "fly".equals(method) 
                || "hit".equals(method)){
            // 重新查找目标
            targets = doFindTargets();
            dams = new int[targets.length];
            inners = new StringBuilder[targets.length];
        }
        // 上一次查找的目标
        return targets;
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#playSkill(int, double)
     */
    @Override
    public void playSkill(int times, double ratio) {
        boolean succ = false;
        StringBuilder builder = new StringBuilder(MessageFormatter.format(
                "{0}|satt|{1}|succ", hero.id, skillAttribute.skill.getSkillId()));
        // 重新查找目标
        findTargets("hit");
        for(int i = 0; i < targets.length; i++){
            if(hero.canAtt(targets[i], FightConstants.ATT_TYPE_SKILL)){
                succ = true;
                inners[i] = new StringBuilder();
                dams[i] = doSkillAttack(targets[i], 1d, inners[i]);
                builder.append(MessageFormatter.format("|{0}|{1}|{2}", targets[i].id, dams[i], inners[i]));
            }
        }
        if(succ){
            hero.room.notifyMsg(builder.toString());
        }
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#flySkill(int, int)
     */
    @Override
    public void flySkill(int times, int flyId) {
        // 重新查找目标
        findTargets("fly"); 
        boolean succ = false;
        StringBuilder builder = new StringBuilder(MessageFormatter.format("{0}|sfly|{1}", hero.id, flyId));
        for(int i = 0; i < targets.length; i++){
            if(hero.canAtt(targets[i], FightConstants.ATT_TYPE_SKILL)){
                succ = true;
                inners[i] = new StringBuilder();
                dams[i] = doSkillAttack(targets[i], 1d, inners[i]);
                builder.append(MessageFormatter.format("|{0}|{1}|{2}", targets[i].id, dams[i], inners[i]));
            }
        }
        if(succ){
            hero.room.notifyMsg(builder.toString());
        }
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#handleEffect(java.lang.String, int)
     */
    @Override
    public void handleEffect(String key, int param) {
        switch (key) {
        case "xuanyun":
        case "dingshen":
        {
            double[] params = skillAttribute.paramMap.get(key);
            double time = params[0] * 1000;
            double breakLv = params[1];
            for(int i = 0; i < targets.length; i++){
                DefaultAIUnit target = targets[i];
                if(dams[i] >= 0 && hero.canAtt(target, FightConstants.ATT_TYPE_SKILL)){
                    target.handleEvent(new BuffEvent(key, this, time, breakLv));
                }
            }
            break;
        }

        case "huoyan":
        case "poison":
        {
            double[] params = skillAttribute.paramMap.get(key);
            for(int i = 0; i < targets.length; i++){
                DefaultAIUnit target = targets[i];
                if(dams[i] >= 0 && hero.canAtt(target, FightConstants.ATT_TYPE_SKILL)){
                    double totalDam = params[3] * dams[i];
                    target.handleEvent(new BuffEvent(key, this, params[0], params[1], params[2], totalDam));
                }
            }
            break;
        }

        default:
            handleSpecialEffect(key, param);
            break;
        }
    }


    /**
     * 处理特殊技能效果
     * @param key
     * @param param
     * @Date 2016年7月17日 下午5:27:34
     */
    protected void handleSpecialEffect(String key, int param) {

    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#canBeBreak(org.daheiz.base.fight.event.BreakEvent)
     */
    @Override
    public boolean canBeBreak(BreakEvent event) {
        return event.breakLv >= skillAttribute.skill.getBreakLv();
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#onBreak()
     */
    @Override
    public void onBreak() {

    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#onOver()
     */
    @Override
    public void onOver() {

    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#getSourceHero()
     */
    @Override
    public DefaultAIUnit getSourceHero() {
        return hero;
    }


    /**
     * @see org.daheiz.base.fight.skill.FightSkill#doSkillAttack(org.daheiz.base.fight.unit.DefaultAIUnit, double, java.lang.StringBuilder)
     */
    @Override
    public int doSkillAttack(DefaultAIUnit target, double ratio, StringBuilder inner){
        // 计算伤害
        int dam = FightFormula.calcSkillDam(
                hero.att, hero.matt, 
                target.def, target.mdef, 
                getAttribute("damR", 1), getAttribute("mdamR", 1), 
                (int) getAttribute("damE", 0), (int) getAttribute("mdamE", 0));

        dam = target.handleHitted((int) (dam * ratio), FightConstants.ATT_TYPE_SKILL, hero, inner, this);

        return dam;
    }

}
