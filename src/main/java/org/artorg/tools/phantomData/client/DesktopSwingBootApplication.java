package org.artorg.tools.phantomData.client;

import java.util.List;

import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;
import org.artorg.tools.phantomData.server.boot.SwingConsoleFrame;
import org.artorg.tools.phantomData.server.boot.SwingStartupProgressFrame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class DesktopSwingBootApplication extends Application {
	private static final int nConsoleLinesServer = 39;
	private static final int nConsoleLinesClient = 1;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Main.setStage(primaryStage);
		List<String> rawArgs = getParameters().getRaw();
		String[] args = rawArgs.toArray(new String[rawArgs.size()]);

		ConsoleFrame consoleFrame = new SwingConsoleFrame();
		StartupProgressFrame startupFrame = new SwingStartupProgressFrame();
		
		Platform.runLater(() -> {
			Main.createBooter(consoleFrame, startupFrame, nConsoleLinesServer,
				nConsoleLinesClient).catchedBoot(args);
		});
	}

}