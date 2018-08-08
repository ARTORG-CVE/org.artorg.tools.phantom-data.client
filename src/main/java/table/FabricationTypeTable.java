package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.FabricationTypeConnector;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import specification.Table;

public class FabricationTypeTable implements Table<FabricationTypeTable, FabricationType>{

	private Set<FabricationType> fabricationTypes;
	
	{
		fabricationTypes = new HashSet<FabricationType>();
		fabricationTypes.addAll(FabricationTypeConnector.get().readAllAsSet());
	}

	@Override
	public List<TableColumn<FabricationType, ?>> createColumns() {
		List<TableColumn<FabricationType,?>> columns = new ArrayList<TableColumn<FabricationType, ?>>();
		
		TableColumn<FabricationType, String> idCol = new TableColumn<FabricationType, String>("id");
		TableColumn<FabricationType, String> shortcutCol = new TableColumn<FabricationType, String>("shortcut");
	    TableColumn<FabricationType, String> valueCol = new TableColumn<FabricationType, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFabricationType())));
	    
	    columns.add(idCol);
	    columns.add(shortcutCol);
	    columns.add(valueCol);
		
		return columns;
	}

	@Override
	public Set<FabricationType> getItems() {
		return fabricationTypes;
	}

}
