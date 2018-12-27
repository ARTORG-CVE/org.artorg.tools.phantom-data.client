package org.artorg.tools.phantomData.client.editor2;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	private final List<PropertyNode<T, ?>> nodes;

	{
		nodes = new ArrayList<>();
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.connector = (ICrudConnector<T>) Connectors.getConnector(itemClass);
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
						Platform.runLater(() -> nodes.stream()
							.forEach(node -> node.entityToNodeAdd(item3)));
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
			nodes.stream().forEach(node -> node.nodeToEntity(item));
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

	public PropertyNode<T, ?> createTextField(BiConsumer<T, String> setter,
		Function<T, String> getter) {
		return createTextField(setter, getter, item -> "", "");
	}

	public PropertyNode<T, ?> createTextField(BiConsumer<T, String> setter,
		Function<T, String> getter, Function<T, String> getterAdd, String defaultValue) {
		TextField node = new TextField();
		return createTextField(node, setter, getter, getterAdd, defaultValue);

	}

	public PropertyNode<T, String> createTextField(TextField node,
		BiConsumer<T, String> setter, Function<T, String> getter) {
		return createTextField(node, setter, getter, item -> "", "");
	}

	public PropertyNode<T, String> createTextField(TextField node,
		BiConsumer<T, String> valueToEntitySetter,
		Function<T, String> entityToValueEditGetter,
		Function<T, String> entityToValueAddGetter, String defaultValue) {
		return createNode(valueToEntitySetter, entityToValueEditGetter,
			entityToValueAddGetter, node, (value) -> node.setText(value),
			() -> node.getText(), () -> node.setText(""));
	}

	public PropertyNode<T, Boolean> createCheckBox(BiConsumer<T, Boolean> setter,
		Function<T, Boolean> getter, Function<T, Boolean> getterAdd,
		Boolean defaultValue) {

		CheckBox node = new CheckBox();
		return createNode(setter, getter, getterAdd, node,
			(value) -> node.setSelected(value), () -> node.isSelected(),
			() -> node.setSelected(false));
	}

	public PropertyNode<T, LocalDate> createDatePicker(BiConsumer<T, Date> setter,
		Function<T, Date> getter) {
		Function<LocalDate, Date> localToDate =
			local -> Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Function<Date, LocalDate> dateToLocal =
			date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		BiConsumer<T, LocalDate> setterL =
			(item, local) -> setter.accept(item, localToDate.apply(local));
		Function<T, LocalDate> getterL = item -> dateToLocal.apply(getter.apply(item));

		DatePicker node = new DatePicker();
		return createNode(setterL, getterL, item -> LocalDate.now(), node,
			(value) -> node.setValue(value), () -> node.getValue(),
			() -> node.setValue(LocalDate.now()));
	}

	public <U> ComboBoxCreator<U> createComboBox(Class<U> subItemClass, ComboBox<U> node) {
		return new ComboBoxCreator<U>(subItemClass, node);
	}
	
	public <U> ComboBoxCreator<U> createComboBox(Class<U> subItemClass) {
		return new ComboBoxCreator<U>(subItemClass);
	}

	public class ComboBoxCreator<U> {
		private final Class<U> subItemClass;
		private final ComboBox<U> node;

		public ComboBoxCreator(Class<U> subItemClass, ComboBox<U> node) {
			this.subItemClass = subItemClass;
			this.node = node;
		}
		
		public ComboBoxCreator(Class<U> subItemClass) {
			this.subItemClass = subItemClass;
			this.node = null;
		}
		
		@SuppressWarnings("unchecked")
		public PropertyNode<T, U> of(BiConsumer<T, String> valueToEntitySetter, Function<T, String> entityToValueEditGetter) {
			return of((BiConsumer<T, U>)valueToEntitySetter, (Function<T, U>)entityToValueEditGetter, item -> null, s -> (String)s);
		}
		
		public PropertyNode<T, U> of(BiConsumer<T, U> valueToEntitySetter, Function<T, U> entityToValueEditGetter,
			Function<U, String> mapper) {
			return of(valueToEntitySetter, entityToValueEditGetter, item -> null, mapper);
		}		
		
		public PropertyNode<T, U> of(BiConsumer<T, U> valueToEntitySetter,
			Function<T, U> entityToValueEditGetter, Function<T, U> entityToValueAddGetter,
			Function<U, String> mapper) {
			final ComboBox<U> node = this.node != null? this.node : new ComboBox<U>();
			
			createComboBox(node, subItemClass, mapper);
			PropertyNode<T, U> propertyNode = new PropertyNode<T, U>(itemClass, node) {

				@Override
				protected U entityToValueEditGetterImpl(T item) {
					return entityToValueEditGetter.apply(item);
				}

				@Override
				protected U entityToValueAddGetterImpl(T item) {
					return entityToValueAddGetter.apply(item);
				}

				@Override
				protected void valueToEntitySetterImpl(T item, U value) {
					valueToEntitySetter.accept(item, value);
				}

				@SuppressWarnings("unchecked")
				@Override
				protected U nodeToValueGetterImpl() {
					return ((ComboBox<U>)getNode()).getSelectionModel().getSelectedItem();
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void valueToNodeSetterImpl(U value) {
					selectComboBoxItem(((ComboBox<U>)getNode()), value);
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void defaultSetterRunnableImpl() {
					((ComboBox<U>)getNode()).getSelectionModel().clearSelection();
				}

			};
			nodes.add(propertyNode);
			return propertyNode;
		}

	}

	private <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
		Function<U, String> mapper) {
		createComboBox(comboBox, itemClass, mapper, item -> {});
	}

	protected <U> void createComboBox(ComboBox<U> comboBox, Class<U> itemClass,
		Function<U, String> mapper, Consumer<U> selectedItemChangedConsumer) {
		ICrudConnector<U> connector =
			(ICrudConnector<U>) Connectors.getConnector(itemClass);
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
			if (((Identifiable<?>) comboBox.getItems().get(i))
				.equalsId((Identifiable<?>) item)) {
				comboBox.getSelectionModel().select(i);
				break;
			}
	}

	public <U> PropertyNode<T, U> createNode(BiConsumer<T, U> valueToEntitySetter,
		Function<T, U> entityToValueEditGetter, Function<T, U> entityToValueAddGetter,
		Node node, Consumer<U> valueToNodeSetter, Supplier<U> nodeToValueGetter,
		Runnable defaultSetterRunnable) {
		PropertyNode<T, U> propertyNode = new PropertyNode<T, U>(itemClass, node) {

			@Override
			protected void defaultSetterRunnableImpl() {
				defaultSetterRunnable.run();
			}

			@Override
			protected U entityToValueEditGetterImpl(T item) {
				return entityToValueEditGetter.apply(item);
			}

			@Override
			protected U entityToValueAddGetterImpl(T item) {
				return entityToValueAddGetter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, U value) {
				valueToEntitySetter.accept(item, value);
			}

			@Override
			protected U nodeToValueGetterImpl() {
				return nodeToValueGetter.get();
			}

			@Override
			protected void valueToNodeSetterImpl(U value) {
				valueToNodeSetter.accept(value);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
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
		public PropertyNode<T, Collection<U>> titled(String name,
			Function<T, Collection<U>> entityToValueEditGetter,
			BiConsumer<T, Collection<U>> setter) {
			PropertyNode<T, Collection<U>> propertyNode =
				of(entityToValueEditGetter, setter);
			DbTableViewSelector<T, U> selector =
				(DbTableViewSelector<T, U>) propertyNode.getNode();

			TitledPane titledPane = new TitledPane();
			titledPane.setContent(selector);
			titledPane.setText(name);
			titledPane.setExpanded(false);
			propertyNode.setNode(titledPane);
			return propertyNode;
		}

		public PropertyNode<T, Collection<U>> of(
			Function<T, Collection<U>> entityToValueEditGetter,
			BiConsumer<T, Collection<U>> setter) {
			DbTableViewSelector<T, U> selector =
				new DbTableViewSelector<T, U>(getItemClass(), subItemClass);
			Node node = selector;
			PropertyNode<T, Collection<U>> propertyNode =
				new PropertyNode<T, Collection<U>>(itemClass, node) {

					@Override
					protected Collection<U> entityToValueEditGetterImpl(T item) {
						return entityToValueEditGetter.apply(item);
					}

					@Override
					protected Collection<U> entityToValueAddGetterImpl(T item) {
						return Collections.emptyList();
					}

					@Override
					protected void valueToEntitySetterImpl(T item, Collection<U> value) {
						setter.accept(item, value);
					}

					@Override
					protected Collection<U> nodeToValueGetterImpl() {
						return selector.getSelectedItems();
					}

					@Override
					protected void valueToNodeSetterImpl(Collection<U> value) {
						selector.setItems(value);
					}

					@Override
					protected void defaultSetterRunnableImpl() {
						selector.setItem(null);
					}

				};
			nodes.add(propertyNode);
			return propertyNode;

		}
	}

	@SuppressWarnings("unchecked")
	protected <U> DbTableViewSelector<T, U> createSelector(T item,
		Class<?> subItemClass) {
		List<U> selectableItems = DbTableViewSelector.getSelectableItems(getItemClass(),
			(Class<U>) subItemClass);
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

	public List<PropertyNode<T, ?>> getNodes() {
		return nodes;
	}

	@Override
	public Node getGraphic() {
		return this;
	}

}
