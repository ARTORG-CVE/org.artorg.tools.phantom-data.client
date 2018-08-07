package table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class LiteratureBaseTable implements Table<LiteratureBaseTable, LiteratureBase>{

	private Set<LiteratureBase> literatureBases;
	
	{
		literatureBases = new HashSet<LiteratureBase>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TableView<LiteratureBase> createTableView(TableView<LiteratureBase> table) {
		TableColumn<LiteratureBase, String> idCol = new TableColumn<LiteratureBase, String>("id");
		TableColumn<LiteratureBase, String> shortcutCol = new TableColumn<LiteratureBase, String>("shortcut");
	    TableColumn<LiteratureBase, String> valueCol = new TableColumn<LiteratureBase, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLiteratureBase())));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, shortcutCol, valueCol);
		
		literatureBases.addAll(LiteratureBaseConnector.get().readAllAsSet());
	    ObservableList<LiteratureBase> data = FXCollections.observableArrayList(literatureBases);
	    table.setItems(data);
		return table;
	}

}
