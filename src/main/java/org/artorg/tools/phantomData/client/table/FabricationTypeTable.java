package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.client.connector.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class FabricationTypeTable extends StageTable<FabricationTypeTable, FabricationType, Integer> {

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
	public HttpDatabaseCrud<FabricationType, Integer> getConnector() {
		return FabricationTypeConnector.get();
	}

}
