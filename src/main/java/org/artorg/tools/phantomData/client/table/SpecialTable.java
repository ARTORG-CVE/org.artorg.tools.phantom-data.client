package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialTable extends StageTable<SpecialTable, Special, Integer> {

	private List<Integer> idList;
	
	private int nPropertyCols;
	
	{
	    idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
		.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
		.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
	}
	
	@Override
	public HttpDatabaseCrud<Special, Integer> getConnector() {
		return SpecialConnector.get();
	}
	
	@Override
	public List<String> createColumnNames() {
		List<String> colNames = Arrays.asList("id", "shortcut");
		for (int i=0; i<nPropertyCols; i++) {
			colNames.add(PropertyFieldConnector.get().readById(idList.get(i)).getDescription());
		}
		return colNames;
	}

	@Override
	public List<PropertyUndoable<Special, Object>> createProperties() {
		List<PropertyUndoable<Special, Object>> properties = 
				new ArrayList<PropertyUndoable<Special, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setShortcut((String) o), 
				i -> i.getShortcut()));
		
		idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
				.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
				.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
		
		for (int i=0; i<idList.size(); i++) {
			final int j = i;
			
			try {
				BiConsumer<Special,Object> setter = (item, o) -> 
					item.getBooleanProperties().stream()
					.filter(p -> p.getId() == idList.get(j)).findFirst().get()
					.getPropertyField().setDescription((String) o);
				Function<Special,Object> getter = (item) -> 
					item.getBooleanProperties().stream()
					.filter(p -> p.getId() == idList.get(j)).findFirst().get()
					.getPropertyField().getDescription();
				properties.add(createProperty(setter, getter));
			} catch (NoSuchElementException e) {} 
			
		}
		for (int i=0; i<idList.size(); i++) {
			final int j = i;
			try {
			properties.add(createProperty(
					(item,o) -> item.getBooleanProperties().stream()
						.filter(p -> p.getId() == idList.get(j)).findFirst().get()
						.setBool((Boolean) o), 
					item -> item.getBooleanProperties().stream()
						.filter(p -> p.getId() == idList.get(j)).findFirst().get()
						.getBool()));
			} catch (NoSuchElementException e) {}
		}
		
		return properties;
	}
	
//	@Override
//	public List<TableColumn<Special, ?>> createColumns() {
//		List<TableColumn<Special, ?>> columns = new ArrayList<TableColumn<Special,?>>();
//
//		TableColumn<Special, String> idCol = new TableColumn<Special, String>("id");
//		TableColumn<Special, String> shortcutCol = new TableColumn<Special, String>("shortcut");
//	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
//	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String
//	    		.valueOf(cellData.getValue().getShortcut())));
//	    columns.add(idCol);
//	    columns.add(shortcutCol);
//	    
//	    idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
//	    		.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
//	    		.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
//	    nPropertyCols = idList.size();
//	    List<TableColumn<Special, String>> listBoolPropDescriptionCols = new ArrayList<TableColumn<Special, String>>();
//	    for (int i=0; i<nPropertyCols; i++) {
//	    	String colName = PropertyFieldConnector.get().readById(idList.get(i)).getDescription();
//	    	listBoolPropDescriptionCols.add(new TableColumn<Special, String>(colName));
//	    	final int temp = i;
//	    	listBoolPropDescriptionCols.get(i).setCellValueFactory(cellData -> new SimpleStringProperty(String
//	    			.valueOf(cellData.getValue().getBooleanProperties().get(temp).getBool())));
//	    	columns.add(listBoolPropDescriptionCols.get(i));
//	    }
//	    return columns;
//	}

}
