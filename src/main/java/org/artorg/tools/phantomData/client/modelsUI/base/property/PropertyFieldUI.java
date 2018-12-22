package org.artorg.tools.phantomData.client.modelsUI.base.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editor.PropertyEntry;
import org.artorg.tools.phantomData.client.editor.TitledPropertyPane;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class PropertyFieldUI implements UIEntity<PropertyField> {

	public Class<PropertyField> getItemClass() {
		return PropertyField.class;
	}

	@Override
	public String getTableName() {
		return "Property Fields";
	}

	@Override
	public List<AbstractColumn<PropertyField, ?>> createColumns(List<PropertyField> items) {
		List<AbstractColumn<PropertyField, ?>> columns = new ArrayList<>();
		ColumnCreator<PropertyField, PropertyField> creator = new ColumnCreator<>(getItemClass());
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
		ColumnUtils.createPersonifiedColumns(getItemClass(), columns);

		return columns;
	}

	@Override
	public ItemEditFactoryController<PropertyField> createEditFactory() {
		return new PropertyFieldEditFactoryController();
	}

	private class PropertyFieldEditFactoryController
			extends GroupedItemEditFactoryController<PropertyField> {
		private TextField textFielName;
		private TextField textFieldDescription;
		private ComboBox<Class<?>> comboBoxParentItemClass;
		private AnchorPane parentItemPane;
		private TextField textFieldParentItemClass;

		{
			textFielName = new TextField();
			textFieldDescription = new TextField();
			comboBoxParentItemClass = new ComboBox<Class<?>>();
			parentItemPane = new AnchorPane();
			textFieldParentItemClass = new TextField();
			textFieldParentItemClass.setDisable(true);
			textFieldParentItemClass.setEditable(false);

			Collection<Class<?>> parentItemClasses = Main.getBeaninfos().getEntityClasses();

			ObservableList<Class<?>> observableParentItemClasses =
					FXCollections.observableArrayList();
			observableParentItemClasses.addAll(parentItemClasses);
			comboBoxParentItemClass.setItems(observableParentItemClasses);
			comboBoxParentItemClass.getSelectionModel().selectFirst();
			Callback<ListView<Class<?>>, ListCell<Class<?>>> cellFactory =
					FxUtil.createComboBoxCellFactory((Class<?> c) -> c.getSimpleName());
			comboBoxParentItemClass.setButtonCell(cellFactory.call(null));
			comboBoxParentItemClass.setCellFactory(cellFactory);

			List<TitledPane> panes = new ArrayList<TitledPane>();
			List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
			generalProperties.add(new PropertyEntry("Name", textFielName));
			generalProperties.add(new PropertyEntry("Description", textFieldDescription));
			FxUtil.addToPane(parentItemPane, comboBoxParentItemClass);
			generalProperties.add(new PropertyEntry("Parent item class", parentItemPane));
			TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
			panes.add(generalPane);
			setTitledPanes(panes);
		}

		@Override
		protected void setEditTemplate(PropertyField item) {
			textFielName.setText(item.getName());
			textFieldDescription.setText(item.getDescription());
			parentItemPane.getChildren().clear();
			FxUtil.addToPane(parentItemPane, textFieldParentItemClass);
			Class<?> type = null;
			try {
				type = Class.forName(item.getType());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			textFieldParentItemClass.setText(type.getSimpleName());
		}

		@Override
		public PropertyField createItem() {
			String name = textFielName.getText();
			String description = textFieldDescription.getText();
			Class<?> parentItemClass =
					comboBoxParentItemClass.getSelectionModel().getSelectedItem();
			return new PropertyField(name, description, parentItemClass);
		}

		@Override
		protected void applyChanges(PropertyField item) {
			String name = textFielName.getText();
			String description = textFieldDescription.getText();
			String type = comboBoxParentItemClass.getSelectionModel().getSelectedItem().getName();

			item.setName(name);
			item.setDescription(description);
			item.setType(type);
		}

	}

}
