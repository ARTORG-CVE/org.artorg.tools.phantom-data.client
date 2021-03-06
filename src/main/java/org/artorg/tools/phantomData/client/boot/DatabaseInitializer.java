package org.artorg.tools.phantomData.client.boot;

import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PermissionDeniedException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.model.IdentifiableUUID;
import org.artorg.tools.phantomData.server.models.base.person.*;
import org.artorg.tools.phantomData.server.models.base.property.*;
import org.artorg.tools.phantomData.server.models.phantom.*;

public class DatabaseInitializer {
	private static ICrudConnector<Gender> genderConn =
		Connectors.get(Gender.class);
	private static ICrudConnector<AcademicTitle> academicTitleConn =
		Connectors.get(AcademicTitle.class);
	private static ICrudConnector<Person> personConn =
		Connectors.get(Person.class);
	private static ICrudConnector<AnnulusDiameter> adConn =
		Connectors.get(AnnulusDiameter.class);
	private static ICrudConnector<FabricationType> fTypeConn =
		Connectors.get(FabricationType.class);
	private static ICrudConnector<LiteratureBase> litBaseConn =
		Connectors.get(LiteratureBase.class);
	private static ICrudConnector<PropertyField> fieldConn =
		Connectors.get(PropertyField.class);
	private static ICrudConnector<BooleanProperty> boolPropConn =
		Connectors.get(BooleanProperty.class);
	private static ICrudConnector<IntegerProperty> intPropConn =
		Connectors.get(IntegerProperty.class);
	private static ICrudConnector<Special> specConn =
		Connectors.get(Special.class);
	private static ICrudConnector<Material> materialConn =
			Connectors.get(Material.class);
	private static ICrudConnector<Phantomina> phantominaConn =
		Connectors.get(Phantomina.class);
	private static ICrudConnector<Phantom> phantomConn =
		Connectors.get(Phantom.class);
	private static ICrudConnector<Manufacturing> manufactConn =
			Connectors.get(Manufacturing.class);

	public static void initDatabase() {
		Person admin = null;
		try {
			admin = initPerson();
		} catch (PermissionDeniedException e1) {
			e1.printStackTrace();
		}
		UserAdmin.login(admin);
		try {
			initAnnulusDiameter();
			initFabricationtype();
			initLiteratureBase();
			initSpecial();
			initPhantoms();
		} catch (NoUserLoggedInException | PostException e) {
			e.printStackTrace();
		}
		UserAdmin.logout();
	}

