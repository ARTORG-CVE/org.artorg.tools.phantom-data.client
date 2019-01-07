package org.artorg.tools.phantomData.client;

import java.util.List;
import java.util.Locale;

import org.artorg.tools.phantomData.server.DesktopFxBootServer;
import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.FxConsoleFrame;
import org.artorg.tools.phantomData.server.boot.FxStartupProgressController;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;
import org.artorg.tools.phantomData.server.util.FxUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class DesktopFxBootApplication extends Application {
	private static final int nConsoleLinesServer = 40;
	private static final int nConsoleLinesClient = 1;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		Main.setStage(primaryStage);
		List<String> rawArgs = getParameters().getRaw();
		String[] args = rawArgs.toArray(new String[rawArgs.size()]);

		ConsoleFrame consoleFrame = new FxConsoleFrame();
		StartupProgressFrame startupFrame =
			new FxStartupProgressController(primaryStage);
		Parent parent = FxUtil.loadFXML("fxml/Boot.fxml", startupFrame, getClass());

		DesktopFxBootServer.initStartupStage(primaryStage, parent);

		Platform.runLater(() -> {
			Main.createBooter(consoleFrame, startupFrame, nConsoleLinesServer,
				nConsoleLinesClient).catchedBoot(args);
		});
	}

}
