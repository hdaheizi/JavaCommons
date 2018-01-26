package net.daheizi.commons.fight.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.daheizi.commons.fight.common.FightConstants;
import net.daheizi.commons.fight.common.FightFormula;
import net.daheizi.commons.fight.event.BuffEvent;
import net.daheizi.commons.fight.event.EventAgent;
import net.daheizi.commons.fight.event.EventHandler;
import net.daheizi.commons.fight.event.FightEvent;
import net.daheizi.commons.fight.event.SkillEvent;
import net.daheizi.commons.fight.fsm.GSM;
import net.daheizi.commons.fight.fsm.SkillState;
import net.daheizi.commons.fight.fsm.buff.Buff;
import net.daheizi.commons.fight.fsm.buff.BuffFactory;
import net.daheizi.commons.fight.pot.Pot;
import net.daheizi.commons.fight.room.FightRoom;
import net.daheizi.commons.fight.skill.FightSkill;
import net.daheizi.commons.fight.skill.SkillAttribute;
import net.daheizi.commons.fight.skill.SkillFactory;
import net.daheizi.commons.stl.Tuple;
import net.daheizi.commons.util.MessageFormatter;
import net.daheizi.commons.util.RandomUtil;


/**
 * 战斗实体的抽象类
 * @author daheizi
 * @Date 2016年4月5日 上午2:43:23
 */
public abstract class DefaultAIUnit implements EventHandler {

    /** 战斗中id */
    public int id;

    /** 成员静态id */
    public int memberId;

    /** 势力 攻击方/防守方 */
    public int side;

    /** 所处房间 */
    public FightRoom room;

    /** 状态机 */
    public GSM gsm = new GSM();

    /** 事件代理器 */
    protected EventAgent eventAgent = new EventAgent(this);

    /** buff列表 */
    protected List<Buff> buffList = new LinkedList<>();

    /** 攻击效果列表 */
    public List<Tuple<String, Integer>> effectList = new ArrayList<>();

    /** 攻击效果参数map */
    public Map<String, double[]> paramMap = new HashMap<>();

    /** 攻击属性map */
    public Map<String, Double> attributeMap = new HashMap<>();

    /** 技能map */
    public Map<Integer, SkillAttribute> skillMap = new HashMap<>();

    /** 潜能列表 */
    public Pot[] pots = new Pot[0];

    /** 坐标x */
    public int x;
    /** 坐标y */
    public int y;
    /** 攻击方法 */
    public int attMethod;
    /** 查找方法 */
    public int findMethod;

    /** 血量上限 */
    public int maxHp;    
    /** 魔法上限 */
    public int maxMp;    
    /** 血量 */
    public int hp;
    /** 怒气 */
    public int mp;

    /** 物理攻击 */
    public int att;
    /** 物理防御 */
    public int def;
    /** 魔法攻击 */
    public int matt;
    /** 魔法防御 */
    public int mdef;
    /** 暴击等级 */
    public int crip;
    /** 命中等级 */
    public int hit;
    /** 闪避等级 */
    public int dodge;

    /** 怒气转化率 */
    public float angerConv;

    /** 暴击比 */
    public double critRatio;
    /** 暴击伤害 */
    public int critDam;

    /** 攻击长度 */
    public int attLen;
    /** y轴上的优先级 */
    public int yPriority;
    /** 攻击间隔cd */
    public int attCd;
    /** 移动速度 */
    public float moveSpeed = 1f;
    /** 攻击速度 */
    public float attSpeed = 1f;

    /** 是否自动释放技能 */
    public boolean autoPlaySkill;
    /** 隐身计数器 */
    public int invisibleCounter = 0;
    /** 睡眠计数器 */
    public int sleepCounter = 0;



    /**
     * 执行帧运算
     * @param dt
     * @Date 2016年4月5日 上午2:49:10
     */
    public void update(long dt) {
        if(isDead()){
            // 已经死亡
            return;
        }

        // 更新事件
        eventAgent.update(dt);

        // 更新buff
        Buff[] buffArray = buffList.toArray(new Buff[0]);
        for(Buff buff : buffArray){
            buff.update(dt);
        }

        // 主动更新
        if(!isSleep()){
            // 计算自己的时钟
            dt = attSpeed > 0 ? (long) (dt * attSpeed) : 0;
            activeUpdate(dt);
        }
    }


