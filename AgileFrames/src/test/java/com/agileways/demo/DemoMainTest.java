package com.agileways.demo;

import org.junit.Test;

import junit.framework.TestCase;

public class DemoMainTest {

	@Test
	public void myFirstTest() {
		TestCase.assertEquals(2, 1 + 1);
		DemoScene.main(null);//
	}
	
	public static void main(String [] args) {
		org.junit.runner.JUnitCore.runClasses(DemoMainTest.class);
	}

}
