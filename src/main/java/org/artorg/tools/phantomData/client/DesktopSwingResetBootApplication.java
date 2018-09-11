package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.isInitialized;

import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.DesktopSwingResetBootServer;
import org.artorg.tools.phantomData.server.boot.BootUtilsServer;
import org.artorg.tools.phantomData.server.boot.ServerBooter;

public class DesktopSwingResetBootApplication extends ClientBooter {
	private final ServerBooter booter; 
	
	{
		booter = new DesktopSwingResetBootServer();
	}
	
    public static void main(String[] args) {
    	new DesktopSwingResetBootApplication().boot(args);
    }

	@Override
	public void boot(String[] args) {
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
    		
    		System.out.println(PhantomConnector.get().existById(1));
    		
    		if (!isInitialized())
				initDatabase();
    		FxUtil.setMainFxClass(MainFx.class);
    		MainFx.launch(args);
    	} catch(Exception e) {
    		booter.setConsoleFrameVisible(true);
    		e.printStackTrace();
    	}
	}

	@Override
	public ServerBooter getServerBooter() {
		return booter;
	}
    
}
