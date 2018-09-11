package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.server.boot.ServerBooter;

public interface ClientBooter {

	boolean boot(String[] args);
	
	ServerBooter getServerBooter();
	
	LaunchConfigurationClient getLaunchConfigurationClient();
	
	ClientLauncher getClientLauncher();

}
