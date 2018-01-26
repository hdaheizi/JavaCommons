package org.daheiz.base.test.unit;

public class Book implements IBook {
    
    private int num;
    
    public Book(){
        num = 1;
    }

    public Book(int num){
        this.num = num;
    }
    
    
    public int getNum(){
        return num;
    }
    
    public void setNum(int num, int num2) {
        this.num = num;
    }
    
    int getType() {
        return 1;
    }
    
    public int read(int any){
        return any;
    }
}
