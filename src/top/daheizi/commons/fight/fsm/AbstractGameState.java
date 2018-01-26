package top.daheizi.commons.fight.fsm;

import top.daheizi.commons.fight.event.FightEvent;
import top.daheizi.commons.fight.unit.DefaultAIUnit;

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
     * @see top.daheizi.commons.fight.fsm.GameState#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }


    /**
     * @see top.daheizi.commons.fight.fsm.GameState#onEnter()
     */
    @Override
    public void onEnter(){

    }


    /**
     * @see top.daheizi.commons.fight.fsm.GameState#onExit()
     */
    @Override
    public void onExit(){

    }


    /**
     * @see top.daheizi.commons.fight.fsm.GameState#handleEvent(top.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event){

    }
}
