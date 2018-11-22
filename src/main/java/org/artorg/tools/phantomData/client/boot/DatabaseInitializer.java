package org.artorg.tools.phantomData.client.boot;

import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.model.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.model.base.person.Gender;
import org.artorg.tools.phantomData.server.model.base.person.Person;
import org.artorg.tools.phantomData.server.model.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.base.property.PropertyField;
import org.artorg.tools.phantomData.server.model.measurement.PhysicalQuantity;
import org.artorg.tools.phantomData.server.model.measurement.Unit;
import org.artorg.tools.phantomData.server.model.measurement.UnitPrefix;
import org.artorg.tools.phantomData.server.model.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;
import org.artorg.tools.phantomData.server.model.phantom.Phantomina;
import org.artorg.tools.phantomData.server.model.phantom.Special;

public class DatabaseInitializer {
	private static ICrudConnector<Gender, ?> genderConn =
		Connectors.getConnector(Gender.class);
	private static ICrudConnector<AcademicTitle, ?> academicTitleConn =
		Connectors.getConnector(AcademicTitle.class);
	private static ICrudConnector<Person, ?> personConn =
		Connectors.getConnector(Person.class);
	private static ICrudConnector<AnnulusDiameter, ?> adConn =
		Connectors.getConnector(AnnulusDiameter.class);
	private static ICrudConnector<FabricationType, ?> fTypeConn =
		Connectors.getConnector(FabricationType.class);
	private static ICrudConnector<LiteratureBase, ?> litBaseConn =
		Connectors.getConnector(LiteratureBase.class);
	private static ICrudConnector<PropertyField, ?> fieldConn =
		Connectors.getConnector(PropertyField.class);
	private static ICrudConnector<BooleanProperty, ?> boolPropConn =
		Connectors.getConnector(BooleanProperty.class);
	private static ICrudConnector<IntegerProperty, ?> intPropConn =
		Connectors.getConnector(IntegerProperty.class);
	private static ICrudConnector<Special, ?> specConn =
		Connectors.getConnector(Special.class);
	private static ICrudConnector<Phantomina, ?> phantominaConn =
		Connectors.getConnector(Phantomina.class);
	private static ICrudConnector<Phantom, ?> phantomConn =
		Connectors.getConnector(Phantom.class);

	private static ICrudConnector<UnitPrefix, ?> unitPrefixConn =
		Connectors.getConnector(UnitPrefix.class);
	private static ICrudConnector<Unit, ?> unitConn = Connectors.getConnector(Unit.class);
	private static ICrudConnector<PhysicalQuantity, ?> physicalQuantityConn =
		Connectors.getConnector(PhysicalQuantity.class);

	public static void initDatabase() {
		initPerson();
		initAnnulusDiameter();
		initFabricationtype();
		initLiteratureBase();
		initSpecial();
		initPhantoms();

		initUnits();

		System.out.println("Database initialized succesful");

		UserAdmin.logout();

	}

	public static boolean isInitialized() {
		try {
			return (adConn.readAll().length > 0);
		} catch (Exception e) {}
		return false;
	}

