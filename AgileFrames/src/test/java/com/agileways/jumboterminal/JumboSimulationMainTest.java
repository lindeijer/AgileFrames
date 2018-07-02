package com.agileways.jumboterminal;

import org.junit.Test;

import com.agileways.traces.SimulationMain;

import junit.framework.TestCase;

public class JumboSimulationMainTest {

	@Test
	public void myFirstTest() {
		TestCase.assertEquals(2, 1 + 1);
		SimulationMain.main(null);//
	}
	
	public static void main(String [] args) {
		org.junit.runner.JUnitCore.runClasses(JumboSimulationMainTest.class);
	}

}
