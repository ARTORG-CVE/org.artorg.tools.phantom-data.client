package org.artorg.tools.phantomData.client.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.scene.control.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.scene.control.VGridBoxPane;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.util.Reflect;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public abstract class ItemEditFactoryController<ITEM extends DbPersistent<ITEM, ?>> extends VGridBoxPane
		implements FxFactory<ITEM> {
	protected Button applyButton;
	private List<Node> rightNodes;
	private List<AbstractTableViewSelector<ITEM>> selectors;
	private ProTableView<ITEM> table;
	private AnchorPane pane;

	{
		super.addColumn(80.0);
		super.addColumn(180.0);
		applyButton = new Button("Apply");
	}

	public abstract ITEM createItem();

	protected abstract void setEditTemplate(ITEM item);

	public void setAddTemplate(ITEM item) {
//		setEditTemplate();
	}

	protected abstract void applyChanges(ITEM item);

	public ProTableView<ITEM> getTableView() {
		return table;
	}

	public Node getGraphic() {
		return pane;
	}

	@Override
	public void setTableView(ProTableView<ITEM> table) {
		this.table = table;
	}

	protected abstract AnchorPane createRootPane();

	protected abstract void addProperties(ITEM item);

	public abstract List<PropertyEntry> getPropertyEntries();

	protected void setSelectedChildItems(ITEM item, AbstractTableViewSelector<ITEM> selector) {
		selector.setSelectedChildItems(item);
	}

	public List<AbstractTableViewSelector<ITEM>> getSelectors() {
		return selectors;
	}

	@SuppressWarnings("unchecked")
	private List<AbstractTableViewSelector<ITEM>> createSelectors(ITEM item, Class<?> itemClass) {
		List<AbstractTableViewSelector<ITEM>> selectors = new ArrayList<AbstractTableViewSelector<ITEM>>();

		List<Class<? extends DbPersistent<?, ?>>> subItemClasses = Reflect.getCollectionSetterMethods(itemClass)
				.map(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<? extends DbPersistent<?, ?>> cls = (Class<? extends DbPersistent<?, ?>>) Reflect
							.getGenericTypeClass(type);
					return cls;
				}).filter(c -> c != null).filter(c -> DbPersistent.class.isAssignableFrom(c))
				.collect(Collectors.toList());

		subItemClasses.forEach(subItemClass -> {
			if (Reflect.containsCollectionSetter(itemClass, subItemClass)) {
				ICrudConnector<?, ?> connector = Connectors.getConnector(subItemClass);
				Set<Object> selectableItemSet = (Set<Object>) connector.readAllAsSet();

				if (selectableItemSet.size() > 0) {
					try {
						AbstractTableViewSelector<ITEM> titledSelector = new TitledPaneTableViewSelector<ITEM>(
								subItemClass);
						titledSelector.setSelectableItems(selectableItemSet);

						if (item != null) {
							Method selectedMethod = Reflect.getMethodByGenericReturnType(itemClass, subItemClass);

							if (selectedMethod != null) {
								Function<ITEM, Collection<Object>> subItemGetter2;
								subItemGetter2 = i -> {
									if (i == null)
										return null;
									try {
										return (Collection<Object>) (selectedMethod.invoke(i));
									} catch (IllegalAccessException | IllegalArgumentException
											| InvocationTargetException e) {
										e.printStackTrace();
									}
									return null;
								};
								titledSelector.setSelectedItems(subItemGetter2.apply(item).stream()
										.filter(e -> e != null).collect(Collectors.toSet()));
							}
						}

						titledSelector.init();
						selectors.add(titledSelector);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return selectors;
	}

	@SuppressWarnings("unchecked")
	public final ICrudConnector<ITEM, ?> getConnector() {
		if (getTableView().getTable() instanceof IDbTable)
			return ((IDbTable<ITEM>) getTableView().getTable()).getConnector();
		return null;
	}

	protected void initDefaultValues() {
		rightNodes.forEach(node -> {
			if (node instanceof ComboBox)
				((ComboBox<?>) node).getSelectionModel().clearSelection();
			else if (node instanceof TextField)
				((TextField) node).setText("");
			else if (node instanceof CheckBox)
				((CheckBox) node).setSelected(false);
		});
	}

	protected <T extends Comparable<T>> void selectComboBoxItem(ComboBox<T> comboBox, T item) {
		for (int i = 0; i < comboBox.getItems().size(); i++)
			if (comboBox.getItems().get(i).equals(item)) {
				comboBox.getSelectionModel().select(i);
				break;
			}
	}

	protected <T extends DbPersistent<T, ID>, ID extends Comparable<ID>> void createComboBox(ComboBox<T> comboBox,
			Class<T> itemClass, Function<T, String> mapper) {
		createComboBox(comboBox, itemClass, mapper, item -> {
		});
	}

	@SuppressWarnings("unchecked")
	protected <T extends DbPersistent<T, ID>, ID extends Comparable<ID>> void createComboBox(ComboBox<T> comboBox,
			Class<T> itemClass, Function<T, String> mapper, Consumer<T> selectedItemChangedConsumer) {
		ICrudConnector<T, ID> connector = (ICrudConnector<T, ID>) PersonalizedHttpConnectorSpring
				.getOrCreate(itemClass);
		FxUtil.createDbComboBox(comboBox, connector, mapper);

		ChangeListener<T> listener = (observable, oldValue, newValue) -> {
			try {
				selectedItemChangedConsumer.accept(newValue);
			} catch (Exception e) {
			}
		};
		comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	public AnchorPane create(Class<?> itemClass) {
		return create(null, itemClass);
	}

	public AnchorPane create(ITEM item, Class<?> itemClass) {
		selectors = createSelectors(item, itemClass);
		addProperties(item);
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		pane = createRootPane();
		if (item != null)
			setAddTemplate(item);
		applyButton.setOnAction(event -> {
			if (!UserAdmin.isUserLoggedIn())
				MainFx.getMainController().openLoginLogoutFrame();
			else {
				FxUtil.runNewSingleThreaded(() -> {
					try {
						ITEM newItem = createItem();
						if (newItem != null) {
							if (selectors != null)
								selectors.stream().filter(selector -> selector != null)
										.forEach(selector -> selector.setSelectedChildItems(newItem));
							if (getConnector().create(newItem)) {
								getTableView().getItems().add(newItem);
								Platform.runLater(() -> {
									initDefaultValues();
								});
							}
						}
					} catch (Exception e) {
						handleException(e);
					}
				});
			}
		});
		applyButton.setText("Create");
		return pane;
	}

	public AnchorPane edit(ITEM item, Class<?> itemClass) {
		selectors = createSelectors(item, itemClass);
		Label label = new Label();
		label.setText(item.getId().toString());
		label.setDisable(true);
		PropertyEntry idEntry = new PropertyEntry("Id", label);
		getPropertyEntries().add(idEntry);
		addProperties(item);
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		pane = createRootPane();
		if (item != null)
			setEditTemplate(item);
		applyButton.setOnAction(event -> {
			if (!UserAdmin.isUserLoggedIn())
				MainFx.getMainController().openLoginLogoutFrame();
			else {
				try {
					applyChanges(item);

					selectors.forEach(selector -> selector.setSelectedChildItems(item));

					if (getConnector().update(item))
						table.refresh();

				} catch (Exception e) {
					handleException(e);
				}
			}
		});

		applyButton.setText("Save");
		return pane;
	}

	private void handleException(Exception e) {
		e.printStackTrace();
		if (e instanceof NoUserLoggedInException)
			System.err.println("log in !!");
	}

	private void createRightNodes(List<PropertyEntry> entries) {
		rightNodes = entries.stream().map(e -> e.getRightNode()).collect(Collectors.toList());
	}

}
