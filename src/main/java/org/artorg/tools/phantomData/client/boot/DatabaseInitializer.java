package org.artorg.tools.phantomData.client.boot;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.IntegerPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class DatabaseInitializer {
	private static AnnulusDiameterConnector adConn = AnnulusDiameterConnector.get();
	private static FabricationTypeConnector fTypeConn = FabricationTypeConnector.get();
	private static LiteratureBaseConnector litBaseConn = LiteratureBaseConnector.get();
	private static PropertyFieldConnector fieldConn = PropertyFieldConnector.get();
	private static BooleanPropertyConnector boolPropConn = BooleanPropertyConnector.get();
	private static IntegerPropertyConnector intPropConn = IntegerPropertyConnector.get();
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
	
	public static boolean isInitialized() {
		try {
			return  (phantomConn.readAll().length>0);
		} catch (Exception e) {}
		return false;
	}

	private static void initAnnulusDiameter() {
		adConn.create(new AnnulusDiameter(21, 21.0));
		adConn.create(new AnnulusDiameter(25, 25.0));
	}

	private static void initFabricationtype() {
		FabricationType fType1 = new FabricationType("A", "Small, thin"); 
		fTypeConn.create(fType1);
		fTypeConn.create(new FabricationType("B", "Small, thick"));
		fTypeConn.create(new FabricationType("C", "Tomo, thin"));
		fTypeConn.create(new FabricationType("D", "Tomo, thick"));
		
		System.out.println(fTypeConn.read(fType1));
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
		PropertyField field3 = new PropertyField("nSimulations", "num. of simulations?");
		fieldConn.create(field1);
		fieldConn.create(field2);
		fieldConn.create(field3);
		field1 = fieldConn.read(field1);
		field2 = fieldConn.read(field2);
		field3 = fieldConn.read(field3);

		BooleanProperty bool1 = new BooleanProperty(field1, true); 
		BooleanProperty bool2 = new BooleanProperty(field1, false);
		BooleanProperty bool3 = new BooleanProperty(field2, true);
		BooleanProperty bool4 = new BooleanProperty(field2, false);
		boolPropConn.create(bool1);
		boolPropConn.create(bool2);
		boolPropConn.create(bool3);
		boolPropConn.create(bool4);
		bool1 = boolPropConn.read(bool1);
		bool2 = boolPropConn.read(bool2);
		bool3 = boolPropConn.read(bool3);
		bool4 = boolPropConn.read(bool4);
		
		IntegerProperty int1 = new IntegerProperty(field3, 20); 
		intPropConn.create(int1);
		int1 = intPropConn.read(int1);
		
		Special special1 = new Special("L");
		special1.addProperty(bool1);
		special1.addProperty(bool4);
		specConn.create(special1);
		
		Special special2 = new Special("C");
		special2.addProperty(bool2);
		special2.addProperty(bool3);
		specConn.create(special2);
		
		Special special3 = new Special("N");
		special3.addProperty(bool2);
		special3.addProperty(bool4);
		specConn.create(special3);
		
		Special special4 = new Special("ZZ");
		special4.addProperty(int1);
		specConn.create(special4);

	}

	private static void initFiles() {
		FileTypeConnector fileTypeConn = FileTypeConnector.get();
		FileType fileType1 = new FileType("phantom-specific-geometry-main-cad-model");
		FileType fileType2 = new FileType("phantom-specific-geometry-fabrication-part");
		FileType fileType3 = new FileType("thesis-master");
		FileType fileType4 = new FileType("thesis-phd"); 
		
		fileTypeConn.create(fileType1);
		fileTypeConn.create(fileType2);
		fileTypeConn.create(fileType3);
		fileTypeConn.create(fileType4);

		FileConnector fileConn = FileConnector.get();
		fileConn.create(new PhantomFile("", "model", "stl", fileTypeConn.read(fileType1)));
		fileConn.create(new PhantomFile("", "model2", "stl", fileTypeConn.read(fileType1)));
		fileConn.create(new PhantomFile("", "model3", "stl", fileTypeConn.read(fileType3)));

		fileConn.readByName("model").create("D:/Users/Marc/Desktop/test1.stl");
		fileConn.readByName("model").toString();

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
	}

	private static Phantom createPhantom(int annulusDiameter, String fType, String litBase,
			String special, int number) {
		AnnulusDiameter annulusDiameter2 = AnnulusDiameterConnector.get()
				.readByShortcut(annulusDiameter);
		FabricationType fType2 = FabricationTypeConnector.get().readByShortcut(fType);
		LiteratureBase litBase2 = LiteratureBaseConnector.get().readByShortcut(litBase);
		Special special2 = SpecialConnector.get().readByShortcut(special);
		return new Phantom(annulusDiameter2, fType2, litBase2, special2, number);
	}

}
