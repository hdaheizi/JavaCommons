package org.daheiz.base.test.unit;


public class OtherBook extends Book implements IBook{

	private final int type;
	
	public OtherBook() {
		super(10);
		type = 77;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	public int getNum() {
		return 100;
	}
	
	public void doSomething(Integer param1, int[] array, String param2, int param3){
		System.out.println("doSomething: " + param1 + param2 + param3 + array);
	};
}

