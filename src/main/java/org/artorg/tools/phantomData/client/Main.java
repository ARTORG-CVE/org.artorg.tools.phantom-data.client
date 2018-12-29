package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.boot.DatabaseInitializer;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.modelsUI.PersonifiedUI;
import org.artorg.tools.phantomData.client.modelsUI.base.*;
import org.artorg.tools.phantomData.client.modelsUI.base.person.*;
import org.artorg.tools.phantomData.client.modelsUI.base.property.*;
import org.artorg.tools.phantomData.client.modelsUI.measurement.*;
import org.artorg.tools.phantomData.client.modelsUI.phantom.*;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.ServerBooter;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.server.models.base.*;
import org.artorg.tools.phantomData.server.models.base.person.*;
import org.artorg.tools.phantomData.server.models.base.property.*;
import org.artorg.tools.phantomData.server.models.measurement.*;
import org.artorg.tools.phantomData.server.models.phantom.*;
import org.artorg.tools.phantomData.server.util.FxUtil;
import org.reflections.Reflections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends DesktopFxBootApplication {
	private static ServerBooter booter;
	private static final Reflections reflections = new Reflections("org.artorg.tools.phantomData");
	private static Class<?> mainFxClass;
	private static boolean started;
	private static Scene scene;
	private static Stage stage;
	private static MainController mainController;
	private static final Set<Class<?>> entityClasses;
	private static final Map<Class<?>, UIEntity<?>> uiEntities;
//	private static final List<EntityBeanInfo<?>> entityBeanInfos;

	static {
		mainFxClass = null;
		started = false;

		entityClasses = reflections.getSubTypesOf(DbPersistent.class).stream()
				.filter(c -> c.isAnnotationPresent(Entity.class)).collect(Collectors.toSet());
		uiEntities = new HashMap<>();
//		entityBeanInfos = new ArrayList<>();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public static ServerBooter createBooter(ConsoleFrame consoleFrame,
			StartupProgressFrame startupFrame, int nConsoleLinesServer, int nConsoleLinesClient) {
		return new ServerBooter() {

			@Override
			protected void uncatchedBoot(String[] args) {
				Main.setBooter(this);
				initBeforeServerStart(BootApplication.class, consoleFrame, startupFrame);
				Logger.setDefaultOut(getBooter().getConsoleDiverter().getOut());
				Logger.setDefaultErr(getBooter().getConsoleDiverter().getErr());

				getStartupFrame().setVisible(true);
				getStartupFrame().setTitle("Phantom Database");
				getStartupFrame().setProgressing(true);
				if (isDebugConsoleMode()) getConsoleFrame().setVisible(true);
				if (!isConnected()) {
					getStartupFrame().setnConsoleLines(nConsoleLinesServer + nConsoleLinesClient);
					setServerStartedEmbedded(true);
					Task<Void> task = FxUtil.createTask(() -> startSpringServer(args),
							e -> handleException(e));
					task.setOnSucceeded(event -> {
						Main.bootClient(this);
					});
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
		System.out.println(
				"==================== Booting PhantomData Client Application ====================");
		Logger.info.println("Client - Connected to database at " + getBooter().getDatabasePath());
		Logger.info.println("Client - Connected to files at " + getBooter().getFilesPath());
		Logger.info.println("Client - Started succesful on port " + getBooter().getPort());

		Main.setMainFxClass(DesktopFxBootApplication.class);
		CrudConnector.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlShutdownActuator(booter.getUrlShutdownActuator());
		if (!DatabaseInitializer.isInitialized()) {
			initDatabase();
			Logger.warn.println("No data found. Initialized with sample data");
		}

		loadClientStage();
		booter.finish();

	}

	public static void loadClientStage() {
		uiEntities.put(AcademicTitle.class, new AcademicTitleUI());
		uiEntities.put(Person.class, new PersonUI());
		uiEntities.put(DbFile.class, new DbFileUI());
		uiEntities.put(FileTag.class, new FileTagUI());
		uiEntities.put(Note.class, new NoteUI());
		uiEntities.put(BooleanProperty.class, new BooleanPropertyUI());
		uiEntities.put(DoubleProperty.class, new DoublePropertyUI());
		uiEntities.put(IntegerProperty.class, new IntegerPropertyUI());
		uiEntities.put(StringProperty.class, new StringPropertyUI());
		uiEntities.put(PropertyField.class, new PropertyFieldUI());
		uiEntities.put(ExperimentalSetup.class, new ExperimentalSetupUI());
		uiEntities.put(Measurement.class, new MeasurementUI());
		uiEntities.put(Project.class, new ProjectUI());
		uiEntities.put(AnnulusDiameter.class, new AnnulusDiameterUI());
		uiEntities.put(FabricationType.class, new FabricationTypeUI());
		uiEntities.put(LiteratureBase.class, new LiteratureBaseUI());
		uiEntities.put(Manufacturing.class, new ManufacturingUI());
		uiEntities.put(Phantomina.class, new PhantominaUI());
		uiEntities.put(Phantom.class, new PhantomUI());
		uiEntities.put(Special.class, new SpecialUI());
		uiEntities.put(AbstractProperty.class, new PropertiesUI());
		uiEntities.put(AbstractPersonifiedEntity.class, new PersonifiedUI());
		
//		getEntityClasses().stream().forEach(itemClass -> {
//			entityBeanInfos.add(new EntityBeanInfo(itemClass));
//		});
		
		

		stage = new Stage();
		mainController = new MainController(stage);
		scene = new Scene(mainController);
		FxUtil.addCss(scene);
		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		FxUtil.addIcon(stage);

		// speed optimization
		Connectors.createConnectors(getEntityClasses());

		Logger.setDefaultOut(getBooter().getConsoleDiverter().getOut());
		Logger.setDefaultErr(getBooter().getConsoleDiverter().getErr());
		Platform.runLater(() -> {
			Logger.setDefaultOut(getBooter().getConsoleDiverter().getOut());
			Logger.setDefaultErr(getBooter().getConsoleDiverter().getErr());
		});

		getBooter().getConsoleDiverter().addOutLineConsumer((consoleLines, newLine) -> {
			Platform.runLater(() -> {
				mainController.setStatus(newLine, true);
			});
		});

		getBooter().getConsoleDiverter().addErrLineConsumer((consoleLines, newLine) -> {
			Platform.runLater(() -> {
				mainController.setStatus("Exception thrown", false);
			});
		});

		stage.show();
		stage.requestFocus();
		stage.toFront();

		started = true;
		Logger.info.println("Client booted succesful");
	}

	@SuppressWarnings("unchecked")
	public static <T> UIEntity<T> getUIEntity(Class<T> itemClass) {
		return (UIEntity<T>) uiEntities.get(itemClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends AbstractProperty<T,V>, V> PropertyUI<T,V> getPropertyUIEntity(Class<T> propertyClass) {
		return  (PropertyUI<T, V>) uiEntities.get(propertyClass);
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

	public static Set<Class<?>> getEntityClasses() {
		return entityClasses;
	}

}
