package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.SpecialConnector;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

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
				.ifPresent(p -> p.setBool((Boolean) o));
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
}
