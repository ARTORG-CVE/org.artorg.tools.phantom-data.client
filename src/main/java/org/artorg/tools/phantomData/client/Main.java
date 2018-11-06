package org.artorg.tools.phantomData.client;

import org.artorg.tools.phantomData.client.beans.EntityBeanInfos;
import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.artorg.tools.phantomData.client.controllers.editFactories.measurement.MeasurementEditFactoryController;
import org.reflections.Reflections;
import org.slf4j.Logger;

public class Main extends DesktopSwingBootApplication {
	private static ClientBooter clientBooter;
	private static final Reflections reflections = new Reflections("org.artorg.tools.phantomData");
	private static final EntityBeanInfos beanInfos = new EntityBeanInfos(reflections);
	private static Class<?> mainFxClass;

	static {
		mainFxClass = null;
		
	}
	
	public static Reflections getReflections() {
		return reflections;
	}

	public static void main(String[] args) {
		
//		MeasurementEditFactoryController test = new MeasurementEditFactoryController();
		
		new Main().boot(args);
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
	
	public static void setMainFxClass(Class<?> mainClass) {
		if (Main.mainFxClass != null) throw new UnsupportedOperationException();
		Main.mainFxClass = mainClass;
	}
	
	public static Class<?> getMainFxClass() {
		if (Main.mainFxClass == null)
			throw new NullPointerException();
		return Main.mainFxClass;
	}

}
