package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.server.boot.LaunchConfigurationServer;

public class LaunchConfigurationClient extends LaunchConfigurationServer {

	@Override
	protected void setUrlLocalhost(String urlLocalhost) {
		super.setUrlLocalhost(urlLocalhost);
		HttpDatabaseCrud.setUrlLocalhost(urlLocalhost);
//		MainController.setUrlLocalhost(urlLocalhost);
	}
	
	@Override
	protected void setUrlShutdownActuator(String urlShutdownActuator) {
		super.setUrlShutdownActuator(urlShutdownActuator);
		MainController.setUrlShutdownActuator(urlShutdownActuator);
	}
	
	@Override
	protected void setBootApplicationClass(Class<?> mainClass) {
		super.setBootApplicationClass(mainClass);
//		MainController.setMainClass(mainClass);
	}
	
}
