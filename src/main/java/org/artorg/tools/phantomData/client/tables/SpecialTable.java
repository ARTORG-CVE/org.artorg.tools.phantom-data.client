package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class SpecialTable extends StageTable<SpecialTable, Special, Integer> {
	
	@Override
	public HttpDatabaseCrud<Special, Integer> getConnector() {
		return SpecialConnector.get();
	}
	
	@Override
	public List<IColumn<Special, ?, ?>> createColumns() {
		List<IColumn<Special, ?, ?>> columns =
				new ArrayList<IColumn<Special, ?, ?>>();
		columns.add(new Column<Special, Special, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<Special, Special, String, Integer>(
				"shortcut", item -> item, 
				path -> path.getShortcut(), 
				(path,value) -> path.setShortcut(value)));
		
		List<Integer> idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
				.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
				.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
		
		idList.forEach(id -> 
			columns.add(new ColumnOptional<Special, BooleanProperty, String, Integer>(
					"test",
					item -> item.getBooleanProperties().stream()
						.filter(p -> p.getPropertyField().getId() == id).findFirst(),
					path -> String.valueOf(path.getBool()),
					(path,value) -> path.setBool(Boolean.valueOf(value)),
					""
		)));
		
		return columns;
	}
	
	
	public void test() {
		System.out.println("########################## in Special Table start");
		Special s = this.getItems().stream().filter(item -> item.getShortcut().equals("L")).findFirst().get();
		s.getBooleanProperties().stream().filter(p -> p.getId().equals(1)).findFirst().get().setBool(false);
		
		System.out.println(s);
		this.getConnector().update(s);
		System.out.println("########################## in Special Table end");
//		BooleanPropertyConnector.get().update(s.getBooleanProperties().stream().filter(p -> p.getId().equals(1)).findFirst().get());
		
		
		
	}

	
}