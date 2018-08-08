package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.client.specification.Table;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PropertyFieldTable extends StageTable<PropertyFieldTable,PropertyField, Integer> {
	
	public static Collection<TableColumn<PropertyField,?>> createUpperTableColumns() {
		List<TableColumn<PropertyField,?>> list = new ArrayList<TableColumn<PropertyField,?>>();
		TableColumn<PropertyField, String> descriptionCol = new TableColumn<PropertyField, String>("description");
		descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDescription())));
		list.add(descriptionCol);
		return list;
	}

	@Override
	public List<TableColumn<PropertyField, ?>> createColumns() {
		List<TableColumn<PropertyField, ?>> columns = new ArrayList<TableColumn<PropertyField, ?>>();
		
		TableColumn<PropertyField, String> idCol = new TableColumn<PropertyField, String>("id");
		TableColumn<PropertyField, String> nameCol = new TableColumn<PropertyField, String>("name");
	    TableColumn<PropertyField, String> descriptionCol = new TableColumn<PropertyField, String>("description");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getName())));
	    descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDescription())));
	    
	    columns.add(idCol);
	    columns.add(nameCol);
	    columns.add(descriptionCol);
	    
		return columns;
	}
	
	@Override
	public HttpDatabaseCrud<PropertyField, Integer> getConnector() {
		return PropertyFieldConnector.get();
	}

}
