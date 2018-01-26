package net.daheizi.commons.fight.fsm.buff;

import net.daheizi.commons.fight.event.BreakEvent;
import net.daheizi.commons.fight.skill.FightSkill;
import net.daheizi.commons.fight.unit.DefaultAIUnit;

/**
 * 眩晕buff
 * @author daheizi
 * @Date 2016年7月17日 下午3:44:26
 */
public class XuanyunBuff extends CommBuff {

    /** 定身时间 */
    protected int cd;

    /** 打断级别 */
    protected int breakLv;


    /**
     * 构造函数
     * @param buffName
     * @param hero
     * @param skill
     * @param type
     * @param priority
     */
    public XuanyunBuff(String buffName, DefaultAIUnit hero, FightSkill skill, int type, float priority, Object... params) {
        super(buffName, hero, skill, type, priority);
        this.cd = doubleToInt(params[0]);
        this.breakLv = doubleToInt(params[1]);
    }


    /**
     * @see net.daheizi.commons.fight.fsm.buff.CommBuff#onEnter()
     */
    @Override
    public void onEnter() {
        super.onEnter();
        hero.sleep();
        hero.handleEvent(new BreakEvent(breakLv, skill));
    }


    /**
     * @see net.daheizi.commons.fight.fsm.buff.CommBuff#onExit()
     */
    @Override
    public void onExit() {
        hero.wakeUp();
        super.onExit();
    }


    /**
     * @see net.daheizi.commons.fight.fsm.buff.CommBuff#mixBuff(net.daheizi.commons.fight.fsm.buff.Buff)
     */
    @Override
    public boolean mixBuff(Buff buff) {
        if(name.equals(buff.getName())){
            // 取较长cd
            XuanyunBuff _buff = (XuanyunBuff) buff;
            _buff.cd = Math.max(cd, _buff.cd);
            hero.removeBuff(this);
            hero.addBuff(_buff);
            return true;
        }
        return false;
    }


    /**
     * @see net.daheizi.commons.fight.fsm.GameState#update(long)
     */
    @Override
    public void update(long dt) {
        cd -= dt;
        if(cd <= 0){
            hero.removeBuff(this);
        }
    }

}
