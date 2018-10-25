package org.artorg.tools.phantomData.client;

import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.artorg.tools.phantomData.client.controllers.editFactories.PersonEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PhantomEditFactoryController;
import org.artorg.tools.phantomData.server.beans.EntityBeanInfos;
import org.reflections.Reflections;

public class Main extends DesktopSwingBootApplication {
	private static ClientBooter clientBooter;
	private static final Reflections reflections = new Reflections("org.artorg.tools.phantomData");
	private static final EntityBeanInfos beanInfos = new EntityBeanInfos(reflections);

	public static Reflections getReflections() {
		return reflections;
	}

	public static void main(String[] args) {
		new Main().boot(args);
		
		
		PersonEditFactoryController test = new PersonEditFactoryController();
		
//		PhantomEditFactoryController test = new PhantomEditFactoryController();
		
		

	}
	
	public static ClientBooter getClientBooter() {
		return clientBooter;
	}

	public static void setClientBooter(ClientBooter clientBooter) {
		Main.clientBooter = clientBooter;
	}

	public static EntityBeanInfos getBeaninfos() {
		return beanInfos;
	}

}
