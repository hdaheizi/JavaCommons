package top.daheizi.commons.test;

import com.reign.util.random.RandomUtils;

public class TestCalc {
    
    public static void main(String[] args) {
        int num = 0;
        long time = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            int a = RandomUtils.nextInt(1, 18);
            int b = RandomUtils.nextInt(1, 5);
            
//            double c = Math.sqrt(a * a + b * b);

            int d = a + b;
//            double dc = d - c;
//            System.out.println("a=" + a + ", b=" + b + ", dc=" + dc);
//            if(Math.abs(dc) > 1.2){
//                num++;
//                System.out.println("outof--------------------");
//            }
        }
        System.out.println(System.currentTimeMillis() - time);
        
    }
}
