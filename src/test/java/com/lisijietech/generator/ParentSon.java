package com.lisijietech.generator;

public class ParentSon extends Parent{
	private int i = 3;
	private int b = 4;
	
//	public void test() {
//		System.out.println("static");
//	}
//	public static void test() {
//		System.out.println("static");
//	}
	
//	public int getI() {
//		return i;
//	}
//
//	public void setI(int i) {
//		this.i = i;
//	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

//	public static void main(String[] args) {
//		Parent p = new Parent();
//		ParentSon s = new ParentSon();
//		s.test();
//		System.out.println(s.getI());
//		System.out.println(s.i);
//	}

}

class Parent {
	private int i = 1;
	int b = 2;
	
	public static void test() {
		System.out.println("static");
	}
	
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
}