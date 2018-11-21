package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;

import java.util.concurrent.Executors;

import org.artorg.tools.phantomData.client.beans.EntityBeanInfos;
import org.artorg.tools.phantomData.client.boot.DatabaseInitializer;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.ServerBooter;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;
import org.artorg.tools.phantomData.server.util.FxUtil;
import org.reflections.Reflections;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends DesktopFxBootApplication {
	private static ServerBooter booter;
	private static final Reflections reflections =
		new Reflections("org.artorg.tools.phantomData");
	private static final EntityBeanInfos beanInfos = new EntityBeanInfos(reflections);
	private static Class<?> mainFxClass;
	private static boolean started;
	private static Scene scene;
	private static Stage stage;
	private static MainController mainController;

	static {
		mainFxClass = null;
		started = false;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public static ServerBooter createBooter(ConsoleFrame consoleFrame,
		StartupProgressFrame startupFrame, int nConsoleLinesServer,
		int nConsoleLinesClient) {
		return new ServerBooter() {

			@Override
			protected void uncatchedBoot(String[] args) {
				Main.setBooter(this);
				initBeforeServerStart(BootApplication.class, consoleFrame, startupFrame);

				getStartupFrame().setVisible(true);
				getStartupFrame().setTitle("Phantom Database");
				getStartupFrame().setProgressing(true);
				if (isDebugConsoleMode()) getConsoleFrame().setVisible(true);
				if (!isConnected()) {
					getStartupFrame()
						.setnConsoleLines(nConsoleLinesServer + nConsoleLinesClient);
					setServerStartedEmbedded(true);
					Task<Void> task = FxUtil.createTask(() -> startSpringServer(args),
						e -> handleException(e));
					task.setOnSucceeded(event -> Main.bootClient(this));
					task.setOnFailed(event -> finish());
					task.setOnCancelled(event -> finish());
					Executors.newCachedThreadPool().execute(task);
				} else {
					getStartupFrame().setnConsoleLines(nConsoleLinesClient);
					Main.bootClient(this);
				}
			}
		};
	}
	
	public static void bootClient(ServerBooter booter) {
		Main.setMainFxClass(DesktopFxBootApplication.class);
		CrudConnector.connectorGetter = itemClass -> Connectors.getConnector(itemClass);
		HttpConnectorSpring.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlShutdownActuator(booter.getUrlShutdownActuator());
		if (!DatabaseInitializer.isInitialized()) initDatabase();

		loadClientStage();
		booter.finish();
	}

	public static void loadClientStage() {
		stage = new Stage();
		mainController = new MainController(stage);
		scene = new Scene(mainController);
		FxUtil.addCss(scene);
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		FxUtil.addIcon(stage);
		stage.show();
		stage.requestFocus();
		stage.toFront();
		started = true;
	}

	public static Reflections getReflections() {
		return reflections;
	}

	public static ServerBooter getBooter() {
		return booter;
	}

	public static void setBooter(ServerBooter booter) {
		Main.booter = booter;
	}

	public static EntityBeanInfos getBeaninfos() {
		return beanInfos;
	}

	public static void setMainFxClass(Class<?> mainClass) {
		if (Main.mainFxClass != null) throw new UnsupportedOperationException();
		Main.mainFxClass = mainClass;
	}

	public static Class<?> getMainFxClass() {
		if (Main.mainFxClass == null) throw new NullPointerException();
		return Main.mainFxClass;
	}

	public static boolean isStarted() {
		return started;
	}

	public static Scene getScene() {
		return scene;
	}

	public static void setScene(Scene scene) {
		Main.scene = scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		Main.stage = stage;
	}

	public static MainController getMainController() {
		return mainController;
	}

}
