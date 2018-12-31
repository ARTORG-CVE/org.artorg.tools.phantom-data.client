package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class DbPropertySelector<T> extends VBox {
	private final Class<? extends AbstractPropertifiedEntity<?>> parentItemClass;
//	private final Class<? extends AbstractProperty> propertyClass;
	private ProTableView<AbstractProperty> tableView;
	private AnchorPane editorPane;

//	public abstract PropertyNode<AbstractProperty, ?> getValuePropertyNode();
//
//	public abstract Collection<P> getProperties(T item);
//
//	public abstract void setProperties(T item, Collection<P> properties);

	public DbPropertySelector(Class<?> parentItemClass) {
		this.parentItemClass = (Class<? extends AbstractPropertifiedEntity<?>>) parentItemClass;

		ItemEditor<BooleanProperty> propertyEditor = Main.getUIEntity(BooleanProperty.class).createEditFactory();
		propertyEditor.showCreateMode();

//		ItemEditor<P> propertyEditor = new ItemEditor<P>(propertyClass) {
//			@Override
//			public void onCreatePostSuccessful(P item) {
//				tableView.getTable().getItems().add(item);
//			}
//
//			@Override
//			public void createPropertyGridPanes(Creator<P> creator) {
//				PropertyUI<P, V> propertyUI = Main.getPropertyUIEntity(propertyClass);
//				creator.addComboBox(PropertyField.class)
//						.of(item -> item.getPropertyField(),
//								(item, value) -> item.setPropertyField(value))
//						.setMapper(p -> p.getName()).addLabeled("Property field");
//
//				Node node = propertyUI.createValueNode();
//				creator.createNode(item -> item.getValue(), (item, value) -> item.setValue(value),
//						item -> propertyUI.getDefaultValue(), node,
//						value -> propertyUI.setValueToNode(node, value),
//						() -> propertyUI.getValueFromNode(node),
//						() -> propertyUI.setValueToNode(node, propertyUI.getDefaultValue()))
//						.addLabeled("Value");
//				creator.addTitledPropertyPane("General");
//			}
//
//			@Override
//			public void createSelectors(Creator<P> creator) {
//				// TODO Auto-generated method stub
//
//			}
//		};
		editorPane = new AnchorPane();
		FxUtil.addToPane(editorPane, propertyEditor);
		this.getChildren().add(editorPane);

		tableView = Main.getUIEntity(AbstractProperty.class).createProTableView();

		tableView.setRowFactory(
				new Callback<TableView<AbstractProperty>, TableRow<AbstractProperty>>() {
					@Override
					public TableRow<AbstractProperty> call(TableView<AbstractProperty> tableView) {
						final TableRow<AbstractProperty> row = new TableRow<>();

						ContextMenu contextMenu = new ContextMenu();
						MenuItem menuItem;
						menuItem = new MenuItem("Edit");
						menuItem.setOnAction(event -> {
							ItemEditor<AbstractProperty> editor =
									Main.getPropertyUIEntity(row.getItem().getClass())
											.createEditFactory();
							editorPane.getChildren().clear();
							FxUtil.addToPane(editorPane, editor);
							editor.showEditMode(row.getItem());
						});
						contextMenu.getItems().add(menuItem);
						menuItem = new MenuItem("Refresh");
						menuItem.setOnAction(event -> {});
						contextMenu.getItems().add(menuItem);

						row.contextMenuProperty()
								.bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
										.then(contextMenu).otherwise((ContextMenu) null));
						return row;
					};
				});

		this.getChildren().add(tableView);

	}

	public void setItem(AbstractPropertifiedEntity<?> item) {
		if (item == null) {
			tableView.getTable().getItems().clear();
			return;
		}
		List<AbstractProperty<?, ?>> items = new ArrayList<>();
		items.addAll(item.getBooleanProperties());
		items.addAll(item.getIntegerProperties());
		items.addAll(item.getDoubleProperties());
		items.addAll(item.getStringProperties());
		items.addAll(item.getDateProperties());
		tableView.getTable().getItems().clear();
		tableView.getTable().getItems().addAll(items);
	}

//	public Class<T> getParentItemClass() {
//		return parentItemClass;
//	}

//	public Class<P> getPropertyClass() {
//		return propertyClass;
//	}

}
