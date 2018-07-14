package net.agileframes.server;

import net.jini.jrmp.JrmpExporter;

public class CreateTransactionManagerMain {
	
	public static void main(String [] args) {
		new JrmpExporter();
		System.setProperty("java.security.policy", "policy.all" );
		com.sun.jini.start.ServiceStarter.main(new String[] {"config/start-mahalo-group.config"});
	}

}
