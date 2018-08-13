package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.PhantomConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends StageTable<PhantomTable, Phantom, Integer> {
	


//	public boolean contains(int id) {
//		return phantoms.stream().filter(p -> p.getId().intValue()==id)
//				.findFirst().isPresent();
//	}

	
	@Override
	public HttpDatabaseCrud<Phantom, Integer> getConnector() {
		return PhantomConnector.get();
	}
	
	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("annulus [mm]", "type", "literature", "specials", "number");
	}

	@Override
	public List<PropertyUndoable<Phantom, Integer, Object>> createProperties() {
		List<PropertyUndoable<Phantom, Integer, Object>> properties = 
				new ArrayList<PropertyUndoable<Phantom, Integer, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.getAnnulusDiameter().setValue((Double) o), 
				i -> i.getAnnulusDiameter().getValue()));
		properties.add(createProperty(
				(i,o) -> i.getFabricationType().setValue((String) o), 
				i -> i.getFabricationType().getValue()));
		properties.add(createProperty(
				(i,o) -> i.getLiteratureBase().setValue((String) o), 
				i -> i.getLiteratureBase().getValue()));
		properties.add(createProperty(
				(i,o) -> i.getSpecial().setShortcut((String) o), 
				i -> i.getSpecial().getShortcut()));
		properties.add(createProperty(
				(i,o) -> i.setNumber((Integer) o), 
				i -> i.getNumber()));
		return properties;
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
