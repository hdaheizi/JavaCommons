package top.daheizi.commons.fight.common;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.daheizi.commons.fight.room.FightRoom;

/**
 * 战斗调度器
 * @author daheizi
 * @Date 2016年3月30日 上午1:28:59
 */
public class FightSchedule {

    /** log */
    private static Logger log = LoggerFactory.getLogger(FightSchedule.class);

    /** 实例 */
    private static final FightSchedule instantce = new FightSchedule();

    /** 每帧时长 */
    private static final long interval = 1000 / FightConstants.FRAME_RATE;

    /** 执行线程 */
    private Thread thread;

    /** 战场列表 */
    private CopyOnWriteArrayList<FightRoom> roomList;

    /**
     * 构造函数
     */
    private FightSchedule() {
        roomList = new CopyOnWriteArrayList<>();
        thread = new FightScheduleThread();
        thread.start();
    }

    /**
     * 获取实例
     * @return
     * @Date 2016年3月30日 上午1:07:45
     */
    public static FightSchedule getInstance() {
        return instantce;
    }

    /**
     * 执行帧运算
     * @param realFrameTime
     * @Date 2016年3月30日 上午1:05:54
     */
    private void runFrame(long realFrameTime) {
        for(FightRoom room : roomList){
            try{
                room.update(realFrameTime);
            }catch(Exception e){
                log.error("runFrame error, roomId:{0}", e, room.getRoomId());
            }
        }
    }

    /**
     * 加入战斗
     * @param room
     * @Date 2016年3月30日 上午1:32:50
     */
    public void schedule(FightRoom room) {
        roomList.add(room);
    }

    /**
     * 移除战斗
     * @param room
     * @Date 2016年3月30日 上午1:32:47
     */
    public void unschedule(FightRoom room) {
        roomList.remove(room);
    }

    /**
     * 执行线程
     * @author daheizi
     * @Date 2016年3月30日 上午1:06:40
     */
    private class FightScheduleThread extends Thread {

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long realFrameTime = 0;
            while(!this.isInterrupted()) {
                // 一帧真正消耗的时间(也即两帧时间间隔 dt)
                realFrameTime = System.currentTimeMillis() - currentTime;
                // 每帧开始时间
                currentTime = System.currentTimeMillis();
                // 执行帧运算
                runFrame(realFrameTime);
                // 帧循环运算消耗时间
                long execTime = System.currentTimeMillis() - currentTime;
                // 计算休眠时间
                if(execTime < interval) {
                    try {
                        sleep(interval - execTime);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
        }
    }
}