    /**
     * 主动更新
     * @param dt
     * @Date 2016年7月17日 上午2:25:45
     */
    protected void activeUpdate(long dt){
        // 状态机更新
        gsm.update(dt);
        // 释放技能
        if(autoPlaySkill){
            for(SkillAttribute skill: skillMap.values()){
                if(canPlaySkill(skill)){
                    addEvent(new SkillEvent(skill));
                    break;
                }
            }
        }
    }



    /**
     * @see net.daheizi.commons.fight.event.EventHandler#addEvent(net.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void addEvent(FightEvent event){
        eventAgent.addEvent(event);
    }


    /**
     * @see net.daheizi.commons.fight.event.EventHandler#handleEvent(net.daheizi.commons.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event) {        
        // buff处理
        for(Buff buff : buffList.toArray(new Buff[0])){
            buff.handleEvent(event);
            if(event.isExpire){
                return;
            }
        }

        // 触发潜能
        for(Pot pot : pots){
            pot.handleEvent(event);
            if(event.isExpire){
                return;
            }
        }

        // 状态机处理
        gsm.handleEvent(event);
        if(event.isExpire){
            return;
        }
        // 技能事件
        if(event instanceof SkillEvent){
            SkillEvent _event = (SkillEvent) event;
            if(canPlaySkill(_event.skill)){
                FightSkill skill = SkillFactory.createSkill(this, _event.skill);
                DefaultAIUnit[] targets = skill.findTargets("prep");
                if(hasAttTargets(FightConstants.ATT_TYPE_SKILL, targets)){
                    // 重置怒气
                    reduceMp(this.mp); 
                    // 进入技能状态
                    gsm.changeState(new SkillState(this, skill, gsm.getCurState()));
                }
            }
            return;
        }else if(event instanceof BuffEvent){
            // buff事件
            BuffEvent _event = (BuffEvent) event;
            Buff buff = BuffFactory.createBuff(_event.buffName, this, _event.skill, _event.params);
            addBuff(buff);
        }
    }


    /**
     * 处理受击
     * @param dam 伤害值
     * @param attType 攻击类型
     * @param att 攻击方
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年8月28日 上午2:24:45
     */
    public int handleHitted(int dam, int attType, DefaultAIUnit att, StringBuilder inner, Object source){
        // 命中判定
        if(!att.succAttack(this, attType, inner, source)){
            return -1;
        }

        // 闪避处理
        if(attType == FightConstants.ATT_TYPE_NORM 
                && !isSleep()
                && RandomUtil.nextDouble() < FightFormula.calcDodgeRatio(this.dodge, att.hit)){
            // 闪避了
            if(null != inner){
                inner.append("dodge,");
            }
            return -1;
        }

        // 暴击处理
        if(attType == FightConstants.ATT_TYPE_NORM){
            if(RandomUtil.nextDouble() < FightFormula.calcCritRatio(att.crip)){
                // 暴击了
                dam = (int) (dam * att.critRatio) + att.critDam;
                if(null != inner){
                    inner.append("crit,");
                }
            }
        }

        // 攻击方计算伤害加成
        att.calcRealDam(dam, att, this, attType, inner, source);

        // 造成伤害
        dam = handleDam(dam, attType, att, inner, source);

        return dam;
    }


    /**
     * 造成伤害
     * @param dam 伤害值
     * @param attType 攻击类型
     * @param att 攻击方
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年6月6日 上午2:05:55
     */
    public int handleDam(int dam, int attType, DefaultAIUnit att, StringBuilder inner, Object source){
        // 受击方计算伤害
        dam = this.calcRealDam(dam, att, this, attType, inner, source);

        // 造成伤害
        dam = reduceHp(dam, inner);

        // 处理怒气
        handleHittedMp(dam, att, attType);

        return dam;
    }


