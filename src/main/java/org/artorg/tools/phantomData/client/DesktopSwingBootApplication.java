package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;
import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.isInitialized;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.boot.SwingConsoleStartupClientBooter;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.DesktopSwingBootServer;
import org.artorg.tools.phantomData.server.boot.SwingStartupProgressFrame;

public class DesktopSwingBootApplication extends SwingConsoleStartupClientBooter {

	public static void main(String[] args) {
		new DesktopSwingBootApplication().boot(args);
	}

	public void boot(String[] args) {
		setServerBooter(new DesktopSwingBootServer());
		getServerBooter().setBootApplicationClass(Main.class);
		getServerBooter().setExternalConfigOverridable(false);
		catchedBoot(args, () -> {
			getServerBooter().setStartupFrame(new SwingStartupProgressFrame());
			getServerBooter().init();
			
			getStartupFrame().setVisible(true);
			getStartupFrame().setnConsoleLines(78);
			getStartupFrame().setTitle("Phantom Database");
			getStartupFrame().setProgressing(true);

			setServerBooter(new DesktopSwingBootServer());
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
			} catch (Exception e) {
				getServerBooter().setConsoleFrameVisible(true);
				e.printStackTrace();
			}

			getStartupFrame().setVisible(false);

		});
	}
	
	

}
