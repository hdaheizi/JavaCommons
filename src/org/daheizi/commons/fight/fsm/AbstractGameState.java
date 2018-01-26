package org.daheizi.commons.fight.fsm;

import org.daheizi.commons.fight.event.FightEvent;
import org.daheizi.commons.fight.unit.DefaultAIUnit;

/**
 * 抽象状态类
 * @author daheizi
 * @Date 2016年5月22日 下午5:16:03
 */
public abstract class AbstractGameState implements GameState {

    /** 状态名称 */
    protected String name;

    /** 拥有状态的AI */
    protected DefaultAIUnit hero;

    /**
     * 构造函数
     * @param name
     * @param hero
     */
    public AbstractGameState(String name, DefaultAIUnit hero) {
        this.name = name;
        this.hero = hero;
    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#onEnter()
     */
    @Override
    public void onEnter(){

    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#onExit()
     */
    @Override
    public void onExit(){

    }


    /**
     * @see org.daheizi.commons.fight.fsm.GameState#handleEvent(org.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event){

    }
}
