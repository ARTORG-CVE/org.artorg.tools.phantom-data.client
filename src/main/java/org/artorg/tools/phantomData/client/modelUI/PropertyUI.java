package org.artorg.tools.phantomData.client.modelUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.editor2.ItemEditor;
import org.artorg.tools.phantomData.client.editor2.PropertyNode;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public abstract class PropertyUI<
	T extends AbstractProperty<T, VALUE> & DbPersistent<T, UUID>,
	VALUE extends Comparable<VALUE>> extends UIEntity<T> {

	protected abstract String toString(VALUE value);

	protected abstract VALUE fromString(String s);

	protected abstract T createProperty(PropertyField propertyField, VALUE value);

	protected abstract Node createValueNode();

	protected abstract VALUE getValueFromNode(Node valueNode);

	protected abstract void setValueToNode(Node valueNode, VALUE value);

	protected abstract VALUE getDefaultValue();

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
		columns.add(creator.createFilterColumn("Field Name",
			path -> path.getPropertyField().getName(),
			(path, value) -> path.getPropertyField().setName(value)));
		columns.add(
			creator.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
				(path, value) -> path.setValue(fromString(value))));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public FxFactory<T> createEditFactory() {
		ItemEditor<T> creator = new ItemEditor<>(getItemClass());
		VBox vBox = new VBox();
		PropertyNode<T, ?> propertyNode;

		List<PropertyEntry> generalProperties = new ArrayList<>();
		propertyNode = creator.createComboBox(PropertyField.class).of(
			(item, value) -> item.setPropertyField(value),
			item -> item.getPropertyField(), p -> p.getName());
		generalProperties
			.add(new PropertyEntry("Property Field", propertyNode.getNode()));

		Node node = createValueNode();
		propertyNode = creator.createNode((item, value) -> item.setValue(value),
			item -> item.getValue(), item -> getDefaultValue(), node,
			value -> setValueToNode(node, value),
			() -> getValueFromNode(node), () -> setValueToNode(node, getDefaultValue()));
		generalProperties.add(new PropertyEntry("Value", propertyNode.getNode()));
		TitledPropertyPane generalPane =
			new TitledPropertyPane(generalProperties, "General");
		vBox.getChildren().add(generalPane);

		vBox.getChildren().add(creator.createButtonPane(creator.getApplyButton()));

		FxUtil.addToPane(creator, vBox);
		return creator;

	}

}
