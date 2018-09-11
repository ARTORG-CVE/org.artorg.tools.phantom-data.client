package org.artorg.tools.phantomData.client.boot;

import org.artorg.tools.phantomData.server.boot.ServerBooter;

public abstract class ClientBooter extends MainFx {

	public abstract void boot(String[] args);
	
	public abstract ServerBooter getServerBooter();

}
