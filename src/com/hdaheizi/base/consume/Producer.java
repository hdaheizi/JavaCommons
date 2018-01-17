package com.hdaheizi.base.consume;

/** 
 * 生产者类Producer继承线程类ProducerOrConsumer
 *  
 *  
 * @author ZHAODi
 *  
 */  
public class Producer extends ProducerOrConsumer {  
	
	  
	/**
	 * 调用父类构造函数
	 * @param 线程编号
	 * @param 生产数量
	 * @param 存放位置
	 */
    public Producer(int x, int num, Storage storage) {
		super(x, num, storage);
	}

	/**
	 * 调用仓库的生产函数
	 * @param x 编号
	 * @param num 数量
	 */
    public void producerorconsumer(int x,int num)  {  
        getStorage().produce(x,num); 
    }
}