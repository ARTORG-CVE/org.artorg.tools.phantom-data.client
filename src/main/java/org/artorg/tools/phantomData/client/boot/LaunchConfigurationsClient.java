package org.artorg.tools.phantomData.client.boot;

import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.deleteDatabase;
import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.deleteFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.isConnected;
import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.shutdownServer;
import static org.artorg.tools.phantomData.server.boot.BootUtilsServer.startingServer;

public class LaunchConfigurationsClient {
	public static final LaunchConfigurationClient START_SERVER;
	public static final LaunchConfigurationClient START_SERVER_TEST;
	
	static {
		START_SERVER = new LaunchConfigurationClient();
		START_SERVER.setConsumer(args -> {
			prepareFileStructure(START_SERVER);
			
			new Thread(() -> startingServer(START_SERVER, args)).start();
				
			while(!isConnected(START_SERVER)) {
				try {Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		});
		
		START_SERVER_TEST = new LaunchConfigurationClient();
		START_SERVER_TEST.setConsumer(args -> {
			shutdownServer(START_SERVER_TEST);
			deleteDatabase(START_SERVER_TEST);
			deleteFileStructure(START_SERVER_TEST);
			prepareFileStructure(START_SERVER_TEST);
//			logInfos();
			startingServer(START_SERVER_TEST, args);
			
		});
		

	}
	
}
