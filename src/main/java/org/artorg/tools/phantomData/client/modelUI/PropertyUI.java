package org.artorg.tools.phantomData.client.modelUI;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public abstract class PropertyUI<T extends AbstractProperty<T, VALUE>,
		VALUE> extends UIEntity<T> {

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
		ColumnCreator<T, T> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Type", path -> {
			try {
				return Class.forName(path.getPropertyField().getType()).getSimpleName();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return path.getPropertyField().getType();
		}, (path, value) -> {}));
		columns.add(
				creator.createFilterColumn("Field Name", path -> path.getPropertyField().getName(),
						(path, value) -> path.getPropertyField().setName(value)));
		columns.add(creator.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(fromString(value))));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<T> createEditFactory() {
		ItemEditor<T> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();

		List<PropertyEntry> entries = new ArrayList<>();
		creator.createComboBox(PropertyField.class)
				.of(item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value))
				.setMapper(p -> p.getName()).addLabeled("Property field", entries);

		Node node = createValueNode();
		creator.createNode((item, value) -> item.setValue(value), item -> item.getValue(),
				item -> getDefaultValue(), node, value -> setValueToNode(node, value),
				() -> getValueFromNode(node), () -> setValueToNode(node, getDefaultValue()))
				.addLabeled("Value", entries);
		TitledPane generalPane = creator.createTitledPane(entries, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;

	}

}
