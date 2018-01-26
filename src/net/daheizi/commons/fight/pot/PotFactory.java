package net.daheizi.commons.fight.pot;

import net.daheizi.commons.fight.unit.DefaultAIUnit;
import net.daheizi.commons.sdata.Potential;

/**
 * 潜能工厂
 * @author daheizi
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
