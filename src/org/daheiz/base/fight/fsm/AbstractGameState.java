package org.daheiz.base.fight.fsm;

import org.daheiz.base.fight.event.FightEvent;
import org.daheiz.base.fight.unit.DefaultAIUnit;

/**
 * 抽象状态类
 * @author daheiz
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
     * @see org.daheiz.base.fight.fsm.GameState#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }


    /**
     * @see org.daheiz.base.fight.fsm.GameState#onEnter()
     */
    @Override
    public void onEnter(){

    }


    /**
     * @see org.daheiz.base.fight.fsm.GameState#onExit()
     */
    @Override
    public void onExit(){

    }


    /**
     * @see org.daheiz.base.fight.fsm.GameState#handleEvent(org.daheiz.base.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event){

    }
}
