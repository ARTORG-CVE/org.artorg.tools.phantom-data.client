package org.artorg.tools.phantomData.client.modelsUI.base.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PropertyFieldUI extends UIEntity<PropertyField> {

	public Class<PropertyField> getItemClass() {
		return PropertyField.class;
	}

	@Override
	public String getTableName() {
		return "Property Fields";
	}

	@Override
	public List<AbstractColumn<PropertyField, ?>> createColumns(Table<PropertyField> table,
			List<PropertyField> items) {
		List<AbstractColumn<PropertyField, ?>> columns = new ArrayList<>();
		ColumnCreator<PropertyField, PropertyField> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Type", path -> {
			try {
				return Class.forName(path.getType()).getSimpleName();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return path.getType();
		}));
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName((String) value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
		createPersonifiedColumns(table, columns);

		return columns;
	}

	@Override
	public ItemEditor<PropertyField> createEditFactory() {
		AnchorPane typePane = new AnchorPane();
		ComboBox<Class<?>> comboBoxType = new ComboBox<>();
		TextField textFieldType = new TextField();
		textFieldType.setDisable(true);
		Collection<Class<?>> parentItemClasses = Main.getEntityClasses();
		comboBoxType.setItems(FXCollections.observableArrayList(parentItemClasses));
		FxUtil.setComboBoxCellFactory(comboBoxType, (Class<?> c) -> c.getSimpleName());

		ItemEditor<PropertyField> editor = new ItemEditor<PropertyField>(getItemClass()) {
			@Override
			public void onShowingCreateMode(PropertyField item) {
				typePane.getChildren().clear();
				FxUtil.addToPane(typePane, comboBoxType);
			}

			@Override
			public void onShowingEditMode(PropertyField item) {
				typePane.getChildren().clear();
				FxUtil.addToPane(typePane, textFieldType);
				textFieldType.setText(item.getType());
			}

		};
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.addEntry("Description", editor.createTextField(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)));
		propertyPane.addEntry(new Label("Type"), typePane);
		propertyPane.autosizeColumnWidths();
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;
	}

}
