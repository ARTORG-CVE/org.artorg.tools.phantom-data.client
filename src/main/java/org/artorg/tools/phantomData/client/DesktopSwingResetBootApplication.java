package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.isInitialized;

import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.DesktopSwingResetBootServer;

public class DesktopSwingResetBootApplication extends ClientBooter {
	
    public static void main(String[] args) {
    	new DesktopSwingResetBootApplication().boot(args);
    }

	@Override
	public void boot(String[] args) {
		setServerBooter(new DesktopSwingResetBootServer());
		getServerBooter().init();
		if (!getServerBooter().isConnected()) {
			getServerBooter().setServerStartedEmbedded(true);
			getServerBooter().boot(args);
		}
    	
    	try {
    		CrudConnectors.connectorGetter = itemClass -> Connectors.getConnector(itemClass); 
    		HttpConnectorSpring.setUrlLocalhost(getServerBooter().getUrlLocalhost());
    		MainController.setUrlLocalhost(getServerBooter().getUrlLocalhost());
    		MainController.setUrlShutdownActuator(getServerBooter().getUrlShutdownActuator());
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
