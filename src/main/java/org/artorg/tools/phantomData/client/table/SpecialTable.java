package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class SpecialTable extends StageTable<SpecialTable, Special, Integer> {

	private List<Integer> idList;
	
	private int nPropertyCols;
	
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
	    
	    idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
	    		.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
	    		.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
	    nPropertyCols = idList.size();
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
	public HttpDatabaseCrud<Special, Integer> getConnector() {
		return SpecialConnector.get();
	}

	@Override
	public Object getValue(Special item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getShortcut();
		}
		return item.getBooleanProperties().get(idList.get(col)-2).getBool();
	}

	@Override
	public void setValue(Special item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setShortcut((String) value); break;
		}
		item.getBooleanProperties().get(idList.get(col)-2).getBool();
	}
	
	@Override
	public List<String> getColumnNames() {
		List<String> colNames = Arrays.asList("id", "shortcut");
		for (int i=0; i<nPropertyCols; i++) {
			colNames.add(PropertyFieldConnector.get().readById(idList.get(i)).getDescription());
		}
		return colNames;
	}

}
