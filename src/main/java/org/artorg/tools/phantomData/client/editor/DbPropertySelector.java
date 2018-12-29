package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public abstract class DbPropertySelector<T, P extends AbstractProperty<P, V>, V>
		extends VBox {
	private final Class<T> parentItemClass;
	private final Class<P> propertyClass;
	private ProTableView<P> tableView;
	private Button applyButton;

	public abstract PropertyNode<P, V> getValuePropertyNode();

	public abstract Collection<P> getProperties(T item);

	public abstract void setProperties(T item, Collection<P> properties);

	public DbPropertySelector(Class<T> parentItemClass, Class<P> propertyClass) {
		this.parentItemClass = parentItemClass;
		this.propertyClass = propertyClass;
		
		ItemEditor<P> propertyEditor = new ItemEditor<P>(propertyClass) {
			@Override
			public void onCreatePostSuccessful(P item) {
				tableView.getTable().getItems().add(item);
			}
		};
		PropertyUI<P,V> propertyUI = Main.getPropertyUIEntity(propertyClass);
		
		List<PropertyEntry> entries = new ArrayList<>();
		propertyEditor.createComboBox(PropertyField.class)
				.of(item -> item.getPropertyField(), (item, value) -> item.setPropertyField(value))
				.setMapper(p -> p.getName()).addLabeled("Property field", entries);
		
		Node node = propertyUI.createValueNode();
		propertyEditor.createNode((item, value) -> item.setValue(value), item -> item.getValue(),
				item -> propertyUI.getDefaultValue(), node, value -> propertyUI.setValueToNode(node, value),
				() -> propertyUI.getValueFromNode(node), () -> propertyUI.setValueToNode(node, propertyUI.getDefaultValue()))
				.addLabeled("Value", entries);
		this.getChildren().add(propertyEditor.createUntitledPane(entries));
		
		applyButton = new Button("Apply");
		
		this.getChildren().add(createButtonPane(applyButton));
		
		
		
		
		
		ProTableView<P> table = Main.getUIEntity(propertyClass).createProTableView();
		this.getChildren().add(table);

	}
	
	private void create() {
		createItem(null);
	}
	
	private void createItem(P item) {
		
		
		applyButton.setText("Create");
	}

	private void editItem(P item) {
		
	}
	
	
	
	
	
	
	public AnchorPane createButtonPane(Button button) {
		button.setPrefHeight(25.0);
		button.setMaxWidth(Double.MAX_VALUE);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPrefHeight(button.getPrefHeight() + 20);
		buttonPane.setMaxHeight(buttonPane.getPrefHeight());
		buttonPane.setPadding(new Insets(5, 10, 5, 10));
		buttonPane.getChildren().add(button);
		FxUtil.setAnchorZero(button);
		return buttonPane;
	}

	private Optional<P> findElement(Collection<P> properties, UUID id) {
		return properties.stream().filter(p -> p.getId().equals(id)).findFirst();
	}

	public Class<T> getParentItemClass() {
		return parentItemClass;
	}

	public Class<P> getPropertyClass() {
		return propertyClass;
	}

}