    /**
     * 处理受击后的怒气
     * @param dam
     * @param att
     * @param attType
     * @Date 2016年6月16日 下午9:07:44
     */
    protected void handleHittedMp(int dam, DefaultAIUnit att, int attType){
        if(!isDead()){
            // 怒气
            addMp((int) (room.hittedMp + angerConv * dam * maxMp / maxHp));
        }else if(attType == FightConstants.ATT_TYPE_NORM){
            // 普攻击杀
            att.addMp(room.normAttKillMp);
        }else if(attType == FightConstants.ATT_TYPE_SKILL){
            // 技能击杀
            att.addMp(room.skillKillMp);
        }
    }


    /**
     * 添加buff
     * @param buff
     * @Date 2016年4月14日 上午1:33:14
     */
    public void addBuff(Buff buff){
        // 判断是否被其它buff融合
        Buff[] buffArray = buffList.toArray(new Buff[0]);
        for(Buff each : buffArray){    
            if(each.mixBuff(buff)){
                return;
            }
        }
        // 根据优先级从高到低查找位置
        int index = 0;
        for(Buff each : buffList){
            if(buff.getPriority() > each.getPriority()){
                break;
            }
            index++;
        }    
        // 加入buff
        if(index < buffList.size()){
            buffList.add(index, buff);
        }else{
            buffList.add(buff);
        }
        // 进入状态
        buff.onEnter();
    }


    /**
     * 是否存在指定buff
     * @param name
     * @return
     * @Date 2016年7月10日 下午3:46:32
     */
    public boolean hasBuff(String name){
        for(Buff buff : buffList){
            if(buff.getName().equals(name)){
                return true;
            }
        }
        return false;
    }


    /**
     * 移除指定buff
     * @param buff
     * @Date 2016年5月13日 下午11:51:08
     */
    public void removeBuff(Buff _buff){
        Iterator<Buff> it = buffList.iterator();
        while(it.hasNext()){
            Buff buff = it.next();
            if(buff == _buff){
                buff.onExit();
                it.remove();
            }
        }
    }


    /**
     * 按名称移除buff
     * @param name
     * @Date 2016年4月14日 上午1:37:01
     */
    public void removeBuff(String name){
        Iterator<Buff> it = buffList.iterator();
        while(it.hasNext()){
            Buff buff = it.next();
            if(buff.getName().equals(name)){
                buff.onExit();
                it.remove();
            }
        }
    }


    /**
     * 按类型移除buff
     * @param type
     * @Date 2016年5月14日 上午12:13:35
     */
    public void removeBuff(int type){
        Iterator<Buff> it = buffList.iterator();
        while(it.hasNext()){
            Buff buff = it.next();
            if(buff.getType() == type){
                buff.onExit();
                it.remove();
            }
        }
    }


    /**
     * 减少血量
     * @param dam
     * @Date 2016年5月22日 下午11:59:17
     */
    protected int reduceHp(int hpDown, StringBuilder inner){
        int _hpDown = hp < hpDown ? hp : hpDown;
        hp -= _hpDown;
        if(isDead()){
            // 处理死亡
            handleDead();
            if(null != inner){
                inner.append("die,");
            }
        }
        return _hpDown;
    }


    /**
     * 恢复血量
     * @param hpUp
     * @return
     * @Date 2016年5月24日 上午12:27:18
     */
    protected int addHp(int hpUp){
        int _hpUp = hp + hpUp > maxHp ? maxHp - hp : hpUp;
        hp +=  _hpUp;
        return _hpUp;
    }


    /**
     * 减少怒气
     * @param mpDown
     * @return
     * @Date 2016年5月24日 上午2:13:48
     */
    protected int reduceMp(int mpDown){
        int _mpDown = mp < mpDown ? mp : mpDown;
        mp -= _mpDown;
        if(_mpDown > 0){
            room.notifyMsg(MessageFormatter.format("{0}|anger|{1}", id, mp));
        }
        return _mpDown;
    }


    /**
     * 恢复能量
     * @param mpUp
     * @return
     * @Date 2016年7月21日 上午12:25:26
     */
    protected int addMp(int mpUp){
        int _mpUp = mp + mpUp > maxMp ? maxMp - mp : mpUp;
        mp += _mpUp;
        if(_mpUp > 0){
            room.notifyMsg(MessageFormatter.format("{0}|anger|{1}", id, mp));
        }
        return _mpUp;
    }


