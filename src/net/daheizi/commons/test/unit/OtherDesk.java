package net.daheizi.commons.test.unit;

public class OtherDesk<T extends OtherBook> extends Desk<T>{

    public OtherDesk(T t) {
        super(t);
    }
    
    @Override
    public int f() {
        return book.getType();
    }

}
