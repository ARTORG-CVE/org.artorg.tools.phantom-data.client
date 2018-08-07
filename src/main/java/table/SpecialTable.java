package table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.server.connector.SpecialConnector;
import org.artorg.tools.phantomData.server.model.Special;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class SpecialTable implements Table<SpecialTable, Special>{
	
	private Set<Special> specials;
	
	{
		specials = new HashSet<Special>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableView<Special> createTableView(TableView<Special> table) {
		TableColumn<Special, String> idCol = new TableColumn<Special, String>("id");
		TableColumn<Special, String> shortcutCol = new TableColumn<Special, String>("shortcut");
	    TableColumn<Special, String> valueCol = new TableColumn<Special, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBooleanProperties()
	    		.stream().map(p -> p.toString()).collect(Collectors.joining(", ", "[", "]")))));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, shortcutCol, valueCol);
		
		specials.addAll(SpecialConnector.get().readAllAsSet());
	    ObservableList<Special> data = FXCollections.observableArrayList(specials);
	    table.setItems(data);
		return table;
	}

}
