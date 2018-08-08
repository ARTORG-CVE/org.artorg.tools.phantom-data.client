package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.artorg.tools.phantomData.server.connector.PhantomConnector;
import org.artorg.tools.phantomData.server.model.Phantom;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import specification.Table;

public class PhantomTable implements Table<PhantomTable, Phantom> {
	
	private Set<Phantom> phantoms;
	
	{
		phantoms = new HashSet<Phantom>();
		phantoms.addAll(PhantomConnector.get().readAllAsSet());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public TableView<Phantom> createTableView(TableView<Phantom> table) {
		TableColumn<Phantom, String> annulusCol = new TableColumn<Phantom, String>("annulus [mm]");
	    TableColumn<Phantom, String> fTypeCol = new TableColumn<Phantom, String>("type");
	    TableColumn<Phantom, String> literatureBaseCol = new TableColumn<Phantom, String>("literature");
	    TableColumn<Phantom, String> specials = new TableColumn<Phantom, String>("specials");
	    TableColumn<Phantom, String> numberCol = new TableColumn<Phantom, String>("number?");
	
	    Function<Double, String> roundNumberFunc = (d) -> {
	    	if ((double)((int)((double)d)) == d) return String.valueOf((int)((double)d));
	    	return String.format("%.1f", d);
	    };
	    
	    annulusCol.setCellValueFactory(cellData -> new SimpleStringProperty(roundNumberFunc.apply(cellData.getValue().getAnnulusDiameter().getValue())));
	    fTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getfType().getFabricationType()));
	    literatureBaseCol.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getLiteratureBase().getLiteratureBase()));
	    specials.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSpecial().getShortcut())));
	    specials.setCellFactory( tc -> new CheckBoxTableCell<>());
	    numberCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
	    numberCol.setCellFactory( tc -> new CheckBoxTableCell<>());
	    
	    table.getColumns().addAll(annulusCol, fTypeCol, literatureBaseCol, specials, numberCol);

	    phantoms.addAll(PhantomConnector.get().readAllAsSet());
	    ObservableList<Phantom> data = FXCollections.observableArrayList(phantoms);
	    table.setItems(data);
	    return table;
	}

	public boolean contains(int id) {
		return phantoms.stream().filter(p -> p.getId().intValue()==id)
				.findFirst().isPresent();
	}

	@Override
	public List<TableColumn<Phantom, ?>> createColumns() {
		List<TableColumn<Phantom, ?>> columns = new ArrayList<TableColumn<Phantom, ?>>();
		
		TableColumn<Phantom, String> annulusCol = new TableColumn<Phantom, String>("annulus [mm]");
	    TableColumn<Phantom, String> fTypeCol = new TableColumn<Phantom, String>("type");
	    TableColumn<Phantom, String> literatureBaseCol = new TableColumn<Phantom, String>("literature");
	    TableColumn<Phantom, String> specials = new TableColumn<Phantom, String>("specials");
	    TableColumn<Phantom, String> numberCol = new TableColumn<Phantom, String>("number?");
	
	    Function<Double, String> roundNumberFunc = (d) -> {
	    	if ((double)((int)((double)d)) == d) return String.valueOf((int)((double)d));
	    	return String.format("%.1f", d);
	    };
	    
	    annulusCol.setCellValueFactory(cellData -> new SimpleStringProperty(roundNumberFunc.apply(cellData.getValue().getAnnulusDiameter().getValue())));
	    fTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getfType().getFabricationType()));
	    literatureBaseCol.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getLiteratureBase().getLiteratureBase()));
	    specials.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSpecial().getShortcut())));
	    specials.setCellFactory( tc -> new CheckBoxTableCell<>());
	    numberCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
	    numberCol.setCellFactory( tc -> new CheckBoxTableCell<>());
	    
	    columns.add(annulusCol);
	    columns.add(fTypeCol);
	    columns.add(literatureBaseCol);
	    columns.add(specials);
	    columns.add(numberCol);
	    
	    return columns;
	}

	@Override
	public Set<Phantom> getItems() {
		return phantoms;
	}
	
	
	
	
	
	
	
	
	
