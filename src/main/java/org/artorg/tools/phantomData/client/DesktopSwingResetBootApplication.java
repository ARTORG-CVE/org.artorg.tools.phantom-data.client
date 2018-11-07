package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.isInitialized;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.boot.SwingConsoleStartupClientBooter;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.DesktopSwingResetBootServer;
import org.artorg.tools.phantomData.server.boot.SwingConsoleFrame;
import org.artorg.tools.phantomData.server.boot.SwingStartupProgressFrame;
import org.artorg.tools.phantomData.server.model.base.DbFile;

import huma.io.ConsoleDiverter;
import javafx.application.Application;

public class DesktopSwingResetBootApplication extends SwingConsoleStartupClientBooter {
	private static final int nConsoleLinesServer;
	private static final int nConsoleLinesFx;
	
	static {
		nConsoleLinesServer = 39;
		nConsoleLinesFx = 1;
	}
	
    public static void main(String[] args) {
    	new DesktopSwingResetBootApplication().boot(args);
    }

	public void boot(String[] args) {
		setServerBooter(new DesktopSwingResetBootServer());
		getServerBooter().setBootApplicationClass(BootApplication.class);
		getServerBooter().setExternalConfigOverridable(false);
		setConsoleFrame(new SwingConsoleFrame());
		setConsoleDiverter(new ConsoleDiverter());
		catchedBoot(args, () -> {
			getServerBooter().setStartupFrame(new SwingStartupProgressFrame());
			getServerBooter().init();
			getServerBooter().prepareFileStructure();
			DbFile.setFilesPath(getServerBooter().getFilesPath());
			
			getStartupFrame().setVisible(true);
			getStartupFrame().setTitle("Phantom Database");
			
			if (getServerBooter().isDebugConsoleMode())
				getConsoleFrame().setVisible(true);
			
			if (!getServerBooter().isConnected()) {
				getStartupFrame().setnConsoleLines(nConsoleLinesServer + nConsoleLinesFx);
				getStartupFrame().setProgressing(true);	
				getServerBooter().deleteFileStructure();
				getServerBooter().init();
				getServerBooter().prepareFileStructure();
				getServerBooter().setServerStartedEmbedded(true);
				getServerBooter().startSpringServer(args);
			} else {
				getStartupFrame().setnConsoleLines(nConsoleLinesServer + nConsoleLinesFx);
				getStartupFrame().setProgressing(true);
				getServerBooter().shutdownSpringServer();
				getServerBooter().deleteFileStructure();
				getServerBooter().init();
				getServerBooter().prepareFileStructure();
				getServerBooter().setServerStartedEmbedded(true);
				getServerBooter().startSpringServer(args);
			}
	    	
	    	try {
	    		CrudConnector.connectorGetter = itemClass -> Connectors.getConnector(itemClass); 
	    		HttpConnectorSpring.setUrlLocalhost(getServerBooter().getUrlLocalhost());
	    		MainController.setUrlLocalhost(getServerBooter().getUrlLocalhost());
	    		MainController.setUrlShutdownActuator(getServerBooter().getUrlShutdownActuator());
	    		if (!isInitialized())
					initDatabase();
	    		Main.setMainFxClass(MainFx.class);
	    		new Thread(() -> Application.launch(args)).start();
	    	} catch(Exception e) {
	    		getServerBooter().setConsoleFrameVisible(true);
	    		e.printStackTrace();
	    	}
	    	
	    	while(!MainFx.isStarted()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			getStartupFrame().setVisible(false);
			getStartupFrame().dispose();
			
		});
	}
    
}
