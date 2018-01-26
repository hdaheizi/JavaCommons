package net.daheizi.commons.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.daheizi.commons.util.MathUtil;
import net.daheizi.commons.util.MessageFormatter;

import com.reign.framework.exception.IlleageArgumentException;

/**
 * 微信红包模拟测试
 * @author daheizi
 * @Date 2017年2月27日 下午3:33:18
 */
public class TestHongbao {
    
    public static void main(String[] args) {
        TestHongbao test = new TestHongbao();
        HongbaoManager manager = test.new HongbaoManager();
        manager.init(5, 100, true);
        manager.run(10000);
        manager.report();
    }
    
    
    
    
    /**
     * 红包
     * @author daheizi
     * @Date 2017年2月27日 下午3:33:35
     */
    class Hongbao {
        
        /** 剩余钱数 */
        int rest;
        
        /** 份数 */
        int pieces;
        
        /** 已打开的红包个数 */
        int index;
        
        /** 每个红包最小数目 */
        static final int min = 1;
        
        /** 红包记录 */
        int[] records;
        
        /** 手气最差的索引 */
        int minIndex;
        
        /** 手气最佳的索引 */
        int maxIndex;
        
        /** 随机种子 */
        final Random random;
        
        
        /**
         * 构造函数
         * @param total
         * @param pieces
         */
        Hongbao(int total, int pieces) {
            if(total < pieces * min){
                throw new IlleageArgumentException("The Hongbao is too small!");
            }
            this.random = new Random();
            this.rest = total;
            this.pieces = pieces;
            this.index = 0;
            this.records = new int[pieces];
        }
        
        /**
         * 是否结束
         * @return
         * @Date 2017年2月27日 下午3:34:09
         */
        boolean isEnd(){
            return index >= pieces || rest <= 0;
        }
        
        
        /**
         * 开红包
         * @return
         * @Date 2017年2月27日 下午3:33:59
         */
        int lottery(){
            if(isEnd()){
                return 0;
            }
            int result = 0;
            if(index == pieces - 1){
                result = rest;
            }else{
                int max = Math.min(rest / (pieces - index) * 2 - min, rest - (pieces - index - 1) * min);
                result = min + random.nextInt(max - min + 1);
            }
            records[index] = result;
            if(result < records[minIndex]){
                minIndex = index;
            }
            if(result > records[maxIndex]){
                maxIndex = index;
            }
            rest -= result;
            index++;
            
            return result;
        }
        
        /**
         * 手气最佳
         * @return
         * @Date 2017年2月27日 下午3:34:38
         */
        int getMinIndex(){
            return minIndex;
        }
        
        /**
         * 获取最佳钱数
         * @return
         * @Date 2017年2月27日 下午5:08:59
         */
        int getMinScore(){
            return records[minIndex];
        }
        
        /**
         * 获取最小钱数
         * @return
         * @Date 2017年2月27日 下午5:09:19
         */
        int getMaxScore(){
            return records[maxIndex];
        }
        
        /**
         * 手气最差
         * @return
         * @Date 2017年2月27日 下午3:34:41
         */
        int getMaxIndex(){
            return maxIndex;
        }
        
        /**
         * 打印红包结果
         * @Date 2017年2月27日 下午3:34:20
         */
        void report(){
            System.out.println(MessageFormatter.format("records:{0}; minNo:{1}; maxNo:{2}",
                    Arrays.toString(records), minIndex + 1, maxIndex + 1));
        }
        
    }
    
    
    
    /**
     * 红包玩家
     * @author daheizi
     * @Date 2017年2月27日 下午3:35:00
     */
    class HongbaoPlayer {
        
        /** 编号 */
        int no;
        
        /** 剩余钱数 */
        int rest;
        
        /** 手气最差次数 */
        int minTimes;
        
        /** 手气最佳次数 */
        int maxTimes;
        
