package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import specification.Table;

public class LiteratureBaseTable implements Table<LiteratureBaseTable, LiteratureBase>{

	private Set<LiteratureBase> literatureBases;
	
	{
		literatureBases = new HashSet<LiteratureBase>();
		literatureBases.addAll(LiteratureBaseConnector.get().readAllAsSet());
	}
	
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
	public Set<LiteratureBase> getItems() {
		return literatureBases;
	}

}
