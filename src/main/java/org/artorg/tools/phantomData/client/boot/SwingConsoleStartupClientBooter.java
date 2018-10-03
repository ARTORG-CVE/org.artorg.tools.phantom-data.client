package org.artorg.tools.phantomData.client.boot;

public abstract class SwingConsoleStartupClientBooter extends ClientBooter {
	
	public boolean catchedBoot(String[] args, Runnable rc) {
		try {
			rc.run();
		} catch (Exception e) {
			try {
				getConsoleFrame().setTitle("Phantom Database - Exception thrown!");
				setErrorOccured(true);
				if (!super.handleException(e))
					e.printStackTrace();
			}
			catch (Exception e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
		if (!getConsoleFrame().isErrorOccured() && !isErrorOccured() && !isDebugConsoleMode())
			getConsoleFrame().setVisible(false);
		else 
			if (isRunnableJarExecution()) 
				getConsoleFrame().setVisible(true);
		if (isErrorOccured())
			return false;
		return true;
	}

	private boolean isDebugConsoleMode() {
		return true;
	}

}