//	
//	public int getNextPhantomNumber(String idWithoutNumber) {
//		if (!idWithoutNumber.matches(".*-.*-.*-.*"))
//			throw new IllegalArgumentException();
//		
//		return phantoms.stream().filter(p -> p.id.matches(idWithoutNumber)).mapToInt(p -> {
//				Matcher m = Pattern.compile(".*-.*-.*-.*-(.*)").matcher(p.id);
//				if (m.find())
//					return Integer.valueOf(m.group(1));
//				else
//					throw new IllegalArgumentException("An ID of a phantom is not properly collected!");
//			}).reduce((i1, i2) -> (i1 > i2? i1: i2)).orElse(1);
//	}
//	
//	
//	
//	public int getNextNumber(double annulusDiameter, String fType, String literatureBase, String specials) {
//		String id = String.format("%s-%s-%s-%s-.*", 
//				String.valueOf(annulusDiameter),
//				fType,
//				literatureBase,
//				specials);
//		return phantoms.stream().filter(phant -> phant.id.matches(id))
//				.mapToInt(phant -> getNumber(phant))
//				.reduce((i1,i2) -> (i1>i2? i1: i2)).orElse(1);
//	}
//	
//	
//	public Phantom createPhantom(float annulusDiameter, String fType, String literatureBase, String specials) {
//		int number = getNextNumber(annulusDiameter, fType, literatureBase, specials);
//		return createPhantom(annulusDiameter, fType, literatureBase, specials, number);
//	}
//	
//	public Phantom createPhantom(float annulusDiameter, String fType, String literatureBase, String specials, int number) {
//		Phantom p = new Phantom();
//		if (!ANNULUS_DIAMETERS.contains(annulusDiameter) ||
//				!F_TYPES.containsKey(fType) ||
//				!LITERATURE_BASES.containsKey(literatureBase) ||
//				!SPECIALS.containsKey(specials)) throw new IllegalArgumentException();
//		String id = String.format("%s-%s-%s-%s-%s", 
//				(float)((int)(annulusDiameter)) == annulusDiameter? String.valueOf((int)annulusDiameter): 
//					String.valueOf(annulusDiameter),
//				fType,
//				literatureBase,
//				specials,
//				number);
//		if (phantoms.stream().filter(phant -> phant.id.equals(id)).count() > 0)
//			throw new IllegalArgumentException();
//		p.id = id;
//		p.annulusDiameter = annulusDiameter;
//		p.fType = F_TYPES.get(fType);
//		p.literatureBase = LITERATURE_BASES.get(literatureBase);
//		p.specials = SPECIALS.get(specials);
//		return p;
//	}
//	
//	public void addPhantom(float annulusDiameter, String fType, String literatureBase, String specials) {
//		phantoms.add(createPhantom(annulusDiameter, fType, literatureBase, specials));
//	}
//	
//	public void addPhantom(float annulusDiameter, String fType, String literatureBase, String specials, int number) {
//		phantoms.add(createPhantom(annulusDiameter, fType, literatureBase, specials, number));
//	}
//	
//	private String getFtype(Phantom p) {
//		return getIdElement(p, 2);
//	}
//	
//	private String getLiteratureBase(Phantom p) {
//		return getIdElement(p, 3);
//	}
//	
//	private String getSpecials(Phantom p) {
//		return getIdElement(p, 4);
//	}
//	
//	private int getNumber(Phantom p) {
//		return Integer.valueOf(getIdElement(p, 5));
//	}
//	
//	private String getIdElement(Phantom p, int index) {
//		Matcher m = Pattern.compile("(%s)-(%s)-(%s)-(%s)-(%s)").matcher(p.id);
//		if (!m.find()) throw new IllegalArgumentException();
//		return m.group(index);
//	}
//	
//	public void setHasLeaftlets(Phantom p, boolean hasLeaflets) {
//		if (p.specials.containsKey("L"))
//			if ((boolean)p.specials.get("L") == true)
//				p.specials.remove("L", hasLeaflets);
//		p.specials.put("L", hasLeaflets);
//	}
//	
//	public void setHasCoronaries(Phantom p, boolean hasCoronairies) {
//		if (p.specials.containsKey("C"))
//			if ((boolean)p.specials.get("C") == true)
//				p.specials.remove("C", hasCoronairies);
//		p.specials.put("L", hasCoronairies);
//	}
//		
//	public boolean getHasLeaflets(Phantom p) {
//		if (p.specials.containsKey("L"))
//			if ((boolean)p.specials.get("L") == true)
//				return true;
//		return false;
//	}
//	
//	public boolean getHasCoronaries(Phantom p) {
//		if (p.specials.containsKey("C"))
//			if ((boolean)p.specials.get("C") == true)
//				return true;
//		return false;
//	}
//	
//	@Override
//	public boolean equals(Object o) {
//		if (o instanceof PhantomDatabase) {
//			PhantomDatabase base = (PhantomDatabase)o;
//			List<Phantom> thisP = this.getPhantoms();
//			List<Phantom> thatP = base.getPhantoms();
//			if ( thisP.size() != thatP.size())
//				return false;
//			for (int i=0; i<thisP.size(); i++)
//				if (!thisP.get(i).equals(thatP.get(i)))
//					return false;
//			return true;
//		}
//		return false;
//	}

}
