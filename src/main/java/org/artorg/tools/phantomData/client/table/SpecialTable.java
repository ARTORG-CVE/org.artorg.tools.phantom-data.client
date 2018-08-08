package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.Table;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class SpecialTable implements Table<SpecialTable, Special>{
	
	private Set<Special> specials;
	
	{
		specials = new HashSet<Special>();
		specials.addAll(SpecialConnector.get().readAllAsSet());
	}

	@Override
	public List<TableColumn<Special, ?>> createColumns() {
		List<TableColumn<Special, ?>> columns = new ArrayList<TableColumn<Special,?>>();

		TableColumn<Special, String> idCol = new TableColumn<Special, String>("id");
		TableColumn<Special, String> shortcutCol = new TableColumn<Special, String>("shortcut");
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String
	    		.valueOf(cellData.getValue().getShortcut())));
	    columns.add(idCol);
	    columns.add(shortcutCol);
	    
	    List<Integer> idList = specials.stream().flatMap(s -> s.getBooleanProperties()
	    		.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
	    		.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
	    int nPropertyCols = idList.size();
	    List<TableColumn<Special, String>> listBoolPropDescriptionCols = new ArrayList<TableColumn<Special, String>>();
	    for (int i=0; i<nPropertyCols; i++) {
	    	String colName = PropertyFieldConnector.get().readById(idList.get(i)).getDescription();
	    	listBoolPropDescriptionCols.add(new TableColumn<Special, String>(colName));
	    	final int temp = i;
	    	listBoolPropDescriptionCols.get(i).setCellValueFactory(cellData -> new SimpleStringProperty(String
	    			.valueOf(cellData.getValue().getBooleanProperties().get(temp).getBool())));
	    	columns.add(listBoolPropDescriptionCols.get(i));
	    }
	    return columns;
	}

	@Override
	public Set<Special> getItems() {
		return specials;
	}

}
