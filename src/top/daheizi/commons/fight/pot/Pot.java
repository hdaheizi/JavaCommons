package top.daheizi.commons.fight.pot;

import top.daheizi.commons.fight.event.FightEvent;
import top.daheizi.commons.fight.unit.DefaultAIUnit;
import top.daheizi.commons.sdata.Potential;
import top.daheizi.commons.util.RandomUtil;

/**
 * 潜能
 * @author daheizi
 * @Date 2016年7月10日 下午10:49:54
 */
public abstract class Pot {

    /** 拥有者 */
    protected DefaultAIUnit hero;

    /** 潜能Id */
    protected int potId;

    /** 相关参数 */
    protected double[] values;


    /**
     * 构造函数
     * @param hero
     * @param potential
     */
    public Pot(DefaultAIUnit hero, Potential potential){
        this.hero = hero;
        this.potId = potential.getId();
        String[] params = potential.getParamStr().split(",");
        values = new double[params.length];
        for(int i = 0; i < params.length; i++){
            values[i] = Double.valueOf(params[i]);
        }
    }


    /**
     * 计算概率事件是否触发
     * @param prob
     * @return
     * @Date 2016年7月13日 下午10:29:58
     */
    protected static boolean doRandom(double prob){
        return RandomUtil.nextDouble() < prob;
    }


    /**
     * 处理事件
     * @param event
     * @Date 2016年7月10日 下午10:51:03
     */
    public void handleEvent(FightEvent event){
    }


    /**
     * 是否命中
     * @param att 攻击方
     * @param def 防守方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年8月28日 上午12:03:13
     */
    public boolean succAttack(DefaultAIUnit att, DefaultAIUnit def,
            int attType, StringBuilder inner, Object source) {
        return true;
    }


    /**
     * 计算伤害
     * @param dam 伤害大小
     * @param att 攻击方
     * @param def 防守方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return 计算结果
     * @Date 2016年8月28日 上午12:05:12
     */
    public int calcDam(int dam, DefaultAIUnit att, DefaultAIUnit def,
            int attType, StringBuilder inner, Object source) {
        return dam;
    }
}
