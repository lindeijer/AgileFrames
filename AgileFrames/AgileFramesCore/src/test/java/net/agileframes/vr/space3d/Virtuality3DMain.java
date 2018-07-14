package net.agileframes.vr.space3d;


import junit.framework.TestCase;

public class Virtuality3DMain {
	
	public static void main(String [] args) {
		System.setProperty("java.security.policy", "policy.all" );
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		System.setProperty("java.rmi.server.codebase","http://localhost:8088/");
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		TestCase.assertEquals(2, 1 + 1);
		Virtuality3D.main(null);//
	}

}
