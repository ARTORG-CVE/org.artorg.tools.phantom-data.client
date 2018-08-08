package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class LiteratureBaseTable extends StageTable<LiteratureBaseTable, LiteratureBase, Integer> {
	
	@Override
	public List<TableColumn<LiteratureBase, ?>> createColumns() {
		List<TableColumn<LiteratureBase, ?>> columns = new ArrayList<TableColumn<LiteratureBase, ?>>();
		
		TableColumn<LiteratureBase, String> idCol = new TableColumn<LiteratureBase, String>("id");
		TableColumn<LiteratureBase, String> shortcutCol = new TableColumn<LiteratureBase, String>("shortcut");
	    TableColumn<LiteratureBase, String> valueCol = new TableColumn<LiteratureBase, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLiteratureBase())));
	    
	    columns.add(idCol);
	    columns.add(shortcutCol);
	    columns.add(valueCol);
	    
	    return columns;
	}
	
	@Override
	public HttpDatabaseCrud<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}

}
