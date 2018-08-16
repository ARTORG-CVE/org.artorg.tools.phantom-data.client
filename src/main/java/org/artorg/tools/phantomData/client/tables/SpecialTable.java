package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class SpecialTable extends Table<SpecialTable, Special, Integer> {

	{
		this.setConnector(SpecialConnector.get());
	}

	@Override
	public List<IColumn<Special, ?>> createColumns() {
		List<IColumn<Special, ?>> columns = new ArrayList<IColumn<Special, ?>>();
		columns.add(new Column<Special, Special, Integer>("id", item -> item, path -> String.valueOf(path.getId()),
				(path, value) -> path.setId(Integer.valueOf((String) value)), SpecialConnector.get()));
		columns.add(new Column<Special, Special, Integer>("shortcut", item -> item, path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value), SpecialConnector.get()));

		Map<Integer, String> map = new HashMap<Integer, String>();
		Set<BooleanProperty> set = getItems().stream().flatMap(s -> s.getBooleanProperties().stream())
				.collect(Collectors.toSet());
		set.stream().sorted((p1, p2) -> p1.getPropertyField().getId().compareTo(p2.getPropertyField().getId()))
				.forEach(p -> map.put(p.getPropertyField().getId(), p.getPropertyField().getDescription()));

		map.entrySet().stream()
				.forEach(entry -> columns.add(new ColumnOptional<Special, BooleanProperty, Integer>(entry.getValue(),
						item -> item.getBooleanProperties().stream()
								.filter(p -> p.getPropertyField().getId() == entry.getKey()).findFirst(),
						path -> String.valueOf(path.getBool()),
						(path, value) -> path.setBool(Boolean.valueOf((String) value)), "",
						BooleanPropertyConnector.get())));

		return columns;
	}

}
