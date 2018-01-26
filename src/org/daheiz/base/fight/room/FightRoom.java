package org.daheiz.base.fight.room;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.daheiz.base.fight.common.FightConstants;
import org.daheiz.base.fight.common.FightFormula;
import org.daheiz.base.fight.event.AddUnitEvent;
import org.daheiz.base.fight.event.EventAgent;
import org.daheiz.base.fight.event.EventHandler;
import org.daheiz.base.fight.event.FightEvent;
import org.daheiz.base.fight.fsm.IdleState;
import org.daheiz.base.fight.unit.DefaultAIUnit;
import org.daheiz.base.stl.Tuple;
import org.daheiz.base.util.MessageFormatter;

/**
 * 战斗房间，相当于一个战场
 * @author daheiz
 * @Date 2016年5月22日 下午7:51:06
 */
public class FightRoom extends Room implements EventHandler{

    /** 战斗单元id生成器 */
    protected int heroIdGenerator = 0;

    /** 战斗历时时长 */
    protected long roomTime;

    /** 事件代理器 */
    protected EventAgent eventAgent = new EventAgent(this);

    /** 消息列表 */
    protected List<String> msgList = new LinkedList<>();

    /** 防守方 */
    protected List<DefaultAIUnit> attList;

    /** 攻击方 */
    protected List<DefaultAIUnit> defList;

    /** 坐标矩阵 */
    protected int[][] posArray;

