package org.daheiz.base.test.unit;

public class Desk<T extends Book> {

    protected T book;
    
    public Desk(T t) {
        book = t;
    }
    
    public int f() {
        return book.getNum();
    }
}
