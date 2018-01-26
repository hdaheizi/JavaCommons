package top.daheizi.commons.test;

import java.util.Timer;
import java.util.TimerTask;

public class TestTimer {
    
    public static void main(String[] args) {    
        Timer timer=new Timer();
        timer.schedule(new ExampleTask1(), 0, 2*1000);
//        timer.scheduleAtFixedRate(new ExampleTask2(), 0, 5*1000);
        Timer timer2=new Timer();
        timer2.scheduleAtFixedRate(new ExampleTask2(), 0, 1*1000);
    }

}

/**
 * 任务1执行三次取消
 * @author zhaodi
 * @Date 2015年9月7日 上午2:53:51
 */
class ExampleTask1 extends TimerTask{

    int times=3;
    @Override
    public void run() {
        System.out.println("beep1");
        times--;
        if(times<=0){
            cancel();
        }
        
    }
    
}

/**
 * 任务2第一次执行完毕后睡眠10s
 * @author zhaodi
 * @Date 2015年9月7日 上午2:54:16
 */
class ExampleTask2 extends TimerTask{

    int times=1;
    @Override
    public void run() {
        System.out.println("beep2");
        if(times>0){
            times--;
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
