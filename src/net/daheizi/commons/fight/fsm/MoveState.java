package net.daheizi.commons.fight.fsm;

import net.daheizi.commons.fight.unit.DefaultAIUnit;
import net.daheizi.commons.util.MessageFormatter;

/**
 * 移动状态
 * @author daheizi
 * @Date 2016年6月10日 下午5:41:19
 */
public class MoveState extends AbstractGameState {

    /** 移动时间 */
    private int cd;
    /** 目标x */
    private int toX;
    /** 目标Y */
    private int toY;

    /**
     * 构造函数
     * @param hero
     * @param toX
     * @param toY
     * @param cd
     */
    public MoveState(DefaultAIUnit hero, int toX, int toY, int cd) {
        super("move", hero);
        this.toX = toX;
        this.toY = toY;
        this.cd = cd;
    }

    /**
     * @see net.daheizi.commons.fight.fsm.AbstractGameState#onEnter()
     */
    @Override
    public void onEnter() {
        hero.moveTo(toX, toY);
        hero.room.notifyMsg(MessageFormatter.format("{0}|{1}|{2}|{3}|{4}", hero.id, name, toX, toY, cd));
    }


    /**
     * @see net.daheizi.commons.fight.fsm.GameState#update(long)
     */
    @Override
    public void update(long dt) {
        cd -= dt;
        if(cd <= 0){
            hero.gsm.changeState(new IdleState(hero, 0));
        }
    }
}
