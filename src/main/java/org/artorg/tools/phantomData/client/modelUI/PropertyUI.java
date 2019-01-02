package org.artorg.tools.phantomData.client.modelUI;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;

public abstract class PropertyUI<T extends AbstractProperty<T, VALUE>, VALUE> extends UIEntity<T> {

	public abstract String toString(VALUE value);

	public abstract VALUE fromString(String s);

	public abstract T createProperty(PropertyField propertyField, VALUE value);

	public abstract Node createValueNode();

	public abstract VALUE getValueFromNode(Node valueNode);

	public abstract void setValueToNode(Node valueNode, VALUE value);

	public abstract VALUE getDefaultValue();

	@Override
	public List<AbstractColumn<T, ?>> createColumns(Table<T> table, List<T> items) {
		List<AbstractColumn<T, ?>> columns = new ArrayList<AbstractColumn<T, ?>>();
		ColumnCreator<T, T> editor = new ColumnCreator<>(table);
		columns.add(editor.createFilterColumn("Type", path -> {
			try {
				return Class.forName(path.getPropertyField().getType()).getSimpleName();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return path.getPropertyField().getType();
		}, (path, value) -> {}));
		columns.add(
				editor.createFilterColumn("Field Name", path -> path.getPropertyField().getName(),
						(path, value) -> path.getPropertyField().setName(value)));
		columns.add(editor.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(fromString(value))));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<T> createEditFactory() {
		ItemEditor<T> editor = new ItemEditor<T>(getItemClass());
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane
				.addEntry("Property Field",
						editor.createComboBox(PropertyField.class, item -> item.getPropertyField(),
								(item, value) -> item.setPropertyField(value))
								.setMapper(p -> p.getName()));
		Node node = createValueNode();
		propertyPane.addEntry("Value",
				editor.createNode(item -> item.getValue(), (item, value) -> item.setValue(value),
						item -> getDefaultValue(), node, value -> setValueToNode(node, value),
						() -> getValueFromNode(node),
						() -> setValueToNode(node, getDefaultValue())));
		editor.add(new TitledPropertyPane("General", propertyPane));

		editor.addApplyButton();
		return editor;

	}

}
