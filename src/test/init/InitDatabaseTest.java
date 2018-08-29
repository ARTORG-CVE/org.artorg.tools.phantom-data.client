package init;
import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteDatabase;
import static org.artorg.tools.phantomData.server.boot.BootUtils.deleteFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.shutdownServer;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.FileConnector;
import org.artorg.tools.phantomData.client.connectors.FileTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyContainerConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyContainer;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class InitDatabaseTest {
	
	
	public static void main(String[] args) {
		deleteDatabase();
		deleteFileStructure();
		prepareFileStructure();
		logInfos();
		shutdownServer();
		startingServer(args);
		
		testAnnulusDiameter();
		testFabricationType();
		testLiteratureType();
		testProperty();
		testPhantom();
		testFiles();
	}
	
	private static void testAnnulusDiameter() {
		AnnulusDiameterConnector connector = AnnulusDiameterConnector.get(); 
		AnnulusDiameter d1 = new AnnulusDiameter(2, 11.0);
		AnnulusDiameter d2 = new AnnulusDiameter(4, 28.0);
		AnnulusDiameter d3 = new AnnulusDiameter(5, 29.0);
		connector.create(d1, d2, d3);
		
		System.out.println(connector.toString());

		connector.delete(2);
		
		AnnulusDiameter d4 = new AnnulusDiameter(1,51.0);
		connector.update(d4, 1);
		
		System.out.println(connector.toString());
	}
	
	private static void testFabricationType() {
		FabricationTypeConnector connector = FabricationTypeConnector.get(); 
		FabricationType type1 = new FabricationType("A", "Small");
		FabricationType type2 = new FabricationType("B", "thick");
		FabricationType type3 = new FabricationType("C", "tomo");
		connector.create(type1, type2, type3);
		
		System.out.println(connector.toString());

		connector.delete(2);
		
		FabricationType type4 = new FabricationType("A","Small, thin");
		connector.update(type4, 1);
		
		System.out.println(connector.toString());
		
		System.out.println(connector.readByShortcut("A"));
	}
	
	private static void testLiteratureType() {
		LiteratureBaseConnector connector = LiteratureBaseConnector.get(); 
		LiteratureBase type1 = new LiteratureBase("X", "Mean");
		LiteratureBase type2 = new LiteratureBase("D", "Swanson");
		LiteratureBase type3 = new LiteratureBase("Z", "Reul");
		connector.create(type1, type2, type3);
		
		System.out.println(connector.toString());

		connector.delete(2);
		
		LiteratureBase type4 = new LiteratureBase("A","Patient");
		connector.update(type4, 1);
		
		System.out.println(connector.toString());
		
		System.out.println(connector.readByShortcut("Z"));
	}
	
	private static void testProperty() {
		PropertyFieldConnector fieldConn = PropertyFieldConnector.get();
		PropertyField field1 = new PropertyField("hasLeaflets", "has leaflets?");
		PropertyField field2 = new PropertyField("hasCoronaries", "has coronaries?");
		fieldConn.create(field1, field2);
		field1 = fieldConn.readById(1);
		field2 = fieldConn.readById(2);
		
		BooleanPropertyConnector propConn = BooleanPropertyConnector.get();
		propConn.create(new BooleanProperty(field1, true));
		propConn.create(new BooleanProperty(field1, false));
		propConn.create(new BooleanProperty(field2, true));
		propConn.create(new BooleanProperty(field2, false));

		SpecialConnector specConn = SpecialConnector.get();
		PropertyContainerConnector propContConn = PropertyContainerConnector.get();
		
		Collection<BooleanProperty> list1 = new ArrayList<BooleanProperty>();
		list1.add(propConn.readById(1));
		list1.add(propConn.readById(4));
		PropertyContainer pc1 = new PropertyContainer();
		pc1.setBooleanProperties(list1);
		propContConn.create(pc1);
		specConn.create(new Special("L", pc1));
		
		Collection<BooleanProperty> list2 = new ArrayList<BooleanProperty>();
		list2.add(propConn.readById(2));
		list2.add(propConn.readById(3));
		PropertyContainer pc2 = new PropertyContainer();
		pc2.setBooleanProperties(list2);
		propContConn.create(pc2);
		specConn.create(new Special("C", pc2));
		
		Collection<BooleanProperty> list3 = new ArrayList<BooleanProperty>();
		list3.add(propConn.readById(2));
		list3.add(propConn.readById(4));
		PropertyContainer pc3 = new PropertyContainer();
		pc3.setBooleanProperties(list3);
		propContConn.create(pc3);
		specConn.create(new Special("N", pc3));
		
		specConn.delete(2);
		
		System.out.println(specConn.toString());
		
		List<BooleanProperty> list4 = new ArrayList<BooleanProperty>();
		list4.add(propConn.readById(1));
		PropertyContainer pc4 = new PropertyContainer();
		pc4.setBooleanProperties(list4);
		propContConn.create(pc4);
		specConn.update(new Special("Z", pc4), 1);
		
		System.out.println(specConn.toString());
		
		System.out.println(propConn.readFirstByPropertyFieldName(fieldConn.readById(1)).toString());
		System.out.println(propConn.readFirstByPropertyFieldName(fieldConn.readById(2)).toString());
		
	}
	
	private static void testPhantom() {
		PhantomConnector phantConn = PhantomConnector.get();
		AnnulusDiameterConnector daConn = AnnulusDiameterConnector.get();
		FabricationTypeConnector fTypeConn = FabricationTypeConnector.get();
		LiteratureBaseConnector litBaseConn = LiteratureBaseConnector.get();
		SpecialConnector specConn = SpecialConnector.get();
		
		Phantom p1 = new Phantom();
		p1.setAnnulusDiameter(daConn.readById(1));
		p1.setFabricationType(fTypeConn.readById(1));
		p1.setLiteratureBase(litBaseConn.readById(1));
		p1.setSpecial(specConn.readById(1));
		p1.setNumber(1);
		
		Phantom p2 = new Phantom();
		p2.setAnnulusDiameter(daConn.readById(1));
		p2.setFabricationType(fTypeConn.readById(1));
		p2.setLiteratureBase(litBaseConn.readById(1));
		p2.setSpecial(specConn.readById(3));
		p2.setNumber(2);
		
		Phantom p3 = new Phantom();
		p3.setAnnulusDiameter(daConn.readById(3));
		p3.setFabricationType(fTypeConn.readById(1));
		p3.setLiteratureBase(litBaseConn.readById(3));
		p3.setSpecial(specConn.readById(1));
		p3.setNumber(3);
		phantConn.create(p1, p2, p3);

		System.out.println(phantConn.toString());

		phantConn.delete(2);
		
		Phantom p4 = new Phantom();
		p4.setAnnulusDiameter(daConn.readById(3));
		p4.setFabricationType(fTypeConn.readById(1));
		p4.setLiteratureBase(litBaseConn.readById(3));
		p4.setSpecial(specConn.readById(1));
		p4.setNumber(5);
		phantConn.update(p4,1);
		
		System.out.println(phantConn.toString());
	}
	
	private static void testFiles() {
		FileTypeConnector fileTypeConn = FileTypeConnector.get();
		FileType fileType1 = new FileType("phantom-3d-main-stl");
		FileType fileType2 = new FileType("phantom-3d-fabrication-part-stl");
		FileType fileType3 = new FileType("phantom-3d-fabrictaion-part-gcode");
		fileTypeConn.create(fileType1, fileType2, fileType3);
		
		System.out.println(fileTypeConn.toString());
		
		fileTypeConn.delete(2);
		
		FileType fileType4 = new FileType("thesis-master");
		fileTypeConn.update(fileType4,1);
		
		System.out.println(fileTypeConn.toString());
		
		FileConnector fileConn = FileConnector.get();
		PhantomFile file1 = new PhantomFile("", "model", "stl", fileTypeConn.readById(1));
		PhantomFile file2 = new PhantomFile("", "model2", "stl", fileTypeConn.readById(1));
		PhantomFile file3 = new PhantomFile("", "model3", "stl", fileTypeConn.readById(3));
		fileConn.create(file1, file2, file3);
		
		System.out.println(fileConn.toString());
		
		fileConn.delete(2);
		
		PhantomFile file4 = new PhantomFile("", "model4", "stl", fileTypeConn.readById(3));
		fileConn.update(file4,1);
		
		System.out.println(fileConn.toString());
		
	}
	

}