    /** 英雄排序器 -- 攻击长度从小到大，y轴优先级从高到低 */
    protected Comparator<DefaultAIUnit> HERO_COMPARATOR = new Comparator<DefaultAIUnit>() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(DefaultAIUnit o1, DefaultAIUnit o2) {
            if (o1.attLen == o2.attLen) {
                return o2.yPriority - o1.yPriority;
            }
            return o1.attLen - o2.attLen;
        }
    };

    /** Y的搜索方法 */
    public static final int[][] searchY = new int[][]{
        {},
        {0,  1,  2,  3,  4,  5},
        {0,  1, -1,  2,  3,  4},
        {0,  1, -1,  2, -2,  3},
        {0, -1,  1, -2,  2, -3},
        {0, -1,  1, -2, -3, -4},
        {0, -1, -2, -3, -4, -5}
    };

    /** 受击基础能量 */
    public int hittedMp;

    /** 普攻击杀获得能量 */
    public int normAttKillMp;

    /** 技能击杀获得能量 */
    public int skillKillMp;

    /**
     * 构造函数
     * @Date 2016年6月10日 下午5:08:12
     */
    public FightRoom(List<DefaultAIUnit> attList, List<DefaultAIUnit> defList) {
        super();
        this.attList = attList;
        this.defList = defList;
        posArray = new int[FightConstants.ROOM_WIDTH + 2][FightConstants.ROOM_HEIGHT + 2];
    }

    /**
     * 准备开战前的初始化工作
     * @Date 2016年7月17日 上午12:37:04
     */
    protected void init() {
        initHeroList(attList, FightConstants.FORCE_ATT);
        initHeroList(defList, FightConstants.FORCE_DEF);
        hittedMp = 1;
        normAttKillMp = 10;
        skillKillMp = 20;
    }
    
    /**
     * 初始化战斗单元
     * @param heroList
     * @param side
     * @Date 2016年7月17日 上午12:48:40
     */
    protected void initHeroList(List<DefaultAIUnit> heroList, int side) {
        Collections.sort(heroList, HERO_COMPARATOR);
        for (DefaultAIUnit hero : heroList) {
            hero.id = ++heroIdGenerator;
            hero.room = this;
            hero.side = side;
            hero.x = side == 1 ? 1 : 18;
            hero.y = 3;
            holdPos(hero.x, hero.y);
            hero.gsm.setCurState(new IdleState(hero, 0));
        }
    }
    
    /**
     * 执行帧运算
     * @param dt
     * @Date 2016年4月5日 上午2:49:10
     */
    public void update(long dt) {
        // 累计战斗时长
        roomTime += dt;
        // 处理事件
        eventAgent.update(dt);
        // 攻击方
        for (DefaultAIUnit att : attList) {
            att.update(dt);
        }
        // 防守方
        for (DefaultAIUnit def : defList) {
            def.update(dt);
        }
        // 有一方全部阵亡战斗即结束
        if (checkEnd(attList) || checkEnd(defList)) {
            int winSide = checkEnd(attList) ? FightConstants.FORCE_DEF : FightConstants.FORCE_ATT;
            endFight(winSide);
        }

        // 推送消息
        notifyAllMsgs();
    }

    /**
     * @see org.daheiz.base.fight.event.EventHandler#addEvent(org.daheiz.base.fight.event.FightEvent)
     */
    @Override
    public void addEvent(FightEvent event) {
        // 延迟处理的事件
        eventAgent.addEvent(event);
    }

    /**
     * @see org.daheiz.base.fight.event.EventHandler#handleEvent(org.daheiz.base.fight.event.FightEvent)
     */
    @Override
    public void handleEvent(FightEvent event) {
        if (event instanceof AddUnitEvent) {
            // 添加战斗单元
            StringBuilder builder = new StringBuilder("0|born|");
            AddUnitEvent _event = (AddUnitEvent) event;
            for (DefaultAIUnit hero : _event.heroList) {
                StringBuilder inner = new StringBuilder();
                addAIUnit(hero, inner);
                builder.append(inner);
            }
            notifyMsg(builder.toString());
        }
    }
    
    /**
     * 向房间推送消息
     * @param msg
     * @Date 2016年5月22日 下午6:29:44
     */
    public void notifyMsg(String msg) {
        msg += "|" + roomTime;
        msgList.add(msg);
    }

    /**
     * 开始战斗
     * @Date 2016年7月11日 下午11:32:56
     */
    public void startFight() {
        init();
        notifyMsg("0|fightStart");
        FightManager.getInstance().startFight(this);
    }

    /**
     * 结束战斗
     * @param winSide
     * @Date 2016年7月10日 下午11:40:58
     */
    public void endFight(int winSide) {
        notifyMsg(MessageFormatter.format("0|fightOver|{0}", winSide));
        FightManager.getInstance().endFight(this);
    }
    
    /**
     * 检查战斗一方是否结束
     * @param heroList
     * @return
     * @Date 2016年5月22日 下午7:53:22
     */
    protected boolean checkEnd(List<DefaultAIUnit> heroList) {
        for (DefaultAIUnit hero : heroList) {
            if (!hero.isDead()) {
                // 有一个英雄活着就未结束
                return false;
            }
        }
        return true;
    }
    
    /**
     * 每帧结束推送全部战斗消息
     * @Date 2016年5月22日 下午6:30:29
     */
    protected void notifyAllMsgs() {
        if (!msgList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String msg : msgList) {
                sb.append(msg).append(";");
            }
            String msgs = sb.substring(0, sb.length() - 1);
            msgList.clear();
            // TODO 可重定向战斗消息
            System.out.println(msgs);
        }
    }

    /**
     * 查找第一个敌人(敌方最前排的一个英雄)
     * @param hero
     * @return
     * @Date 2016年7月11日 下午11:55:40
     */
    public DefaultAIUnit findFirstEnemy(DefaultAIUnit hero) {
        DefaultAIUnit firstEnemy = null;
        List<DefaultAIUnit> heroList = getEnemys(hero);
        for (DefaultAIUnit target : heroList) {
            if (target == null || target.isDead() || target == hero) {
                // 无效
                continue;
            }
            if (firstEnemy == null) {
                // 首个目标
                firstEnemy = target;
            }else if (hero.side == FightConstants.FORCE_ATT 
                    && target.x < firstEnemy.x) {
                // 防守方x最小
                firstEnemy = target;
            }else if (hero.side == FightConstants.FORCE_DEF 
                    && target.x > firstEnemy.x) {
                // 攻击方x最大
                firstEnemy = target;
            }
        }

        return firstEnemy;
    }

    /**
     * 按类型查找目标
     * @param findMethod
     * @return
     * @Date 2016年6月7日 上午12:21:32
     */
    public DefaultAIUnit[] findTargets(DefaultAIUnit hero, int findMethod, Object... params) {
        switch(findMethod) {
        case FightConstants.FIND_METHOD_1:
        {
            List<DefaultAIUnit> heroList = getEnemys(hero);
            DefaultAIUnit[] heros = new DefaultAIUnit[heroList.size()];
            int i = 0;
            for (DefaultAIUnit each : heroList) {
                if (!each.isDead()) {
                    heros[i++] = each;
                }
            }
            return heros;
        }

        case FightConstants.FIND_METHOD_2:
        {
            List<DefaultAIUnit> heroList = getTeammates(hero);
            DefaultAIUnit[] heros = new DefaultAIUnit[heroList.size()];
            int i = 0;
            for (DefaultAIUnit each : heroList) {
                if (!each.isDead()) {
                    heros[i++] = each;
                }
            }
            return heros;
        }

        case FightConstants.FIND_METHOD_3:
        {
            DefaultAIUnit[] heros = new DefaultAIUnit[attList.size() + defList.size()];
            int i = 0;
            for (DefaultAIUnit each : attList) {
                if (!each.isDead()) {
                    heros[i++] = each;
                }
            }
            for (DefaultAIUnit each : defList) {
                if (!each.isDead()) {
                    heros[i++] = each;
                }
            }
            return heros;
        }

        case FightConstants.FIND_METHOD_4:
        {
            // 目标x
            int targetX = 0;
            DefaultAIUnit[] projections = new DefaultAIUnit[FightConstants.ROOM_HEIGHT + 2];
            // 先找出每列最前排，向y轴投影
            if (hero.side == FightConstants.FORCE_ATT) {
                targetX = FightConstants.ROOM_WIDTH;
                for (DefaultAIUnit each : defList) {
                    if (!each.isDead() && (projections[each.y] == null || each.x < projections[each.y].x)) {
                        // 守方x最小
                        projections[each.y] = each;
                        targetX = Math.min(targetX, each.x);
                    }
                }
            }else{
                for (DefaultAIUnit each : attList) {
                    if (!each.isDead() && (projections[each.y] == null || each.x > projections[each.y].x)) {
                        // 攻方x最大
                        projections[each.y] = each;
                        targetX = Math.max(targetX, each.x);
                    }
                }
            }
            // 选出最终目标
            DefaultAIUnit[] heros = new DefaultAIUnit[projections.length];
            int i = 0;
            for (DefaultAIUnit each : projections) {
                if (each != null && each.x == targetX) {
                    heros[i++] = each;
                }
            }
            return heros;
        }

        case FightConstants.FIND_METHOD_5:
        {
            // 目标x
            int targetX = 0;
            DefaultAIUnit[] projections = new DefaultAIUnit[FightConstants.ROOM_HEIGHT + 2];
            // 先找出每列最后排，向y轴投影
            if (hero.side == FightConstants.FORCE_DEF) {
                targetX = FightConstants.ROOM_WIDTH;
                for (DefaultAIUnit each : attList) {
                    if (!each.isDead() && (projections[each.y] == null || each.x < projections[each.y].x)) {
                        // 攻方x最小
                        projections[each.y] = each;
                        targetX = Math.min(targetX, each.x);
                    }
                }
            }else{
                for (DefaultAIUnit each : defList) {
                    if (!each.isDead() && (projections[each.y] == null || each.x > projections[each.y].x)) {
                        // 守方x最大
                        projections[each.y] = each;
                        targetX = Math.max(targetX, each.x);
                    }
                }
            }
            // 选出最终目标
            DefaultAIUnit[] heros = new DefaultAIUnit[projections.length];
            int i = 0;
            for (DefaultAIUnit each : projections) {
                if (each != null && each.x == targetX) {
                    heros[i++] = each;
                }
            }
            return heros;
        }
        
        default:

        }
        
        return new DefaultAIUnit[0];
    }

    /**
     * 获取友方英雄列表
     * @param hero
     * @return
     * @Date 2016年6月13日 上午12:27:06
     */
    protected List<DefaultAIUnit> getTeammates(DefaultAIUnit hero) {
        return hero.side == FightConstants.FORCE_ATT ? attList : defList;
    }

    /**
     * 获取敌方英雄列表
     * @param hero
     * @return
     * @Date 2016年6月13日 上午12:27:04
     */
    protected List<DefaultAIUnit> getEnemys(DefaultAIUnit hero) {
        return hero.side == FightConstants.FORCE_DEF ? attList : defList;
    }

    /**
     * 占用位置
     * @param x
     * @param y
     * @Date 2016年6月10日 下午7:45:53
     */
    public void holdPos(int x, int y) {
        posArray[x][y]++;
    }

    /**
     * 释放位置
     * @param x
     * @param y
     * @Date 2016年6月10日 下午7:45:50
     */
    public void releasePos(int x, int y) {
        posArray[x][y]--;
    }

    /**
     * 位置是否空闲
     * @param x
     * @param y
     * @return
     * @Date 2016年6月10日 下午7:45:46
     */
    public boolean hasPos(int x, int y) {
        if (x >= 1 && x <= FightConstants.ROOM_WIDTH 
                && y >= 1 && y <= FightConstants.ROOM_HEIGHT) {
            return posArray[x][y] <= 0;
        }
        return false;
    }

    /**
     * 寻找攻击位置
     * @param hero
     * @param target
     * @return
     * @Date 2016年6月14日 上午12:43:35
     */
    public Tuple<Integer, Integer> findAttPos(DefaultAIUnit hero, DefaultAIUnit target) {
        // 最近距离
        int minLen = Math.min(hero.attLen, target.attLen); 
        // 最远距离
        int maxLen = Math.max(FightFormula.calcLen(hero.x, target.x) * 2 / 3, hero.attLen);
        // 攻防符号位
        int sign = hero.side == FightConstants.FORCE_ATT ? 1 : -1;
        // 当前距离
        int dx = (target.x - hero.x) * sign;

        if (dx < minLen) {
            // 距离太近
            for (int len = minLen; len <= maxLen; len++) {
                // 由近及远寻找合适位置
                int toX = target.x - len * sign;
                for (int dy : searchY[hero.y]) {
                    if (hasPos(toX, hero.y + dy)) {
                        return new Tuple<>(toX, hero.y + dy);
                    }
                }
            }
            return null;    
        } else if (dx > maxLen) {
            // 距离太远
            for (int len = maxLen; len >= minLen; len--) {
                // 由远及近寻找合适位置
                int toX = target.x - len * sign;
                for (int dy : searchY[hero.y]) {
                    if (hasPos(toX, hero.y + dy)) {
                        return new Tuple<>(toX, hero.y + dy);
                    }
                }
            }
            return null;
        } else {
            // 已经处在攻击范围内，尝试向中间靠拢
            int midY = (FightConstants.ROOM_HEIGHT + 1) / 2;
            int toY = hero.y + searchY[hero.y][1];
            if (hero.y != midY && hasPos(hero.x, toY)) {
                for (DefaultAIUnit friend : getTeammates(hero)) {
                    if (!friend.isDead() 
                            && friend.y == toY 
                            && sign * (friend.x - hero.x) > 0) {
                        // 内侧前排有队友，则不靠拢
                        return null;
                    }
                }
                // 向中间靠拢
                return new Tuple<>(hero.x, toY);
            }
            return null;
        }
    }

    /**
     * 添加战斗单元
     * @param hero
     * @param inner
     * @Date 2016年7月17日 上午4:29:44
     */
    protected void addAIUnit(DefaultAIUnit hero, StringBuilder inner) {
        hero.id = ++heroIdGenerator;
        hero.room = this;
        holdPos(hero.x, hero.y);
        hero.gsm.setCurState(new IdleState(hero, 0));
        List<DefaultAIUnit> heroList = getTeammates(hero);
        heroList.add(hero);
        Collections.sort(heroList, HERO_COMPARATOR);
        if (null != inner) {
            inner.append(MessageFormatter.format("{0}|{1}|{2},{3},{4},{5}", 
                    hero.id, hero.memberId, hero.x, hero.y, hero.maxHp, hero.maxMp));
        }
    }

}
