package org.daheizi.commons.test;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.daheizi.commons.test.consume.Consumer;
import org.daheizi.commons.test.consume.Producer;
import org.daheizi.commons.test.consume.ProducerOrConsumer;
import org.daheizi.commons.test.consume.Storage;

public class TestConsume {
    public static void main(String[] args) {
        // 仓库对象
        Storage storage = new Storage();

        // 消费者and生产者对象
        Consumer c1 = new Consumer(1, 1, storage);//每次消费1个
        Consumer c2 = new Consumer(2, 50, storage);//每次消费50个
        Producer p3 = new Producer(3, 6, storage);//每次生产6个
        Producer p4 = new Producer(4, 10, storage);//每次生产10个


        // 启动定时消费者线程1and2
        executeFixedRate(c1,0,1,100);//  第0s首次启动，每1s启动一次，第100s终止  
        executeFixedRate(c2,50,5,100);//第50s首次启动，每5s启动一次，第100s终止  

        // 启动定时生产者线程3and4
        executeFixedRate(p3,0,10,50);// 第0s首次启动，每10s启动一次，第50s终止  
        executeFixedRate(p4,30,1,80);//第30s首次启动，每1s启动一次，第80s终止  

    }

    /**
     * 以固定周期启动线程
     * @param porc 线程
     * @param firsttime 首次启动时间
     * @param delay 两次启动时间间隔
     * @param endtime  终止时间
     */
    public static void executeFixedRate(ProducerOrConsumer porc,long firsttime,long delay,long endtime) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        final ScheduledFuture<?> futuretimer = executor.scheduleWithFixedDelay(porc, firsttime, delay, TimeUnit.SECONDS);
        executor.schedule(new Runnable() {
            public void run() {
                futuretimer.cancel(true);
            }
        }, endtime, TimeUnit.SECONDS);

    }

}
