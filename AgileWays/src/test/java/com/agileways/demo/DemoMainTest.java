package com.agileways.demo;

public class DemoMainTest {

	public static void main(String[] args) {
		
		System.setProperty("java.rmi.server.ignoreStubClasses", "true");
		String version = "1.0.2";
		String codeBaseAgileFramesCore = " http://localhost:5001/agileframes-core-"+version+".jar";
		String codeBaseAgileFramesFoRcEs = " http://localhost:5002/agileframes-forces-"+version+".jar";
		String codeBaseAgileFramesTrAcEs = " http://localhost:5003/agileframes-traces-"+version+".jar";
		// String codeBaseAgileFramesSeRvEs = " http://localhost:5004/agileframes-services-"+version+".jar";
		String codeBaseAgileFramesVR = " http://localhost:5005/agileframes-vr-"+version+".jar";
		String codeBaseAgileWays = " http://localhost:6001/agileways-"+version+".jar";
		System.setProperty("java.rmi.server.codebase", codeBaseAgileFramesCore + //
				codeBaseAgileFramesFoRcEs + //
				codeBaseAgileFramesTrAcEs + //
				codeBaseAgileFramesVR + //
				codeBaseAgileWays);
		System.setProperty("agileframes.loginbase.hostname", "localhost");
		System.setProperty("agilesystem.quit", "FALSE");
		DemoScene.main(null);//
	}

}
