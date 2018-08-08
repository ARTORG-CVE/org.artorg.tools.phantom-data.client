import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteDatabase;
import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.shutdownServer;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connector.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connector.FileConnector;
import org.artorg.tools.phantomData.client.connector.FileTypeConnector;
import org.artorg.tools.phantomData.client.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connector.PhantomConnector;
import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.control.MainController;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainTest extends Application {
	
    public static void main( String[] args ) {
    	shutdownServer();
		deleteDatabase();
		deleteFileStructure();
		prepareFileStructure();
		logInfos();
		startingServer(args);
		
		initDatabase();
 
    	
    	launch(args);
    }
    
    @Override
	public void start(Stage stage) throws Exception {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
			MainController controller = new MainController();
			loader.setController(controller);
			Pane root = loader.load();
			Scene scene = new Scene(root,1400,1400);
			scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setWidth(root.getPrefWidth());
			stage.setHeight(root.getPrefHeight()+50);
			stage.setTitle("Phantom database");
			stage.show();
	}
    
    @Override
    public void stop(){
    	shutdownServer();
    }
    
    private static AnnulusDiameterConnector adConn = AnnulusDiameterConnector.get();
	private static FabricationTypeConnector fTypeConn = FabricationTypeConnector.get(); 
	private static LiteratureBaseConnector litBaseConn = LiteratureBaseConnector.get();
	private static PropertyFieldConnector fieldConn = PropertyFieldConnector.get();
	private static BooleanPropertyConnector propConn = BooleanPropertyConnector.get();
	private static SpecialConnector specConn = SpecialConnector.get();
	private static PhantomConnector phantomConn = PhantomConnector.get();
	
	public static void initDatabase() {
		initAnnulusDiameter();
		initFabricationtype();
		initLiteratureBase();
		initSpecial();
		initFiles();
		initPhantoms();
	}
	
	private static void initAnnulusDiameter() {
		adConn.create(new AnnulusDiameter(21, 21.0));
		adConn.create(new AnnulusDiameter(25, 25.0));
	}
	
	private static void initFabricationtype() {
		fTypeConn.create(new FabricationType("A", "Small, thin"));
		fTypeConn.create(new FabricationType("B", "Small, thick"));
		fTypeConn.create(new FabricationType("C", "Tomo, thin"));
		fTypeConn.create(new FabricationType("D", "Tomo, thick"));
	}
	
	private static void initLiteratureBase() {
		litBaseConn.create(new LiteratureBase("J", "Mean S&C, Reul"));
		litBaseConn.create(new LiteratureBase("C", "Swanson and Clark"));
		litBaseConn.create(new LiteratureBase("L", "Reul Large"));
		litBaseConn.create(new LiteratureBase("R", "Reul mean"));
		litBaseConn.create(new LiteratureBase("S", "Reul small"));
		litBaseConn.create(new LiteratureBase("B", "Biotronik"));
		litBaseConn.create(new LiteratureBase("P", "Patient specific"));
	}
	
	private static void initSpecial() {
		PropertyField field1 = new PropertyField("hasLeaflets", "has leaflets?");
		PropertyField field2 = new PropertyField("hasCoronaries", "has coronaries?");
		fieldConn.create(field1);
		fieldConn.create(field2);
		field1 = fieldConn.readById(1);
		field2 = fieldConn.readById(2);
		
		propConn.create(new BooleanProperty(field1, true));
		propConn.create(new BooleanProperty(field1, false));
		propConn.create(new BooleanProperty(field2, true));
		propConn.create(new BooleanProperty(field2, false));
		
		List<BooleanProperty> list1 = new ArrayList<BooleanProperty>();
		list1.add(propConn.readById(1));
		list1.add(propConn.readById(4));
		specConn.create(new Special("L", list1));
		
		List<BooleanProperty> list2 = new ArrayList<BooleanProperty>();
		list2.add(propConn.readById(2));
		list2.add(propConn.readById(3));
		specConn.create(new Special("C", list2));
		
		List<BooleanProperty> list3 = new ArrayList<BooleanProperty>();
		list3.add(propConn.readById(2));
		list3.add(propConn.readById(4));
		specConn.create(new Special("N", list3));
	}
	
	private static void initFiles() {
		FileTypeConnector fileTypeConn = FileTypeConnector.get();
		fileTypeConn.create(new FileType("phantom-specific-geometry-main-cad-model"));
		fileTypeConn.create(new FileType("phantom-specific-geometry-fabrication-part"));
		fileTypeConn.create(new FileType("thesis-master"));
		fileTypeConn.create(new FileType("thesis-phd"));
		
		FileConnector fileConn = FileConnector.get();
		fileConn.create(new PhantomFile("", "model", "stl", fileTypeConn.readById(1)));
		fileConn.create(new PhantomFile("", "model2", "stl", fileTypeConn.readById(1)));
		fileConn.create(new PhantomFile("", "model3", "stl", fileTypeConn.readById(3)));
		
		fileConn.readByName("model").create("D:/Users/Marc/Desktop/test1.stl");
		System.out.println(fileConn.readByName("model").toString());
		
	}
	
	private static void initPhantoms() {
		PhantomConnector phantConn = PhantomConnector.get();
		Phantom[] phantoms = new Phantom[15]; 
		phantoms[0] = createPhantom(21, "A", "C", "N", 3);
		phantoms[1] = createPhantom(21, "A", "C", "N", 5);
		phantoms[2] = createPhantom(21, "A", "C", "N", 7);
		phantoms[3] = createPhantom(21, "A", "C", "N", 8);
		phantoms[4] = createPhantom(21, "A", "C", "N", 9);
		phantoms[5] = createPhantom(21, "A", "C", "N", 12);
		phantoms[6] = createPhantom(21, "A", "J", "L", 1);
		phantoms[7] = createPhantom(25, "A", "J", "L", 1);
		phantoms[8] = createPhantom(25, "A", "J", "L", 2);
		phantoms[9] = createPhantom(25, "A", "J", "L", 3);
		phantoms[10] = createPhantom(25, "A", "J", "L", 5);
		phantoms[11] = createPhantom(25, "A", "J", "L", 6);
		phantoms[12] = createPhantom(25, "A", "J", "N", 1);
		phantoms[13] = createPhantom(25, "A", "J", "N", 2);
		phantoms[14] = createPhantom(21, "A", "P", "N", 1);
		
		List<PhantomFile> files0 = new ArrayList<PhantomFile>();
		files0.add(FileConnector.get().readByName("model"));
		files0.add(FileConnector.get().readByName("model2"));
		phantoms[0].setFiles(files0);
		phantConn.create(phantoms);
		
		System.out.println(phantomConn.toString());
	}
	
	private static Phantom createPhantom(int annulusDiameter, String fType, String litBase, String special, int number) {
		AnnulusDiameter annulusDiameter2 = AnnulusDiameterConnector.get()
				.readByShortcut(annulusDiameter);
		FabricationType fType2 = FabricationTypeConnector.get()
				.readByShortcut(fType);
		LiteratureBase litBase2 = LiteratureBaseConnector.get().readByShortcut(litBase);
		Special special2 = SpecialConnector.get().readByShortcut(special);
		return new Phantom(annulusDiameter2, fType2, litBase2, special2, number);
	}

}
