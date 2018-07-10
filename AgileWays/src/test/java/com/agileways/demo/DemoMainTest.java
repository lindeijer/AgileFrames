package com.agileways.demo;


import junit.framework.TestCase;

public class DemoMainTest {
	
	public static void main(String [] args) {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		System.setProperty("java.rmi.server.codebase","http://localhost:8088/");
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		TestCase.assertEquals(2, 1 + 1);
		DemoScene.main(null);//
	}

}
