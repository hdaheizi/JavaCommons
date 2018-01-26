package net.daheizi.commons.fight.pot;

import net.daheizi.commons.fight.common.FightConstants;
import net.daheizi.commons.fight.unit.DefaultAIUnit;
import net.daheizi.commons.sdata.Potential;

/**
 * 强力格挡，概率格挡掉一定比例普攻伤害
 * @author daheizi
 * @Date 2016年7月13日 下午10:21:53
 */
public class Qiangligedang extends Pot {



    /**
     * 构造函数
     * @param hero
     * @param potential
     */
    public Qiangligedang(DefaultAIUnit hero, Potential potential) {
        super(hero, potential);
    }


    /**
     * @see net.daheizi.commons.fight.pot.Pot#calcDam(int, net.daheizi.commons.fight.unit.DefaultAIUnit, net.daheizi.commons.fight.unit.DefaultAIUnit, int, java.lang.StringBuilder, java.lang.Object)
     */
    @Override
    public int calcDam(int dam, DefaultAIUnit att, DefaultAIUnit def,
            int attType, StringBuilder inner, Object source) {
        if(hero == def 
                && attType == FightConstants.ATT_TYPE_NORM 
                && dam > 0 
                && doRandom(values[0])){
            // 格挡
            dam *= (1 - values[1]);
            if(inner != null){
                inner.append("block,");
            }
        }
        return dam;
    }
}
