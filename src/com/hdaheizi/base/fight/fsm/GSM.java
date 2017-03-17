package com.hdaheizi.base.fight.fsm;

import com.hdaheizi.base.fight.event.FightEvent;

/**
 * 有限状态机
 * @author daheiz
 * @Date 2016年4月14日 上午1:35:17
 */
public class GSM {

	/** 当前状态 */
	private GameState curState;
	
	
	/**
	 * 构造函数
	 */
	public GSM(){}
	
	
	/**
	 * 当前状态
	 * @return
	 * @Date 2016年7月17日 下午1:50:30
	 */
	public GameState getCurState(){
		return curState;
	}
	
	
	/**
	 * 设置当前状态
	 * @param state
	 * @Date 2016年7月17日 下午2:00:34
	 */
	public void setCurState(GameState state){
		this.curState = state;
	}
	
	
	/**
	 * 帧更新
	 * @param dt
	 * @Date 2016年4月14日 上午1:34:12
	 */
	public void update(long dt){
		if(curState == null){
			return;
		}
		curState.update(dt);
	}
	
	
	/**
	 * 切换状态
	 * @param nextState
	 * @Date 2016年4月14日 上午1:33:39
	 */
	public void changeState(GameState nextState){
		if(null != curState){
			curState.onExit();
		}
		
		curState = nextState;
		if(curState != null){
			curState.onEnter();
		}
	}
	
	
	/**
	 * 处理事件
	 * @param event
	 * @Date 2016年4月14日 上午1:33:29
	 */
	public void handleEvent(FightEvent event){
		if(null != curState){
			curState.handleEvent(event);
		}
	}
	
}
