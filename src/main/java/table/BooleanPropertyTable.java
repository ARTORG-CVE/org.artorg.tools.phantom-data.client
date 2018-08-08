package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import specification.Table;

public class BooleanPropertyTable implements Table<BooleanPropertyTable, BooleanProperty> {
	
	private Set<BooleanProperty> booleanProperties;
	
	{
		booleanProperties = new HashSet<BooleanProperty>();
		booleanProperties.addAll(BooleanPropertyConnector.get().readAllAsSet());
	}

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
	public Set<BooleanProperty> getItems() {
		return booleanProperties;
	}
	

}
