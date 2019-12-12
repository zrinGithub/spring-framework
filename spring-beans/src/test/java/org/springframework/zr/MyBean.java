package org.springframework.zr;


public class MyBean {


	private MyBean2 myBean2;

	private String testStr = "default Str";

	public String getTestStr() {
		return testStr;
	}

	public void setTestStr(String testStr) {
		this.testStr = testStr;
	}

	public MyBean2 getMyBean2() {
		return myBean2;
	}

	public void setMyBean2(MyBean2 myBean2) {
		this.myBean2 = myBean2;
	}
}
