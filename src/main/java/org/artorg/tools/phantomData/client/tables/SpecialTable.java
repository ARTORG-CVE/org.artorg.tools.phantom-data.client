package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class SpecialTable extends StageTable<SpecialTable, Special, Integer> {
	
	{
		this.setConnector(SpecialConnector.get());
	}
	
	@Override
	public List<IColumn<Special, ?, ?>> createColumns() {
		List<IColumn<Special, ?, ?>> columns =
				new ArrayList<IColumn<Special, ?, ?>>();
		columns.add(new Column<Special, Special, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId((Integer) value),
				SpecialConnector.get()));
		columns.add(new Column<Special, Special, String, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut((String) value),
				SpecialConnector.get()));
		
		List<Integer> idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
				.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
				.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
		
		idList.forEach(id -> 
			columns.add(new ColumnOptional<Special, BooleanProperty, String, Integer>(
					"test",
					item -> item.getBooleanProperties().stream()
						.filter(p -> p.getPropertyField().getId() == id).findFirst(),
					path -> String.valueOf(path.getBool()),
					(path,value) -> path.setBool((Boolean) value),
					"",
					BooleanPropertyConnector.get()
		)));
		
		return columns;
	}
	
}
