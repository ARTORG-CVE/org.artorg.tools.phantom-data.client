package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import specification.Table;

public class AnnulusDiameterTable implements Table<AnnulusDiameterTable, AnnulusDiameter> {
	
	private Set<AnnulusDiameter> annulusDiameters;
	
	{
		annulusDiameters = new HashSet<AnnulusDiameter>();
		annulusDiameters.addAll(AnnulusDiameterConnector.get().readAllAsSet());
	}

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
	public Set<AnnulusDiameter> getItems() {
		return annulusDiameters;
	}

}
