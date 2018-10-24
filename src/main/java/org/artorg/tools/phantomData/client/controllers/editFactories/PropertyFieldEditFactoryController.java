package org.artorg.tools.phantomData.client.controllers.editFactories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.controller.GroupedItemEditFactoryController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.client.controller.TitledPropertyPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;

public class PropertyFieldEditFactoryController extends GroupedItemEditFactoryController<PropertyField>{
	private TextField textFielName;
	private TextField textFieldDescription;
	private ComboBox<Class<?>> comboBoxParentItemClass;

	{
		textFielName = new TextField();
		textFieldDescription = new TextField();
		comboBoxParentItemClass = new ComboBox<Class<?>>();
		
		List<Class<?>> parentItemClasses = Arrays.asList(Phantom.class, Special.class);
		ObservableList<Class<?>> observableParentItemClasses = FXCollections.observableArrayList();
		observableParentItemClasses.addAll(parentItemClasses);
		comboBoxParentItemClass.setItems(observableParentItemClasses);
		comboBoxParentItemClass.getSelectionModel().selectFirst();
		Callback<ListView<Class<?>>, ListCell<Class<?>>> cellFactory = FxUtil.createComboBoxCellFactory((Class<?> c) -> c.getSimpleName());
		comboBoxParentItemClass.setButtonCell(cellFactory.call(null));
		comboBoxParentItemClass.setCellFactory(cellFactory);
		
		
		List<TitledPane> panes = new ArrayList<TitledPane>();
		List<PropertyEntry> generalProperties = new ArrayList<PropertyEntry>();
		generalProperties.add(new PropertyEntry("Name", textFielName));
		generalProperties.add(new PropertyEntry("Description", textFieldDescription));
		generalProperties.add(new PropertyEntry("Parent item class", comboBoxParentItemClass));
		TitledPropertyPane generalPane = new TitledPropertyPane(generalProperties, "General");
		panes.add(generalPane);
		setTitledPanes(panes);
		
		setItemFactory(this::createItem);
		setTemplateSetter(this::setTemplate);
		setItemCopier(this::copy);
	}

	@Override
	protected void setTemplate(PropertyField item) {
		textFielName.setText(item.getName());
		textFieldDescription.setText(item.getDescription());
	}

	@Override
	public PropertyField createItem() {
		String name = textFielName.getText();
		String description = textFieldDescription.getText();
		Class<?> parentItemClass = comboBoxParentItemClass.getSelectionModel().getSelectedItem();
		return new PropertyField(name, description, parentItemClass);
	}

	@Override
	protected void copy(PropertyField from, PropertyField to) {
		to.setDescription(from.getDescription());
		to.setName(from.getName());
	}

}