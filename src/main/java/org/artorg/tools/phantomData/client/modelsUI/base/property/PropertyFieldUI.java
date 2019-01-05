package org.artorg.tools.phantomData.client.modelsUI.base.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.SelectableLabel;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
		columns.add(creator.createFilterColumn("Entity Type", path -> path.getEntityType()));
		columns.add(creator.createFilterColumn("Property Type", path -> path.getPropertyType()));
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName((String) value)));
		columns.add(creator.createFilterColumn("Description", path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
		createPersonifiedColumns(table, columns);

		return columns;
	}

	@Override
	public ItemEditor<PropertyField> createEditFactory() {
		AnchorPane entityTypePane = new AnchorPane();
		SelectableLabel entityTypeLabel = new SelectableLabel();
		ComboBox<String> comboBoxEntityType = new ComboBox<>();
		Collection<String> entityTypes = Main.getPropertifiedclasses().stream()
				.map(cls -> cls.getSimpleName()).collect(Collectors.toList());
		comboBoxEntityType.setItems(FXCollections.observableArrayList(entityTypes));
		FxUtil.setComboBoxCellFactory(comboBoxEntityType, s -> s);

		AnchorPane propertyTypePane = new AnchorPane();
		SelectableLabel propertyTypeLabel = new SelectableLabel();
		ComboBox<String> comboBoxPropertyType = new ComboBox<>();
		Collection<String> propertyTypes = Main.getPropertyclasses().stream()
				.map(cls -> cls.getSimpleName()).collect(Collectors.toList());
		comboBoxPropertyType.setItems(FXCollections.observableArrayList(propertyTypes));
		FxUtil.setComboBoxCellFactory(comboBoxPropertyType, s -> s);

		ItemEditor<PropertyField> editor = new ItemEditor<PropertyField>(getItemClass()) {
			@Override
			public void onShowingCreateMode(Class<? extends PropertyField> beanClass) {
				entityTypePane.getChildren().clear();
				FxUtil.addToPane(entityTypePane, comboBoxEntityType);

				propertyTypePane.getChildren().clear();
				FxUtil.addToPane(propertyTypePane, comboBoxPropertyType);
			}

			@Override
			public void onShowingEditMode(PropertyField item) {
				entityTypePane.getChildren().clear();
				FxUtil.addToPane(entityTypePane, entityTypeLabel);
				entityTypeLabel.setText(item.getEntityType());

				propertyTypePane.getChildren().clear();
				FxUtil.addToPane(propertyTypePane, propertyTypeLabel);
				propertyTypeLabel.setText(item.getPropertyType());
			}

			@Override
			public void onCreatingClient(PropertyField item) throws InvalidUIInputException {
				item.setEntityType(comboBoxEntityType.getSelectionModel().getSelectedItem());
			}
		};
		PropertyGridPane propertyPane = new PropertyGridPane();
		propertyPane.addPropertyNode(editor.create(comboBoxEntityType, item -> item.getEntityType(),
				(item, value) -> item.setEntityType(value)));
		propertyPane.addEntry(new Label("Entity Type"), entityTypePane);
		propertyPane.addPropertyNode(editor.create(comboBoxPropertyType, item -> item.getPropertyType(),
				(item, value) -> item.setPropertyType(value)));
		propertyPane.addEntry(new Label("Entity Type"), propertyTypePane);
		propertyPane.addEntry("Name", editor.createTextField(item -> item.getName(),
				(item, value) -> item.setName(value)));
		propertyPane.addEntry("Description", editor.createTextField(item -> item.getDescription(),
				(item, value) -> item.setDescription(value)));
		editor.add(new TitledPropertyPane("General", propertyPane));
		editor.addApplyButton();
		return editor;
	}

}