	private static void initPerson() {
		ICrudConnector<AcademicTitle, ?> academicTitleConnInit =
			Connectors.getConnector(AcademicTitle.class);
		ICrudConnector<Person, ?> personConnInit = Connectors.getConnector(Person.class);
		Gender male = new Gender("Male");
		Gender female = new Gender("Female");
		genderConn.create(male, female);
		AcademicTitle noAcademicTitle = new AcademicTitle("", "No title");
		academicTitleConnInit.create(noAcademicTitle);
		Person hutzli = new Person(noAcademicTitle, "Marc", "Hutzli", male);
		personConnInit.create(hutzli);
		personConnInit.update(hutzli);
		academicTitleConnInit.update(noAcademicTitle);

		UserAdmin.login(hutzli);
		AcademicTitle master = new AcademicTitle("M.Sc.", "Master of Science");
		academicTitleConn.create(new AcademicTitle("Dr.", "General Doctor title"));
		academicTitleConn
			.create(new AcademicTitle("Dr. med.", "Doctor title in medicine"));
		academicTitleConn
			.create(new AcademicTitle("Dr. phil.", "Doctor title in philosophy"));
		academicTitleConn.create(master);
		academicTitleConn.create(new AcademicTitle("B.Sc.", "Bachelor of Science"));
		personConn.create(new Person(master, "Silje", "Ekroll Jahren", female));
		personConn.create(new Person(master, "Joël", "Illi", male));
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
		PropertyField field1 = new PropertyField("has leaflets?", "", Special.class);
		PropertyField field2 = new PropertyField("has coronaries?", "", Special.class);
		PropertyField field3 =
			new PropertyField("num. of simulations?", "", Special.class);
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

	private static void initPhantoms() {
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
		phantomConn.create(phantoms);
	}

	private static Phantom createPhantom(int annulusDiameter, String fType,
		String litBase, String special, int number) {
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

		return new Phantom(phantomina, number);
	}

	private static void initUnits() {
		UnitPrefix yocto = new UnitPrefix("yocto", "y", -24);
		UnitPrefix zepto = new UnitPrefix("zepto", "z", -21);
		UnitPrefix atto = new UnitPrefix("atto", "a", -18);
		UnitPrefix femto = new UnitPrefix("femto", "f", -15);
		UnitPrefix pico = new UnitPrefix("pico", "p", -12);
		UnitPrefix nano = new UnitPrefix("nano", "n", -9);
		UnitPrefix micro = new UnitPrefix("micro", "μ", -6);
		UnitPrefix milli = new UnitPrefix("milli", "m", -3);
		UnitPrefix centi = new UnitPrefix("centi", "c", -2);
		UnitPrefix deci = new UnitPrefix("deci", "d", -1);
		UnitPrefix none = new UnitPrefix("-", "", 0);
		UnitPrefix deca = new UnitPrefix("deca", "da", 1);
		UnitPrefix hecto = new UnitPrefix("hecto", "h", 2);
		UnitPrefix kilo = new UnitPrefix("kilo", "k", 3);
		UnitPrefix mega = new UnitPrefix("mega", "M", 6);
		UnitPrefix giga = new UnitPrefix("giga", "G", 9);
		UnitPrefix tera = new UnitPrefix("tera", "T", 12);
		UnitPrefix peta = new UnitPrefix("peta", "P", 15);
		UnitPrefix exa = new UnitPrefix("exa", "E", 18);
		UnitPrefix zetta = new UnitPrefix("zetta", "Z", 21);
		UnitPrefix yotta = new UnitPrefix("yotta", "Y", 24);

		unitPrefixConn.create(yocto, zepto, atto, femto, pico, nano, micro, milli, centi,
			deci, none, deca, hecto, kilo, mega, giga, tera, peta, exa, zetta, yotta);

		PhysicalQuantity lenght = new PhysicalQuantity("Length", "l", "");
		PhysicalQuantity time = new PhysicalQuantity("Time", "t", "");
		PhysicalQuantity mass = new PhysicalQuantity("Mass", "m", "");
		PhysicalQuantity temperature = new PhysicalQuantity("Temperature", "T", "");
		PhysicalQuantity pressure = new PhysicalQuantity("Pressure", "p", "");
		PhysicalQuantity velocity = new PhysicalQuantity("Velocity", "v", "");
		PhysicalQuantity acceleration = new PhysicalQuantity("Acceleration", "a", "");
		PhysicalQuantity force = new PhysicalQuantity("Force", "F", "");
		PhysicalQuantity torque = new PhysicalQuantity("Torque", "M", "");
		PhysicalQuantity momentum = new PhysicalQuantity("Momentum", "p", "");
		PhysicalQuantity angularMomentum =
			new PhysicalQuantity("Angular Momentum", "L", "");
		PhysicalQuantity frequency = new PhysicalQuantity("Frequency", "f", "");

		physicalQuantityConn.create(lenght, time, mass, temperature, pressure, velocity,
			acceleration, force, torque, momentum, angularMomentum, frequency);

		unitConn.create(new Unit("N", "Newton", force, none));
		unitConn.create(new Unit("Pa", "Pascal", pressure, none));
		unitConn.create(new Unit("Pa", "Pascal", pressure, mega));
		unitConn.create(new Unit("bar", "Bar", pressure, none));
		unitConn.create(new Unit("Hz", "Hertz", frequency, none));
		unitConn.create(new Unit("Hz", "Hertz", frequency, kilo));
		unitConn.create(new Unit("Hz", "Hertz", frequency, mega));
		unitConn.create(new Unit("Hz", "Hertz", frequency, giga));
		unitConn.create(new Unit("m/s", "", velocity, none));
		unitConn.create(new Unit("m/s^2", "", acceleration, none));
	}

}