	public static boolean isInitialized() {
		try {
			return (adConn.readAll().length > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Person initPerson() throws PermissionDeniedException {
		ICrudConnector<AcademicTitle> academicTitleConnInit =
			Connectors.get(AcademicTitle.class);
		ICrudConnector<Person> personConnInit = Connectors.get(Person.class);
		Gender male = new Gender("Male");
		Gender female = new Gender("Female");
		try {
			genderConn.create(new Gender[] {male, female});
			AcademicTitle noAcademicTitle = new AcademicTitle("", "No title");
			academicTitleConnInit.create(noAcademicTitle);
			Person hutzli = new Person(noAcademicTitle, "Marc", "Hutzli", "1234", male);
			hutzli.setId(IdentifiableUUID.getUuid("624f42e6252b40c98eecd346f989c0cc"));
			hutzli.setPassword("1234");
			personConnInit.create(hutzli);
			
			Person admin = new Person(noAcademicTitle, "ADMIN", "ADMIN", "1234", male);
			admin.setId(IdentifiableUUID.getUuid("2ccc4440340a4afc9a0307d4167fcefe"));
			admin.setPassword("1234");
			personConnInit.create(admin);
			
			AcademicTitle master = new AcademicTitle("M.Sc.", "Master of Science");
			academicTitleConn.create(new AcademicTitle("Dr.", "General Doctor title"));
			academicTitleConn
				.create(new AcademicTitle("Dr. med.", "Doctor title in medicine"));
			academicTitleConn
				.create(new AcademicTitle("Dr. phil.", "Doctor title in philosophy"));
			academicTitleConn.create(master);
			academicTitleConn.create(new AcademicTitle("B.Sc.", "Bachelor of Science"));
			personConn.create(new Person(master, "Silje", "Ekroll Jahren", "1234", female));
			personConn.create(new Person(master, "Joël", "Illi", "1234", male));
			return hutzli;
		} catch (NoUserLoggedInException | PostException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();	
	}
	
	private static void initAnnulusDiameter() throws NoUserLoggedInException, PostException {
		adConn.create(new AnnulusDiameter(21, 21.0));
		adConn.create(new AnnulusDiameter(25, 25.0));
	}

	private static void initFabricationtype() throws NoUserLoggedInException, PostException {
		FabricationType fType1 = new FabricationType("A", "Small, thin");
		fTypeConn.create(fType1);
		fTypeConn.create(new FabricationType("B", "Small, thick"));
		fTypeConn.create(new FabricationType("C", "Tomo, thin"));
		fTypeConn.create(new FabricationType("D", "Tomo, thick"));
	}

	private static void initLiteratureBase() throws NoUserLoggedInException, PostException {
		litBaseConn.create(new LiteratureBase("J", "Mean S&C, Reul"));
		litBaseConn.create(new LiteratureBase("C", "Swanson and Clark"));
		litBaseConn.create(new LiteratureBase("L", "Reul Large"));
		litBaseConn.create(new LiteratureBase("R", "Reul mean"));
		litBaseConn.create(new LiteratureBase("S", "Reul small"));
		litBaseConn.create(new LiteratureBase("B", "Biotronik"));
		litBaseConn.create(new LiteratureBase("P", "Patient specific"));
	}

	private static void initSpecial() throws NoUserLoggedInException, PostException {
		PropertyField field1 = new PropertyField("has leaflets?", "", Special.class, BooleanProperty.class);
		PropertyField field2 = new PropertyField("has coronaries?", "", Special.class, BooleanProperty.class);
		PropertyField field3 =
			new PropertyField("num. of simulations?", "", Special.class, IntegerProperty.class);
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

		Special special1 = new Special("L", "just leaflets");
		special1.getBooleanProperties().add(bool1);
		special1.getBooleanProperties().add(bool4);
		specConn.create(special1);

		Special special2 = new Special("C", "just coronairies");
		special2.getBooleanProperties().add(bool2);
		special2.getBooleanProperties().add(bool3);
		specConn.create(special2);

		Special special3 = new Special("N", "nothing special");
		special3.getBooleanProperties().add(bool2);
		special3.getBooleanProperties().add(bool4);
		specConn.create(special3);

		Special special4 = new Special("ZZ", "test");
		special4.getIntegerProperties().add(int1);
		specConn.create(special4);

	}

	private static void initPhantoms() throws NoUserLoggedInException, PostException {
		
		Manufacturing manufactPrintCast = new Manufacturing("3d print cast", "description");
		manufactConn.create(manufactPrintCast);
		
		Material material = new Material("name", "description");
		materialConn.create(material);
		
		Phantom[] phantoms = new Phantom[15];
		phantoms[0] = createPhantom(21, "A", "C", "N", 3, manufactPrintCast, material, 3.0f);
		phantoms[1] = createPhantom(21, "A", "C", "N", 5, manufactPrintCast, material, 3.0f);
		phantoms[2] = createPhantom(21, "A", "C", "N", 7, manufactPrintCast, material, 3.0f);
		phantoms[3] = createPhantom(21, "A", "C", "N", 8, manufactPrintCast, material, 3.0f);
		phantoms[4] = createPhantom(21, "A", "C", "N", 9, manufactPrintCast, material, 3.0f);
		phantoms[5] = createPhantom(21, "A", "C", "N", 12, manufactPrintCast, material, 3.0f);
		phantoms[6] = createPhantom(21, "A", "J", "L", 1, manufactPrintCast, material, 3.0f);
		phantoms[7] = createPhantom(25, "A", "J", "L", 1, manufactPrintCast, material, 3.0f);
		phantoms[8] = createPhantom(25, "A", "J", "L", 2, manufactPrintCast, material, 3.0f);
		phantoms[9] = createPhantom(25, "A", "J", "L", 3, manufactPrintCast, material, 3.0f);
		phantoms[10] = createPhantom(25, "A", "J", "L", 5, manufactPrintCast, material, 3.0f);
		phantoms[11] = createPhantom(25, "A", "J", "L", 6, manufactPrintCast, material, 3.0f);
		phantoms[12] = createPhantom(25, "A", "J", "N", 1, manufactPrintCast, material, 3.0f);
		phantoms[13] = createPhantom(25, "A", "J", "N", 2, manufactPrintCast, material, 3.0f);
		phantoms[14] = createPhantom(21, "A", "P", "N", 1, manufactPrintCast, material, 3.0f);
		phantomConn.create(phantoms);
	}

	private static Phantom createPhantom(int annulusDiameter, String fType,
		String litBase, String special, int number, Manufacturing manufacturing, Material material, float thickness) throws NoUserLoggedInException, PostException {
		AnnulusDiameter annulusDiameter2 =
			adConn.readByAttribute(annulusDiameter, "shortcut");
		FabricationType fType2 = fTypeConn.readByAttribute(fType, "shortcut");
		LiteratureBase litBase2 = litBaseConn.readByAttribute(litBase, "shortcut");
		Special special2 = specConn.readByAttribute(special, "shortcut");
		Phantomina phantomina =
			new Phantomina(annulusDiameter2, fType2, litBase2, special2);
		final Phantomina finalPhantomina = phantomina;
		List<Phantomina> phantominas = phantominaConn.readAllAsStream()
			.filter(p -> p.getProductId().equals(finalPhantomina.getProductId()))
			.collect(Collectors.toList());
		if (phantominas.size() == 0) phantominaConn.create(phantomina);
		else if (phantominas.size() == 1) phantomina = phantominas.get(0);
		else {
			phantomina = phantominas.get(0);
			throw new UnsupportedOperationException();
		}

		return new Phantom(phantomina, number, manufacturing, material, thickness, true);
	}

}
