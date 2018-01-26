package org.daheizi.commons.fight.fsm;

import java.util.Iterator;

import org.daheizi.commons.fight.common.FightConstants;
import org.daheizi.commons.fight.common.FightFormula;
import org.daheizi.commons.fight.event.BreakEvent;
import org.daheizi.commons.fight.event.FightEvent;
import org.daheizi.commons.fight.unit.DefaultAIUnit;
import org.daheizi.commons.stl.Tuple;
import org.daheizi.commons.util.MessageFormatter;

/**
 * 战斗状态
 * @author daheizi
 * @Date 2016年6月10日 下午8:42:36
 */
public class FightState extends AbstractGameState {

    /** 攻击目标 */
    private DefaultAIUnit[] targets;

    /** 空闲cd */
    private int normCd;

    /** 起手cd */
    private int prepCd;

    /** 效果迭代器 */
    private Iterator<Tuple<String, Integer>> iterator;

    /** 攻击状态 0-准备 1-攻击 */
    private int state;


    /**
     * 构造函数
     * @param hero
     * @param times
     */
    public FightState(DefaultAIUnit hero, DefaultAIUnit[] targets) {
        super("fight", hero);
        this.targets = targets;
        this.iterator = hero.effectList.iterator();
        this.state = 0;
    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#update(long)
     */
    @Override
    public void update(long dt){
        if(!hero.hasAttTargets(FightConstants.ATT_TYPE_NORM, targets)){
            // 找不到可攻击的目标
            hero.gsm.changeState(new IdleState(hero, state == 0 ? 0 : hero.attCd));
            return;
        }
        if(normCd > 0){
            // 休息
            normCd -= dt;
            return;
        }
        if(prepCd > 0){
            // 抬手
            prepCd -= dt;
            return;
        }

        StringBuilder builder = null; // 消息存储器
        while(iterator.hasNext()){
            Tuple<String, Integer> effect = iterator.next();
            String key = effect.left;
            int param = effect.right;

            switch(key){
            case "norm":
                // 空闲
                normCd = param;
                return;

            case "prep":
                // 起手
                prepCd = param;
                builder = new StringBuilder(hero.id + "|att|prep");
                for(DefaultAIUnit target : targets){
                    if(hero.canAtt(target, FightConstants.ATT_TYPE_SKILL)){
                        builder.append("|").append(target.id);
                    }
                }
                hero.room.notifyMsg(builder.toString());
                return;

            case "effect":
                // 播放特效
                hero.room.notifyMsg(MessageFormatter.format("{0}|effect|{1}", hero.id, param));
                break;

            case "hit":
                // 近身攻击
                state = 1;
                builder = new StringBuilder(hero.id + "|att|succ");
                for(DefaultAIUnit target : targets){
                    if(hero.canAtt(target, FightConstants.ATT_TYPE_NORM)){
                        StringBuilder inner = new StringBuilder();
                        int dam = doNormAttack(target, (double)param / 100, inner);
                        builder.append(MessageFormatter.format("|{0}|{1}|{2}", target.id, dam, inner));
                    }
                }
                hero.room.notifyMsg(builder.toString());
                break;

            case "fly":
                // 弹道飞行
                state = 1;
                builder = new StringBuilder(MessageFormatter.format("{0}|fly|{1}", hero.id, param));
                for(DefaultAIUnit target : targets){
                    if(hero.canAtt(target, FightConstants.ATT_TYPE_NORM)){
                        StringBuilder inner = new StringBuilder();
                        int dam = doNormAttack(target, 1d, inner);
                        builder.append(MessageFormatter.format("|{0}|{1}|{2}", target.id, dam, inner));
                    }
                }
                hero.room.notifyMsg(builder.toString());
                break;

            default:
                // 处理特技
                handleEffect(key, param);
                break;
            }

        }

        // 攻击结束，切换到空闲状态
        hero.gsm.changeState(new IdleState(hero, hero.attCd));
    }


    /**
     * @see org.daheizi.commons.fight.fsm.AbstractGameState#handleEvent(org.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event) {
        if(event instanceof BreakEvent){
            // 被打断
            hero.room.notifyMsg(MessageFormatter.format("{0}|att|break", hero.id));    
            // 切换到空闲状态
            hero.gsm.changeState(new IdleState(hero, hero.attCd));
        }
    }


    /**
     * 处理普攻效果
     * @param key
     * @param param
     * @Date 2016年6月15日 下午9:50:22
     */
    private void handleEffect(String key, int param){

    }


    /**
     * 进行普通攻击
     * @param target
     * @param ratio
     * @param inner
     * @return
     * @Date 2016年8月28日 上午3:32:06
     */
    private int doNormAttack(DefaultAIUnit target, double ratio, StringBuilder inner){
        int dam = hero.attMethod == FightConstants.ATT_METHOD_NORM 
                ? FightFormula.calcDam(hero.att, target.def) 
                        : FightFormula.calcDam(hero.matt, target.mdef);

                dam = target.handleHitted((int) (dam * ratio), FightConstants.ATT_TYPE_NORM, hero, inner, hero);
                return dam;
    }
}
