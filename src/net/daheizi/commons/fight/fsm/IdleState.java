package net.daheizi.commons.fight.fsm;

import net.daheizi.commons.fight.common.FightConstants;
import net.daheizi.commons.fight.common.FightFormula;
import net.daheizi.commons.fight.unit.DefaultAIUnit;
import net.daheizi.commons.stl.Tuple;

/**
 * 空闲状态
 * @author daheizi
 * @Date 2016年6月10日 下午4:51:20
 */
public class IdleState extends AbstractGameState {

    /** cd */
    private int cd;

    /** 下一个状态 */
    private GameState nextState;


    /**
     * 构造函数
     * @param hero
     * @param cd
     */
    public IdleState(DefaultAIUnit hero, int cd) {
        super("idle", hero);
        this.cd = cd;
    }


    /**
     * 构造函数
     * @param hero
     * @param cd
     * @param nextState
     */
    public IdleState(DefaultAIUnit hero, int cd, GameState nextState) {
        this(hero, cd);
        this.nextState = nextState;
    }


    /**
     * @see net.daheizi.commons.fight.fsm.GameState#update(long)
     */
    @Override
    public void update(long dt) {
        cd -= dt;
        if(cd > 0){
            return;
        }else if(nextState != null){
            // 存在有下一状态
            hero.gsm.changeState(nextState);
        }else{
            // 以第一个敌人为基准
            DefaultAIUnit firstEnemy = hero.room.findFirstEnemy(hero);
            if(firstEnemy == null){
                // 找不到目标
                return;
            }
            // 查找攻击位置
            Tuple<Integer, Integer> nextPos = hero.room.findAttPos(hero, firstEnemy);
            if(nextPos != null){
                // 移动到新的位置
                int distance = FightFormula.calcDis(hero.x, hero.y, nextPos.left, nextPos.right);
                int moveTime = (int) (hero.moveSpeed * FightConstants.MOVE_TIME_PER_STEP * distance);
                hero.gsm.changeState(new MoveState(hero, nextPos.left, nextPos.right, moveTime));
            }else{
                // 无需移动,进入战斗状态
                DefaultAIUnit[] targets = hero.room.findTargets(hero, hero.findMethod);
                hero.gsm.changeState(new FightState(hero, targets));
            }

        }
    }


}
