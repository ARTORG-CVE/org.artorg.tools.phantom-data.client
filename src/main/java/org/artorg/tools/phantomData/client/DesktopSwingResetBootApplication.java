package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.isInitialized;

import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.DesktopSwingResetBootServer;
import org.artorg.tools.phantomData.server.boot.BootUtilsServer;

public class DesktopSwingResetBootApplication extends ClientBooter {
	
	{
		
	}
	
    public static void main(String[] args) {
    	new DesktopSwingResetBootApplication().boot(args);
    }

	@Override
	public void boot(String[] args) {
		setServerBooter(new DesktopSwingResetBootServer());
		if (!BootUtilsServer.isConnected(getServerBooter().getServerConfig())) {
			getServerBooter().getServerConfig().setServerStartedEmbedded(true);
			getServerBooter().boot(args);
		}
    	
    	try {
    		HttpDatabaseCrud.setUrlLocalhost(getServerBooter().getServerConfig().getUrlLocalhost());
    		if (!isInitialized())
				initDatabase();
    		FxUtil.setMainFxClass(MainFx.class);
    		MainFx.launch(args);
    	} catch(Exception e) {
    		getServerBooter().setConsoleFrameVisible(true);
    		e.printStackTrace();
    	}
	}
    
}
