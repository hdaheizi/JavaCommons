package org.daheiz.base.fight.pot;

import org.daheiz.base.fight.unit.DefaultAIUnit;
import org.daheiz.base.sdata.Potential;

/**
 * 潜能工厂
 * @author daheiz
 * @Date 2016年7月10日 下午10:56:00
 */
public class PotFactory {


    /**
     * 创建潜能
     * @param potential
     * @return
     * @Date 2016年7月13日 下午10:07:58
     */
    public static Pot createPotential(DefaultAIUnit hero, Potential potential){
        switch(potential.getName()){
        case "qiangligedang":
            return new Qiangligedang(hero, potential);

        default:

        }

        return null;
    }
}
