package org.artorg.tools.phantomData.client.editor2;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.editor.select.DbTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class ItemEditor<T> extends AnchorPane implements FxFactory<T> {
	private final Class<T> itemClass;
	private final Button applyButton;
	private final ICrudConnector<T> connector;

	{
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.connector = (ICrudConnector<T>) Connectors.getConnector(itemClass);
	}

	private final List<PropertyNode<T>> nodes;

	{
		nodes = new ArrayList<>();
	}

	@Override
	public Node getGraphic() {
		return this;
	}

	@Override
	public Node create(T item) {
		applyButton.setOnAction(event -> {
			FxUtil.runNewSingleThreaded(() -> {
				T item2 = item;
				if (item2 == null) {
					try {
						item2 = getItemClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				final T item3 = item2;
				nodes.stream().forEach(node -> node.nodeToEntity(item3));
				try {
				if (getConnector().create(item3)) {
					Platform.runLater(
							() -> nodes.stream().forEach(node -> node.entityToNodeAdd(item3)));
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		applyButton.setText("Create");

		return getGraphic();
	}

	@Override
	public Node edit(T item) {
		nodes.stream().forEach(node -> node.entityToNodeEdit(item));

		applyButton.setOnAction(event -> {

//					applyChanges(item);
			nodes.stream().forEach(node -> {
				node.nodeToEntity(item);
			});

//					selectors.forEach(selector -> selector.setSelectedChildItems(item));

			getConnector().update(item);

		});

		applyButton.setText("Save");

		return getGraphic();
	}

	@Override
	public Node create() {
		return create(null);
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

	
	
	public PropertyNode<T> createTextField(BiConsumer<T, String> setter,
			Function<T, String> getter) {
		return createTextField(setter, getter, item -> "", "");
	}

	public PropertyNode<T> createTextField(BiConsumer<T, String> setter, Function<T, String> getter,
			Function<T, String> getterAdd, String defaultValue) {
		TextField node = new TextField();
		return createTextField(node, setter, getter, getterAdd, defaultValue);
		
	}
	
	public PropertyNode<T> createTextField(TextField node, BiConsumer<T, String> setter,
			Function<T, String> getter) {
		return createTextField(node, setter, getter, item -> "", "");
	}
	
	public PropertyNode<T> createTextField(TextField node, BiConsumer<T, String> setter, Function<T, String> getter,
			Function<T, String> getterAdd, String defaultValue) {
		return createNode(setter, getter, getterAdd, "", node,
				(value) -> node.setText(value), () -> node.getText());
	}

	public <U, N extends Node> PropertyNode<T> createNode(BiConsumer<T, U> setter,
			Function<T, U> getter, Function<T, U> getterAdd, U defaultValue, Node node,
			Consumer<U> entityToNode, Supplier<U> nodeToEntity) {
		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

			@Override
			protected void nodeToEntityImpl(T item) {
				setter.accept(item, nodeToEntity.get());
			}

			@Override
			protected void entityToNodeEditImpl(T item) {
				entityToNode.accept(getter.apply(item));
			}

			@Override
			protected void entityToNodeAddImpl(T item) {
				entityToNode.accept(getterAdd.apply(item));
			}

			@Override
			protected void setDefaultImpl() {
				entityToNode.accept(defaultValue);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}

	public PropertyNode<T> createCheckBox(BiConsumer<T, Boolean> setter,
			Function<T, Boolean> getter, Function<T, Boolean> getterAdd, Boolean defaultValue) {
		
		CheckBox node = new CheckBox();
		return createNode(setter, getter, getterAdd, false, node,
				(value) -> node.setSelected(value), () -> node.isSelected());
//		
//		CheckBox node = new CheckBox();
//		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {
//
//			@Override
//			protected void nodeToEntityImpl(T item) {
//				setter.accept(item, node.isSelected());
//			}
//
//			@Override
//			protected void entityToNodeEditImpl(T item) {
//				node.setSelected(getter.apply(item));
//			}
//
//			@Override
//			protected void entityToNodeAddImpl(T item) {
//				node.setSelected(getterAdd.apply(item));
//			}
//
//			@Override
//			public void setDefaultImpl() {
//				node.setSelected(defaultValue);
//			}
//		};
//		nodes.add(propertyNode);
//		return propertyNode;
	}

	public PropertyNode<T> createDatePicker(BiConsumer<T, Date> setter, Function<T, Date> getter) {
		Function<LocalDate,Date> localToDate = local -> Date
				.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Function<Date,LocalDate> dateToLocal = date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		BiConsumer<T, LocalDate> setterL = (item,local) -> setter.accept(item, localToDate.apply(local));
		Function<T,LocalDate> getterL = item -> dateToLocal.apply(getter.apply(item));
		
		DatePicker node = new DatePicker();
		return createNode(setterL, getterL, item -> LocalDate.now(), LocalDate.now(), node,
				(value) -> node.setValue(value), () -> node.getValue());
		
//		DatePicker node = new DatePicker();
//		PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {
//
//			@Override
//			protected void nodeToEntityImpl(T item) {
//				setter.accept(item, Date
//						.from(node.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//			}
//
//			@Override
//			protected void entityToNodeEditImpl(T item) {
//				LocalDate localDate =
//						getter.apply(item).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//				node.setValue(localDate);
//			}
//
//			@Override
//			protected void entityToNodeAddImpl(T item) {
//				node.setValue(LocalDate.now());
//			}
//
//			@Override
//			public void setDefaultImpl() {
//				node.setValue(LocalDate.now());
//			}
//		};
//		nodes.add(propertyNode);
//		return propertyNode;
	}

	public <U> ComboBoxCreator<U> createComboBox(Class<U> subItemClass) {
		return new ComboBoxCreator<U>(subItemClass);
	}

	public class ComboBoxCreator<U> {
		private final Class<U> subItemClass;

		public ComboBoxCreator(Class<U> subItemClass) {
			this.subItemClass = subItemClass;
		}

		public PropertyNode<T> of(BiConsumer<T, U> setter, Function<T, U> getter,
				Function<U, String> mapper) {
			return of(setter, getter, item -> null, mapper);
		}

		public PropertyNode<T> of(BiConsumer<T, U> setter, Function<T, U> getter,
				Function<T, U> getterAdd, Function<U, String> mapper) {
			ComboBox<U> node = new ComboBox<U>();
			createComboBox(node, subItemClass, mapper);
			PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

				@Override
				protected void nodeToEntityImpl(T item) {
					setter.accept(item, node.getSelectionModel().getSelectedItem());
				}

				@Override
				protected void entityToNodeEditImpl(T item) {
					selectComboBoxItem(node, getter.apply(item));
				}

				@Override
				protected void entityToNodeAddImpl(T item) {
					selectComboBoxItem(node, getterAdd.apply(item));
				}

				@Override
				protected void setDefaultImpl() {
					node.getSelectionModel().clearSelection();
				}
			};
			nodes.add(propertyNode);
			return propertyNode;
		}

	}

	protected <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
			Function<U, String> mapper) {
		createComboBox(comboBox, itemClass, mapper, item -> {});
	}

	protected <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
			Function<U, String> mapper, Consumer<U> selectedItemChangedConsumer) {
		ICrudConnector<U> connector = (ICrudConnector<U>) Connectors.getConnector(itemClass);
		FxUtil.createDbComboBox(comboBox, connector, mapper);

		ChangeListener<U> listener = (observable, oldValue, newValue) -> {
			try {
				selectedItemChangedConsumer.accept(newValue);
			} catch (Exception e) {}
		};
		comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	protected <U> void selectComboBoxItem(ComboBox<U> comboBox, U item) {
		if (item == null) {
			comboBox.getSelectionModel().clearSelection();
			return;
		}
		for (int i = 0; i < comboBox.getItems().size(); i++)
			if (((Identifiable<?>) comboBox.getItems().get(i)).equalsId((Identifiable<?>) item)) {
				comboBox.getSelectionModel().select(i);
				break;
			}
	}

	public <U> SelectorCreator<U> createSelector(Class<U> valueClass) {
		return new SelectorCreator<U>(valueClass);
	}

	public class SelectorCreator<U> {
		private Class<U> subItemClass;

		public SelectorCreator(Class<U> subItemClass) {
			this.subItemClass = subItemClass;
		}

		@SuppressWarnings("unchecked")
		public PropertyNode<T> titled(String name, BiConsumer<T, Collection<U>> setter) {
			PropertyNode<T> propertyNode = of(setter);
			DbTableViewSelector<T, U> selector = (DbTableViewSelector<T, U>) propertyNode.getNode();

			TitledPane titledPane = new TitledPane();
			titledPane.setContent(selector);
			titledPane.setText(name);
			titledPane.setExpanded(false);
			propertyNode.setNode(titledPane);
			return propertyNode;
		}

		public PropertyNode<T> of(BiConsumer<T, Collection<U>> setter) {
			DbTableViewSelector<T, U> selector =
					new DbTableViewSelector<T, U>(getItemClass(), subItemClass);
			Node node = selector;
			PropertyNode<T> propertyNode = new PropertyNode<T>(itemClass, node) {

				@Override
				protected void nodeToEntityImpl(T item) {
					setter.accept(item, selector.getSelectedItems());
				}

				@Override
				protected void entityToNodeEditImpl(T item) {
					selector.setItem(item);
				}

				@Override
				protected void entityToNodeAddImpl(T item) {
					selector.setItem(null);
				}

				@Override
				protected void setDefaultImpl() {
					selector.setItem(null);
				}
			};
			nodes.add(propertyNode);
			return propertyNode;

		}
	}

	@SuppressWarnings("unchecked")
	protected <U> DbTableViewSelector<T, U> createSelector(T item, Class<?> subItemClass) {
		List<U> selectableItems =
				DbTableViewSelector.getSelectableItems(getItemClass(), (Class<U>) subItemClass);
		if (selectableItems.isEmpty()) return null;

		if (DbTableViewSelector.containsCollectionSetter(getItemClass(), subItemClass)) {
			DbTableViewSelector<T, U> selector =
					new DbTableViewSelector<T, U>(getItemClass(), (Class<U>) subItemClass);
			selector.setItem(item);
			return selector;
		}
		return null;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public Button getApplyButton() {
		return applyButton;
	}

}
