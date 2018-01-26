package top.daheizi.commons.test.consume;

import java.util.LinkedList;  
import java.util.concurrent.locks.Condition;  
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;  
/** 
 * 仓库类Storage实现缓冲区 
 *  
 *  
 * @author ZHAODI
 *  
 */  
public class Storage  
{  
    // 仓库最大存储量  
    private final int MAX_SIZE = 100;  
  
    // 仓库存储的载体  
    private LinkedList<Object> list = new LinkedList<Object>();  
    
    private final Lock lock = new ReentrantLock();  
    
    // 仓库满的条件变量  
    private final Condition full = lock.newCondition();  
  
    // 仓库空的条件变量  
    private final Condition empty = lock.newCondition(); 
    
    
  
    /**
     * 生产方法
     * @param 调用该方法的线程编号
     * @param 生产数量
     */
    public void produce(int x,int num)  { 
        
        // 获得锁  
        lock.lock();
  

        try{
            while (list.size() + num > MAX_SIZE){
                // 如果仓库剩余容量不足  
                //wait
                System.out.println("生产者"+x+":\t 空间不足，\t【现存储量为】:" + list.size());
                full.await();
                System.out.println("生产者"+x+":\t 被唤醒");
                
            }
            
            // 生产条件满足情况下，生产num个产品  
            for (int i = 1; i <= num; ++i) {  
                list.add(new Object());  
            }  
      
            System.out.println("生产者"+x+":\t 生产 "+num+"个，\t【现存储量为】:" + list.size());   
      
            
        }catch(Exception e){
            e.printStackTrace();
            
        }finally{
            // 唤醒消费者   
            empty.signalAll(); 
            //unlock
            lock.unlock();
        }
    } 
    
  
    
    
    
    
    /**
     * 消费方法 
     * @param x 调用该方法的线程编号
     * @param num 消费数量
     */
    public void consume(int x,int num)  { 
        
        // 获得锁  
        lock.lock();
  

        try{
            while (list.size() < num){
                // 如果仓库剩余产品不足  
                //wait
                System.out.println("消费者"+x+":\t 产品不足，\t【现存储量为】:" + list.size());
                empty.await();
                System.out.println("消费者"+x+":\t 被唤醒");
            }
            
            // 产品足够的话，消费num个产品  
            for (int i = 1; i <= num; ++i) {  
                list.remove();  
            }  
      
            System.out.println("消费者"+x+":\t 消费 "+num+"个，\t【现存储量为】:" + list.size());   
      
            
        }catch(Exception e){
            e.printStackTrace();
            
        }finally{
            // 唤醒生产者  
            full.signalAll();  
            //unlock
            lock.unlock();
        }
    } 
    
    
    
  
    /**
     * get仓库容量
     * @return 仓库容量
     */
    public int getMAX_SIZE() {  
        return MAX_SIZE;  
    }  
  
    /**
     * get产品存放位置
     * @return list
     */
    public LinkedList<Object> getList() {  
        return list;  
    }  
  
    /**
     * 设置产品存放位置
     * @param 存放产品的list
     */
    public void setList(LinkedList<Object> list) {  
        this.list = list;  
    }  
}  
