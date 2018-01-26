package org.daheizi.commons.fight.fsm;

import java.util.Iterator;

import org.daheizi.commons.fight.common.FightConstants;
import org.daheizi.commons.fight.event.BreakEvent;
import org.daheizi.commons.fight.event.FightEvent;
import org.daheizi.commons.fight.skill.FightSkill;
import org.daheizi.commons.fight.unit.DefaultAIUnit;
import org.daheizi.commons.stl.Tuple;
import org.daheizi.commons.util.MessageFormatter;

/**
 * 技能状态
 * @author daheizi
 * @Date 2016年6月6日 下午11:08:37
 */
public class SkillState extends AbstractGameState {

    /** 空闲cd */
    private int normCd;

    /** 起手cd */
    private int prepCd;

    /** 技能 */
    private FightSkill skill;

    /** 技能效果迭代器 */
    private Iterator<Tuple<String, Integer>> iterator;

    /** 前一个状态 */
    private GameState preState;

    /** 已攻击次数 */
    private int attTimes;


    /**
     * 构造函数
     * @param hero
     * @param skill
     * @param preState
     */
    public SkillState(DefaultAIUnit hero, FightSkill skill, GameState preState) {
        super("skill", hero);
        this.skill = skill;
        this.iterator = skill.getSkillAttribute().effectList.iterator();
        this.preState = preState;
        this.attTimes = 0;
    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#update(long)
     */
    @Override
    public void update(long dt) {
        if(normCd > 0){
            normCd -= dt;
            return;
        }
        if(prepCd > 0){
            prepCd -= dt;
            return;
        }

        while(iterator.hasNext()){
            Tuple<String, Integer> effect = iterator.next();
            String key = effect.left;
            int param = effect.right;
            switch(key){                
            case "prep":
                // 起手
                prepCd = param;
                StringBuilder builder = new StringBuilder(MessageFormatter.format(
                        "{0}|satt|{1}|prep", hero.id, skill.getSkillAttribute().skill.getSkillId()));
                for(DefaultAIUnit target : skill.findTargets("prep")){
                    if(hero.canAtt(target, FightConstants.ATT_TYPE_BUFF)){
                        builder.append("|").append(target.id);
                    }
                }
                hero.room.notifyMsg(builder.toString());
                return;
            case "norm":
                // 空闲
                normCd = param;
                return;
            case "effect":
                // 播放特效
                hero.room.notifyMsg(MessageFormatter.format("{0}|effect|{1}", hero.id, param));
                break;
            case "hit":
                // 近身攻击
                skill.playSkill(++attTimes, 1d * param / 100);
                break;
            case "fly":
                // 弹道攻击
                skill.flySkill(++attTimes, param);
                break;
            default:
                // 处理技能效果
                skill.handleEffect(key, param);
                break;
            }
        }
        // 恢复状态
        recovState();
    }


    /**
     * 恢复到释放技能前的状态
     * @Date 2016年7月17日 上午1:19:00
     */
    private void recovState(){
        if(preState != null){
            hero.gsm.changeState(preState);
        }else{
            hero.gsm.changeState(new IdleState(hero, 0));
        }
    }


    /**
     * @see org.daheizi.commons.fight.fsm.AbstractGameState#onExit()
     */
    @Override
    public void onExit() {
        // 释放完毕后回调
        skill.onOver();
        super.onExit();
    }


    /**
     * @see org.daheizi.commons.fight.fsm.AbstractGameState#handleEvent(org.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event) {
        if(event instanceof BreakEvent){
            if(skill.canBeBreak((BreakEvent) event)){
                // 技能被打断
                skill.onBreak();
                hero.room.notifyMsg(MessageFormatter.format("{0}|satt|{1}|break|{2}", hero.id, skill.getSkillAttribute().skill.getSkillId()));
                // 恢复到前一状态
                recovState();
            }
        }
    }

}
