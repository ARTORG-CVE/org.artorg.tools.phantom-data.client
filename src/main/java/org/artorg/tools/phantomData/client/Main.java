package org.artorg.tools.phantomData.client;

import org.artorg.tools.phantomData.client.boot.ClientBooter;
import org.reflections.Reflections;

public class Main extends DesktopSwingBootApplication {
	private static ClientBooter clientBooter;
	private static final Reflections reflections = new Reflections("org.artorg.tools.phantomData");

	public static Reflections getReflections() {
		return reflections;
	}

	public static void main(String[] args) {
		new Main().boot(args);
	}
	
	public static ClientBooter getClientBooter() {
		return clientBooter;
	}

	public static void setClientBooter(ClientBooter clientBooter) {
		Main.clientBooter = clientBooter;
	}

}
