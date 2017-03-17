package com.hdaheizi.base.consume;

/**
 * 消费者
 * @author daheiz
 * @Date 2016年3月17日 上午2:22:48
 */
public class Consumer extends ProducerOrConsumer {  
  /**
   * 构造函数
   * @param x 线程编号
   * @param num 消费数量
   * @param storage 所在仓库
   */
  public Consumer(int x, int num, Storage storage) {
	  super(x, num, storage);
	}

	/**
	 * 调用仓库Storage的消费函数  
	 * @param x 编号
	 * @param num 数量
	 */
    public void producerorconsumer(int x,int num) {  
        getStorage().consume(x,num);  
        
    }

}