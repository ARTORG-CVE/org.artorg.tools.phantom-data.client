package org.artorg.tools.phantomData.client.modelUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;

public abstract class PropertyUI<T extends AbstractProperty<T, VALUE> & DbPersistent<T, UUID>,
		VALUE extends Comparable<VALUE>> implements UIEntity<T> {

	protected abstract String toString(VALUE value);

	protected abstract VALUE fromString(String s);

	protected abstract T createProperty(PropertyField propertyField, VALUE value);

	protected abstract Node createValueNode();

	protected abstract VALUE getValueFromNode(Node valueNode);

	protected abstract void setValueToNode(Node valueNode, VALUE value);

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
		ColumnUtils.createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<T> createEditFactory() {
		return new PropertyEditFactoryController();
	}

	public class PropertyEditFactoryController extends GroupedItemEditFactoryController<T> {
		private ComboBox<PropertyField> comboBoxPropertyField;
		private Node valueNode;

		{
			comboBoxPropertyField = new ComboBox<PropertyField>();
			valueNode = createValueNode();

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			FxUtil.createDbComboBox(comboBoxPropertyField,
					Connectors.getConnector(PropertyField.class), d -> String.valueOf(d.getName()));
			generalProperties.add(new PropertyEntry("Property Field", comboBoxPropertyField));
			generalProperties.add(new PropertyEntry("Value", valueNode));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		public T createItem() {
			PropertyField propertyField =
					comboBoxPropertyField.getSelectionModel().getSelectedItem();

			VALUE value = getValueFromNode(valueNode);
			return createProperty(propertyField, value);
		}

		@Override
		protected void setEditTemplate(T item) {
			super.selectComboBoxItem(comboBoxPropertyField, item.getPropertyField());
			setValueToNode(valueNode, item.getValue());
		}

		@Override
		protected void applyChanges(T item) {
			PropertyField propertyField =
					comboBoxPropertyField.getSelectionModel().getSelectedItem();
			VALUE value = getValueFromNode(valueNode);

			item.setPropertyField(propertyField);
			item.setValue(value);
		}

	}

}
