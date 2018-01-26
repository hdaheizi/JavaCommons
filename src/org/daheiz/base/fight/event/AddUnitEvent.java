package org.daheiz.base.fight.event;

import java.util.ArrayList;
import java.util.List;

import org.daheiz.base.fight.unit.DefaultAIUnit;

/**
 * 添加战斗单元事件
 * @author daheiz
 * @Date 2016年7月17日 上午12:51:09
 */
public class AddUnitEvent extends FightEvent {

    /** 要添加的战斗单元列表 */
    public List<DefaultAIUnit> heroList = new ArrayList<>();;


}
