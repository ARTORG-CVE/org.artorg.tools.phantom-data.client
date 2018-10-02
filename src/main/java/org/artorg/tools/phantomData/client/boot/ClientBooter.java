package org.artorg.tools.phantomData.client.boot;

import java.io.File;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.IBooter;
import org.artorg.tools.phantomData.server.boot.ServerBooter;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;

import huma.io.ConsoleDiverter;

public abstract class ClientBooter extends MainFx implements IBooter {
	private ServerBooter booter;
	
	public abstract boolean catchedBoot(String[] args, Runnable rc);
	
	public ServerBooter getServerBooter() {
		return booter;
	}
	
	public void setServerBooter(ServerBooter booter) {
		this.booter = booter;
		Main.setClientBooter(this);
	}
	
	protected boolean handleException(Exception e) {
		try {
		if (booter.handleException(e))
			return true;
		} catch(Exception e2) {}
		
		return false;
		
	}

	@Override
	public boolean isRunnableJarExecution() {
		return getServerBooter().isRunnableJarExecution();
	}

	@Override
	public File getRunnableJarExecutionDirectory() {
		return getServerBooter().getRunnableJarExecutionDirectory();
	}

	@Override
	public void setConsoleFrameVisible(boolean b) {
		getServerBooter().setConsoleFrameVisible(b);
	}

	@Override
	public void setStartupFrameVisible(boolean b) {
		getServerBooter().setStartupFrameVisible(b);
	}

	@Override
	public ConsoleDiverter getConsoleDiverter() {
		return getServerBooter().getConsoleDiverter();
	}

	@Override
	public Class<?> getBootApplicationClass() {
		return getServerBooter().getBootApplicationClass();
	}

	@Override
	public void setBootApplicationClass(Class<?> bootApplicationClass) {
		 getServerBooter().setBootApplicationClass(bootApplicationClass);
	}

	@Override
	public void setConsoleDiverter(ConsoleDiverter consoleDiverter) {
		getServerBooter().setConsoleDiverter(consoleDiverter);
	}

	@Override
	public boolean isErrorOccured() {
		return getServerBooter().isErrorOccured();
	}

	@Override
	public void setErrorOccured(boolean errorOccured) {
		getServerBooter().setErrorOccured(errorOccured);
	}

	@Override
	public ConsoleFrame getConsoleFrame() {
		return getServerBooter().getConsoleFrame();
	}

	@Override
	public void setConsoleFrame(ConsoleFrame consoleFrame) {
		getServerBooter().setConsoleFrame(consoleFrame);
	}

	@Override
	public StartupProgressFrame getStartupFrame() {
		return getServerBooter().getStartupFrame();
	}

	@Override
	public void setStartupFrame(StartupProgressFrame startupFrame) {
		getServerBooter().setStartupFrame(startupFrame);
	}

}
