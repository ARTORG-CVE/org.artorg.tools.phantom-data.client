package table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.FabricationTypeConnector;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class FabricationTypeTable implements Table<FabricationTypeTable, FabricationType>{

	private Set<FabricationType> fabricationTypes;
	
	{
		fabricationTypes = new HashSet<FabricationType>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TableView<FabricationType> createTableView(TableView<FabricationType> table) {
		TableColumn<FabricationType, String> idCol = new TableColumn<FabricationType, String>("id");
		TableColumn<FabricationType, String> shortcutCol = new TableColumn<FabricationType, String>("shortcut");
	    TableColumn<FabricationType, String> valueCol = new TableColumn<FabricationType, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFabricationType())));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, shortcutCol, valueCol);
		
		fabricationTypes.addAll(FabricationTypeConnector.get().readAllAsSet());
	    ObservableList<FabricationType> data = FXCollections.observableArrayList(fabricationTypes);
	    table.setItems(data);
		return table;
	}

}
