package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.server.boot.ServerBooter;

public abstract class ClientBooter extends MainFx {
	private ServerBooter booter;
	
	public abstract void boot(String[] args);
	
	public ServerBooter getServerBooter() {
		return booter;
	}
	
	public void setServerBooter(ServerBooter booter) {
		this.booter = booter;
		Main.setClientBooter(this);
	}

}