        /** 历史记录 */
        List<Integer> resultList;
        
        
        /**
         * 构造函数
         * @param no
         */
        HongbaoPlayer(int no){
            this.no = no;
            this.resultList = new ArrayList<>();
        }
        
        
        /**
         * 打印玩家结果
         * @Date 2017年2月27日 下午3:35:21
         */
        void report(){
            int totalTimes = resultList.size();
            double sum = 0;
            for(int result : resultList){
                sum += result;
            }
            double X = MathUtil.demical(sum / totalTimes, 2);
            
            double sumS2 = 0;
            for(int result : resultList){
                sumS2 += Math.pow(result - X, 2);
            }
            double S2 = MathUtil.demical(sumS2 / totalTimes, 2);
            
            double minRate = MathUtil.demical((double)minTimes / totalTimes, 4);
            double maxRate = MathUtil.demical((double)maxTimes / totalTimes, 4);
            String format = "Player {0}: [rest:{1}, minRate:{2}, maxRate:{3}, X:{4}, S2:{5}]";
            System.out.println(MessageFormatter.format(format, no, rest, minRate, maxRate, X, S2));
        }
    }
    
    
    /**
     * 红包管理器
     * @author daheizi
     * @Date 2017年2月27日 下午3:35:36
     */
    class HongbaoManager {
        
        /** 玩家列表 */
        HongbaoPlayer[] players;
        
        /** 红包大小 */
        int hongbaoSize;
        
        /** 是否手气最佳发红包 */
        boolean luckyTurnNext;
        
        /** 总次数 */
        int totalTimes;
        
        Map<Integer, Integer> minScoreTimesMap = new TreeMap<>();
        
        /**
         * 初始化
         * @param playersNum    玩家数目
         * @param hongbaoSize    红包大小
         * @param luckyTurnNext    是否最佳发红包
         * @Date 2017年2月27日 下午3:35:48
         */
        void init(int playersNum, int hongbaoSize, boolean luckyTurnNext){
            this.hongbaoSize = hongbaoSize;
            this.luckyTurnNext = luckyTurnNext;
            this.players = new HongbaoPlayer[playersNum];
            for(int i = 0; i < players.length; ++i){
                players[i] = new HongbaoPlayer(i + 1);
            }
        }
        
        
        /**
         * 模拟抢红包
         * @param times 模拟次数
         * @Date 2017年2月27日 下午3:36:37
         */
        void run(int times){
            for(int i = 0; i < times; ++i){
                Hongbao bao = new Hongbao(hongbaoSize, players.length);
                for(HongbaoPlayer player : players){
                    int result = bao.lottery();
                    player.rest += result;
                    player.resultList.add(result);
                }
                players[bao.getMinIndex()].minTimes ++;
                players[bao.getMaxIndex()].maxTimes ++;
                if(luckyTurnNext){
                    players[bao.getMaxIndex()].rest -= hongbaoSize;
                }else{
                    players[bao.getMinIndex()].rest -= hongbaoSize;
                }
                
                Integer minScoreTimes = minScoreTimesMap.get(bao.getMinScore());
                minScoreTimesMap.put(bao.getMinScore(), minScoreTimes == null ? 1 : minScoreTimes + 1);
//                bao.report();
            }
            totalTimes += times;
        }
        
        
        /**
         * 打印模拟报告
         * @Date 2017年2月27日 下午3:37:00
         */
        void report(){
            System.out.println("Total times : " + totalTimes);
            for(HongbaoPlayer player : players){    
                player.report();
            }
            System.out.println("MinScoreTimes distribution in form: [socre->percent]");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(Map.Entry<Integer, Integer> entry : minScoreTimesMap.entrySet()){
                sb.append(entry.getKey())
                .append("->")
                .append(MathUtil.demical((double)entry.getValue() / totalTimes * 100, 2))
                .append("%")
                .append(", ");
            }
            sb.append("]");
            System.out.println(sb.toString());
        }
    }
    
    

}
