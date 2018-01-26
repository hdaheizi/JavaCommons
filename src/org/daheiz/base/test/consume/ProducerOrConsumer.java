package org.daheiz.base.test.consume;


/**
 * 
 * @author daheiz
 * @Date 2016年3月17日 上午2:24:09
 */
public abstract class ProducerOrConsumer implements Runnable{
     //编号
    private int x;
    // 每次生产/消费的产品数量  
    private int num;  
      
    // 所在放置的仓库  
    private Storage storage;  
        
    /**
     * 构造函数
     * @param 线程编号
     * @param 生产or消费的数量
     * @param 存放的位置
     */
    public ProducerOrConsumer(int x,int num,Storage storage){  
        this.x = x;
        this.num = num;
        this.storage = storage;
    }

    /**
     * run函数
     */
    public void run() {
        producerorconsumer(x, num);
    }

    /**
     * 生产or消费函数
     * @param 线程编号
     * @param 生产or消费数量
     */
    public abstract void producerorconsumer(int x, int num);
      
    /**
     * 获取数量
     * @return
     */
    public int getNum() {
        return num;
    }

    /**
     * 设定数量
     * @param num
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 获取仓库
     * @return
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * 设定仓库
     * @param storage
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * 获取线程编号
     * @return
     */
    public int getX() {
        return x;
    }

}
