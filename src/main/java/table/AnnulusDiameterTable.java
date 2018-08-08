package table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class AnnulusDiameterTable implements Table<AnnulusDiameterTable, AnnulusDiameter> {
	
	private Set<AnnulusDiameter> annulusDiameters;
	
	{
		annulusDiameters = new HashSet<AnnulusDiameter>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableView<AnnulusDiameter> createTableView(TableView<AnnulusDiameter> table) {
		TableColumn<AnnulusDiameter, String> idCol = new TableColumn<AnnulusDiameter, String>("id");
		TableColumn<AnnulusDiameter, String> shortcutCol = new TableColumn<AnnulusDiameter, String>("shortcut");
	    TableColumn<AnnulusDiameter, String> valueCol = new TableColumn<AnnulusDiameter, String>("value");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    shortcutCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getShortcut())));
	    valueCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue())));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, shortcutCol, valueCol);
		
		annulusDiameters.addAll(AnnulusDiameterConnector.get().readAllAsSet());
	    ObservableList<AnnulusDiameter> data = FXCollections.observableArrayList(annulusDiameters);
	    table.setItems(data);
	    table.getSortOrder().addAll(idCol, shortcutCol);
		return table;
	}

}
