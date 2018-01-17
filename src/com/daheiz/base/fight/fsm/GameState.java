package com.daheiz.base.fight.fsm;

import com.daheiz.base.fight.event.FightEvent;

/**
 * 状态类接口
 * @author daheiz
 * @Date 2016年5月22日 下午5:17:04
 */
public interface GameState {

	/**
	 * 获取状态名称
	 * @return
	 * @Date 2016年5月22日 下午5:17:20
	 */
	String getName();

	/**
	 * 进入状态时
	 * @Date 2016年5月22日 下午5:17:23
	 */
	void onEnter();

	/**
	 * 退出状态时
	 * @Date 2016年5月22日 下午5:17:26
	 */
	void onExit();

	/**
	 * 更新状态
	 * @param dt
	 * @Date 2016年5月22日 下午5:17:28
	 */
	void update(long dt);

	/**
	 * 处理事件
	 * @param event
	 * @Date 2016年5月22日 下午5:17:31
	 */
	void handleEvent(FightEvent event);

}
