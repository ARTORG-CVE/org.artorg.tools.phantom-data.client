package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.client.specification.Table;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class BooleanPropertyTable extends StageTable<BooleanPropertyTable, BooleanProperty, Integer> {

	@Override
	public List<TableColumn<BooleanProperty, ?>> createColumns() {
		List<TableColumn<BooleanProperty,?>> columns = new ArrayList<TableColumn<BooleanProperty,?>>();
		
		TableColumn<BooleanProperty, String> idCol = new TableColumn<BooleanProperty, String>("id");
		TableColumn<BooleanProperty, String> propertyFieldCol = new TableColumn<BooleanProperty, String>("property field");
	    TableColumn<BooleanProperty, String> boolCol = new TableColumn<BooleanProperty, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    propertyFieldCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPropertyField().getDescription())));
	    boolCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBool())));
	    
	    columns.add(idCol);
	    columns.add(propertyFieldCol);
	    columns.add(boolCol);
		
		return columns;
	}

	@Override
	public HttpDatabaseCrud<BooleanProperty, Integer> getConnector() {
		return BooleanPropertyConnector.get();
	}
	

}
