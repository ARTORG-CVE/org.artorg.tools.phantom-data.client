package table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class BooleanPropertyTable implements Table<BooleanPropertyTable, BooleanProperty> {
	
	private Set<BooleanProperty> booleanProperties;
	
	{
		booleanProperties = new HashSet<BooleanProperty>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableView<BooleanProperty> createTableView(TableView<BooleanProperty> table) {
		TableColumn<BooleanProperty, String> idCol = new TableColumn<BooleanProperty, String>("id");
		TableColumn<BooleanProperty, String> propertyFieldCol = new TableColumn<BooleanProperty, String>("property field");
	    TableColumn<BooleanProperty, String> boolCol = new TableColumn<BooleanProperty, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    propertyFieldCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPropertyField().getDescription())));
	    boolCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBool())));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, propertyFieldCol, boolCol);
		
		booleanProperties.addAll(BooleanPropertyConnector.get().readAllAsSet());
	    ObservableList<BooleanProperty> data = FXCollections.observableArrayList(booleanProperties);
	    table.setItems(data);
	    table.getSortOrder().addAll(idCol, propertyFieldCol);
		return table;
	}
	
	public static Collection<TableColumn<BooleanProperty,?>> createUpperTableColumns() {
		List<TableColumn<BooleanProperty,?>> list = new ArrayList<TableColumn<BooleanProperty,?>>();
		
		
		
		
		return list;
	}
	

}
