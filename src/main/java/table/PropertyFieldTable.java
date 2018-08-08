package table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class PropertyFieldTable implements Table<PropertyFieldTable,PropertyField> {
	
	private Set<PropertyField> propertyFields;
	
	{
		propertyFields = new HashSet<PropertyField>();
		propertyFields.addAll(PropertyFieldConnector.get().readAllAsSet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableView<PropertyField> createTableView(TableView<PropertyField> table) {
		TableColumn<PropertyField, String> idCol = new TableColumn<PropertyField, String>("id");
		TableColumn<PropertyField, String> nameCol = new TableColumn<PropertyField, String>("name");
	    TableColumn<PropertyField, String> descriptionCol = new TableColumn<PropertyField, String>("description");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getName())));
	    descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDescription())));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, nameCol, descriptionCol);
		
		propertyFields.addAll(PropertyFieldConnector.get().readAllAsSet());
	    ObservableList<PropertyField> data = FXCollections.observableArrayList(propertyFields);
	    table.setItems(data);
		return table;
	}
	
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
	public Set<PropertyField> getItems() {
		return propertyFields;
	}

}
