package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class AnnulusDiameterTable extends StageTable<AnnulusDiameterTable, AnnulusDiameter, Integer> {

	@Override
	public List<TableColumn<AnnulusDiameter, ?>> createColumns() {
		List<TableColumn<AnnulusDiameter,?>> columns = new ArrayList<TableColumn<AnnulusDiameter,?>>();
		
		TableColumn<AnnulusDiameter, String> idCol = new TableColumn<AnnulusDiameter, String>("id");
		TableColumn<AnnulusDiameter, String> shortcutCol = new TableColumn<AnnulusDiameter, String>("shortcut");
	    TableColumn<AnnulusDiameter, String> valueCol = new TableColumn<AnnulusDiameter, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue())));
	    
		columns.add(idCol);
		columns.add(shortcutCol);
		columns.add(valueCol);
		
	    return columns;
	}
	
	@Override
	public HttpDatabaseCrud<AnnulusDiameter, Integer> getConnector() {
		return AnnulusDiameterConnector.get();
	}

}
