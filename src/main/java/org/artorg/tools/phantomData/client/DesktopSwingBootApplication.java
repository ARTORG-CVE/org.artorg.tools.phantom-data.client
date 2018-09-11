package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.DesktopSwingBootServer;
import org.artorg.tools.phantomData.server.boot.BootUtilsServer;
import org.artorg.tools.phantomData.server.boot.ServerBooter;

public class DesktopSwingBootApplication extends MainFx {
	
    public static void main(String[] args) {
    	ServerBooter booter = new DesktopSwingBootServer();
    	new Thread(() -> booter.boot(args)).start();
		
    	while (!BootUtilsServer.isConnected(booter.getServerConfig())) {
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	try {
    		HttpDatabaseCrud.setUrlLocalhost(booter.getServerConfig().getUrlLocalhost());
    		initDatabase();
//    		FxUtil.setMainFxClass(MainFx.class);
//    		MainFx.launch(args);
    	} catch(Exception e) {
    		booter.setConsoleFrameVisible(true);
    		e.printStackTrace();
    	}
    	
    }
    
}