    /**
     * 移动到指定位置
     * @param toX
     * @param toY
     * @Date 2016年6月14日 下午9:52:33
     */
    public void moveTo(int toX, int toY){
        room.releasePos(x, y);
        room.holdPos(toX, toY);
        this.x = toX;
        this.y = toY;
    }


    /**
     * 是否可以释放技能
     * @param skill
     * @return
     * @Date 2016年7月10日 下午6:44:01
     */
    public boolean canPlaySkill(SkillAttribute skill){
        return skill != null 
                && hp > 0
                && mp >= maxMp
                && !isSleep()
                && !(gsm.getCurState() instanceof SkillState);
    }


    /**
     * 是否存在有效攻击目标
     * @param attType
     * @param targets
     * @return
     * @Date 2016年7月13日 上午12:36:38
     */
    public boolean hasAttTargets(int attType, DefaultAIUnit[] targets){
        if(null != targets){
            for(DefaultAIUnit target : targets){
                if(canAtt(target, attType)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 是否可以攻击
     * @param target
     * @param 攻击方式
     * @return
     * @Date 2016年7月13日 上午12:35:12
     */
    public boolean canAtt(DefaultAIUnit target, int attType){
        if(target == null || target.isDead() || !isVisible()){
            return false;
        }
        return true;
    }


    /**
     * 处理死亡
     * @Date 2016年5月22日 下午10:37:46
     */
    public void handleDead(){
        // 清除所有buff
        Buff[] buffArray = buffList.toArray(new Buff[0]);
        for(Buff buff : buffArray){
            buff.onExit();
        }
        buffList.clear();

        // 退出状态机
        gsm.changeState(null);
        // 释放位置
        room.releasePos(x, y);    
    }



    /**
     * 是否死亡
     * @return
     * @Date 2016年7月13日 下午9:49:35
     */
    public boolean isDead() {
        return hp <= 0;
    }


    /**
     * 是否睡眠
     * @return
     * @Date 2016年7月17日 下午3:36:12
     */
    public boolean isSleep() {
        return sleepCounter > 0;
    }


    /**
     * 睡眠
     * @Date 2016年9月13日 上午1:09:12
     */
    public void sleep(){
        sleepCounter++;
    }


    /**
     * 唤醒
     * @Date 2016年9月13日 上午1:09:06
     */
    public void wakeUp(){
        if(sleepCounter > 0){
            sleepCounter--;
        }
    }


    /**
     * 是否可见
     * @return
     * @Date 2016年7月21日 上午12:00:54
     */
    public boolean isVisible() {
        return invisibleCounter <= 0;
    }


    /**
     * 是否命中
     * @param target 受击方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年8月28日 上午12:03:13
     */
    public boolean succAttack(DefaultAIUnit target, int attType, StringBuilder inner, Object source) {
        // 攻击方buff判定
        for(Buff buff : buffList.toArray(new Buff[0])){
            if(!buff.succAttack(this, target, attType, inner, source)){
                return false;
            }
        }

        // 受击方buff判定
        for(Buff buff : target.buffList.toArray(new Buff[0])){
            if(!buff.succAttack(this, target, attType, inner, source)){
                return false;
            }
        }

        // 受击方潜能判定
        for(Pot pot : pots){
            if(!pot.succAttack(this, target, attType, inner, source)){
                return false;
            }
        }
        return true;
    }


    /**
     * 计算真实伤害
     * @param dam 伤害大小
     * @param att 攻击方
     * @param def 受击方
     * @param attType 攻击方式
     * @param inner 消息接收器
     * @param source 伤害来源
     * @return
     * @Date 2016年8月28日 上午12:05:12
     */
    public int calcRealDam(int dam, DefaultAIUnit att, DefaultAIUnit def, int attType, StringBuilder inner, Object source){
        // buff计算
        for(Buff buff : buffList.toArray(new Buff[0])){
            dam = buff.calcDam(dam, att, def, attType, inner, source);
        }
        // 潜能计算
        for(Pot pot : pots){
            dam = pot.calcDam(dam, att, def, attType, inner, source);
        }
        return dam;
    }
}
