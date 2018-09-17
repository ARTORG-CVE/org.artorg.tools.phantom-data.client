package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;

public class PhantomFilterTable extends FilterTableSpringDb<Phantom, Integer> {

	{
		this.setConnector(PhantomConnector.get());
	}

	@Override
	public List<IColumn<Phantom, ?>> createColumns() {
		List<IColumn<Phantom, ?>> columns =
				new ArrayList<IColumn<Phantom, ?>>();
		IColumn<Phantom,?> column;
		column = new Column<Phantom, Phantom, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				PhantomConnector.get());
		column.setVisibility(false);
		columns.add(column);
		columns.add(new Column<Phantom, Phantom, Integer>(
				"PID", item -> item, 
				path -> path.getProductId(), 
				(path,value) -> path.setProductId(value),
				PhantomConnector.get()));
		columns.add(new Column<Phantom, AnnulusDiameter, Integer>(
				"annulus [mm]", item -> item.getAnnulusDiameter(), 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Double.valueOf(value)),
				AnnulusDiameterConnector.get()));
		columns.add(new Column<Phantom, FabricationType, Integer>(
				"type", item -> item.getFabricationType(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value),
				FabricationTypeConnector.get()));
		columns.add(new Column<Phantom, LiteratureBase, Integer>(
				"literature", item -> item.getLiteratureBase(), 
				path -> path.getValue(), 
				(path,value) -> path.setValue(value),
				LiteratureBaseConnector.get()));
		column = new Column<Phantom, Special, Integer>(
				"special", item -> item.getSpecial(), 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value),
				SpecialConnector.get());
		columns.add(column);
		columns.add(new Column<Phantom, Phantom, Integer>(
				"number", item -> item, 
				path -> String.valueOf(path.getNumber()), 
				(path,value) -> path.setNumber(Integer.valueOf(value)),
				PhantomConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Phantoms";
	}
	
	
	
//	public boolean contains(int id) {
//	return phantoms.stream().filter(p -> p.getId().intValue()==id)
//			.findFirst().isPresent();
//}

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
