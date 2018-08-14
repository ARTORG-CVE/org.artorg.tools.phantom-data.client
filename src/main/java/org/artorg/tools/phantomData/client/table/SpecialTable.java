package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.Optional;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.PropertySheet.Item;

public class SpecialTable extends StageTable<SpecialTable, Special, Integer> {
	
	@Override
	public HttpDatabaseCrud<Special, Integer> getConnector() {
		return SpecialConnector.get();
	}
	
	@Override
	public List<String> createColumnNames() {
		List<String> colNames = new ArrayList<String>();
		colNames.addAll(Arrays.asList("id", "shortcut"));
		List<Integer> idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
				.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
				.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
		int nPropertyCols = idList.size();
		for (int i=0; i<nPropertyCols; i++) 
			colNames.add(PropertyFieldConnector.get().readById(idList.get(i)).getDescription());
		return colNames;
	}

	@Override
	public List<PropertyUndoable<Special, Integer, Object>> createProperties() {
		List<PropertyUndoable<Special, Integer, Object>> properties = 
				new ArrayList<PropertyUndoable<Special, Integer, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setShortcut((String) o), 
				i -> i.getShortcut()));
		
		List<Integer> idList = getItems().stream().flatMap(s -> s.getBooleanProperties()
				.stream()).mapToInt(p -> p.getPropertyField().getId()).distinct().sorted()
				.collect(() -> new ArrayList<Integer>(), (set, e) -> set.add(e), (e1, e2) -> {});
		
		for (int i=0; i<idList.size(); i++) {
			final int j = i;
			BiConsumer<Special,Object> setter = (item, o) ->
				item.getBooleanProperties().stream()
				.filter(p -> p.getId() == idList.get(j)).findFirst()
				.ifPresent(p -> p.setBool(Boolean.valueOf((String) o)));
			Function<Special,Object> getter = (item) -> { 
				Optional<BooleanProperty> result = item.getBooleanProperties().stream()
						.filter(p -> p.getPropertyField().getId() == idList.get(j)).findFirst();
				if (result.isPresent()) return result.get().getBool();
				return "";
			};
			properties.add(createProperty(setter, getter));
		}
		
		return properties;
	}
	
	@Override
	public List<Column<Special, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<Special, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<Special, ? extends DatabasePersistent<?, ?>, ?, ?>>();
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
		
		for (int i=0; i<idList.size(); i++) {
			final int j = i;
			BiConsumer<Special,Object> setter = (item, o) ->
				item.getBooleanProperties().stream()
				.filter(p -> p.getId() == idList.get(j)).findFirst()
				.ifPresent(p -> p.setBool(Boolean.valueOf((String) o)));
			Function<BooleanProperty,Boolean> getter = (path) -> { 
				Optional<BooleanProperty> result = item.getBooleanProperties().stream()
						.filter(p -> p.getPropertyField().getId() == idList.get(j)).findFirst();
				if (result.isPresent()) return result.get().getBool();
				return "";
			};
			columns.add(new Column<Special, Optional<BooleanProperty>, String, Integer>(
					item -> item.getBooleanProperties().stream()
						.filter(p -> p.getPropertyField().getId() == idList.get(j)).findFirst()
					,
					path -> {
						if (path.isPresent()) return String.valueOf(path.get().getBool());
						return "";
					},
					(path,value) -> {
						if (path.isPresent()) path.get().setBool(Boolean.valueOf(value));
					},
					"test"
					));
			
			properties.add(createProperty(setter, getter));
		}
		
		
		
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
