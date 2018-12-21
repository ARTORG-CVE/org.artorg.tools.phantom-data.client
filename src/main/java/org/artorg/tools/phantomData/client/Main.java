package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.client.boot.DatabaseInitializer.initDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.artorg.tools.phantomData.client.beans.EntityBeanInfos;
import org.artorg.tools.phantomData.client.boot.DatabaseInitializer;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.controllers.MainController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.modelsUI.base.DbFileUI;
import org.artorg.tools.phantomData.client.modelsUI.base.FileTagUI;
import org.artorg.tools.phantomData.client.modelsUI.base.NoteUI;
import org.artorg.tools.phantomData.client.modelsUI.base.person.AcademicTitleUI;
import org.artorg.tools.phantomData.client.modelsUI.base.person.PersonUI;
import org.artorg.tools.phantomData.client.modelsUI.base.property.BooleanPropertyUI;
import org.artorg.tools.phantomData.client.modelsUI.base.property.DoublePropertyUI;
import org.artorg.tools.phantomData.client.modelsUI.base.property.IntegerPropertyUI;
import org.artorg.tools.phantomData.client.modelsUI.base.property.PropertyFieldUI;
import org.artorg.tools.phantomData.client.modelsUI.base.property.StringPropertyUI;
import org.artorg.tools.phantomData.client.modelsUI.measurement.ExperimentalSetupUI;
import org.artorg.tools.phantomData.client.modelsUI.measurement.MeasurementUI;
import org.artorg.tools.phantomData.client.modelsUI.measurement.ProjectUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.AnnulusDiameterUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.FabricationTypeUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.LiteratureBaseUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.ManufacturingUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.PhantomUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.PhantominaUI;
import org.artorg.tools.phantomData.client.modelsUI.phantom.SpecialUI;
import org.artorg.tools.phantomData.server.BootApplication;
import org.artorg.tools.phantomData.server.boot.ConsoleFrame;
import org.artorg.tools.phantomData.server.boot.ServerBooter;
import org.artorg.tools.phantomData.server.boot.StartupProgressFrame;
import org.artorg.tools.phantomData.server.logging.Logger;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.FileTag;
import org.artorg.tools.phantomData.server.models.base.Note;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;
import org.artorg.tools.phantomData.server.models.base.property.StringProperty;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.Special;
import org.artorg.tools.phantomData.server.util.FxUtil;
import org.reflections.Reflections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends DesktopFxBootApplication {
	private static ServerBooter booter;
	private static final Reflections reflections = new Reflections(
			"org.artorg.tools.phantomData");
	private static final EntityBeanInfos beanInfos = new EntityBeanInfos(reflections);
	private static Class<?> mainFxClass;
	private static boolean started;
	private static Scene scene;
	private static Stage stage;
	private static MainController mainController;
	private static final Map<Class<?>, UIEntity<?>> uiEntities;
	
	static {
		mainFxClass = null;
		started = false;
		
		uiEntities = new HashMap<>();
		
		
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
				Logger.setDefaultOut(getBooter().getConsoleDiverter().getOut());
				Logger.setDefaultErr(getBooter().getConsoleDiverter().getErr());
				
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
//		ICrudConnector.connectorGetter = itemClass -> Connectors.getConnector(itemClass);
		CrudConnector.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlLocalhost(booter.getUrlLocalhost());
		MainController.setUrlShutdownActuator(booter.getUrlShutdownActuator());
		if (!DatabaseInitializer.isInitialized()) initDatabase();
		
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
		Connectors.createConnectors(beanInfos.getEntityClasses());
//		SplitTabView.searchFactoryClasses(beanInfos.getEntityClasses());
		
//		getBooter().getConsoleDiverter().setDefaultOut(System.out);
//		getBooter().getConsoleDiverter().setDefaultErr(System.err);
		Logger.setDefaultOut(getBooter().getConsoleDiverter().getOut());
		Logger.setDefaultErr(getBooter().getConsoleDiverter().getErr());
		
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
	}
	
	@SuppressWarnings("unchecked")
	public static <T> UIEntity<T> getUIEntity(Class<T> itemClass) {
		return (UIEntity<T>) uiEntities.get(itemClass);
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
