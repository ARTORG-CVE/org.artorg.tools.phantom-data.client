package org.artorg.tools.phantomData.client.column;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AbstractColumn<ITEM, E> {
	private ObservableList<ITEM> items;
	private boolean visible;
	private boolean editable;
	private boolean filterable;
	private boolean idColumn;
	private final String columnName;
	private Function<ITEM,E> valueGetter;
	private BiConsumer<ITEM,E> valueSetter;

	{
		visible = true;
		editable = true;
		filterable = true;
		idColumn = false;
		items = FXCollections.observableArrayList();
	}
	
	public AbstractColumn(String columnName) {
		this.columnName = columnName;
	}
	
	public abstract <U extends DbPersistent<U,?>> boolean update(ITEM item);
	
	public List<E> getValues() {
		return getItems().stream().map(item -> get(item)).collect(Collectors.toList());
	}
	
	public E get(ITEM item) {
		return valueGetter.apply(item);
	}
	
	public boolean contains(ITEM item) {
		try {
			valueGetter.apply(item);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	public boolean contains(String value) {
		return getItems().stream().filter(item -> get(item).equals(value))
				.findFirst().isPresent();
	}
	
	@SuppressWarnings("unchecked")
	public void set(ITEM item, Object value) {
		valueSetter.accept(item, (E)value);
	}
	
	// Getters & Setters
	public Function<ITEM, E> getValueGetter() {
		return valueGetter;
	}

	public void setValueGetter(Function<ITEM, E> valueGetter) {
		this.valueGetter = valueGetter;
	}

	public BiConsumer<ITEM, E> getValueSetter() {
		return valueSetter;
	}

	public void setValueSetter(BiConsumer<ITEM, E> valueSetter) {
		this.valueSetter = valueSetter;
	}
	
	public ObservableList<ITEM> getItems() {
		return items;
	}
	
	public void setItems(ObservableList<ITEM> items) {
		this.items = items;
	}
	
	public String getName() {
		return columnName;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	
	public boolean isIdColumn() {
		return idColumn;
	}
	
	public void setIdColumn(boolean idColumn) {
		this.idColumn = idColumn;
	}

}
