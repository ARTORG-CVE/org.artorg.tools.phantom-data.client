package org.artorg.tools.phantomData.client;

import org.artorg.tools.phantomData.client.boot.ClientBooter;

public class Main extends DesktopSwingBootApplication {
	private static ClientBooter clientBooter;

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
