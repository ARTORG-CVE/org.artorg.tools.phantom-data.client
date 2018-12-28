package org.artorg.tools.phantomData.client.editor;

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
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class ItemEditor<T> extends AnchorPane {
	private final Class<T> itemClass;
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private final List<PropertyNode<T, ?>> nodes;
	private T item;

	{
		nodes = new ArrayList<>();
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.connector = (ICrudConnector<T>) Connectors.get(itemClass);
	}

	public void onCreateInit(T item) {}

	public void onCreateBeforePost(T item) {}

	public void onCreatePostSuccessful(T item) {}

	public void onEditInit(T item) {}

	public void onEditBeforApplyChanges(T item) {}

	public void onEditBeforePut(T item) {}

	public void onEditPutSuccessful(T item) {}

	public final void createItem(T item) {
		this.item = item;
		onCreateInit(item);
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
					onCreateBeforePost(item3);
					if (getConnector().create(item3)) {
						this.item = item3;
						onCreatePostSuccessful(item3);
						Platform.runLater(
								() -> nodes.stream().forEach(node -> node.entityToNodeAdd(item3)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		applyButton.setText("Create");
		nodes.stream().forEach(node -> node.entityToNodeAdd(item));
	}

	public final void editItem(T item) {
		this.item = item;
		onEditInit(item);
		applyButton.setOnAction(event -> {
			onEditBeforApplyChanges(item);
			nodes.stream().forEach(node -> node.nodeToEntity(item));
			onEditBeforePut(item);
			if (getConnector().update(item)) {
				this.item = item;
				onEditPutSuccessful(item);
			}
		});
		applyButton.setText("Save");
		nodes.stream().forEach(node -> node.entityToNodeEdit(item));
	}

	public final void createItem() {
		createItem(null);
	}

	public <U> void addNodes(ItemEditor<U> subEditor) {
		Collection<PropertyNode<T,?>> list = subEditor.getNodes().stream()
				.map(propertyNode -> propertyNode.map(itemClass, item -> subEditor.getItem()))
				.collect(Collectors.toList());
		nodes.addAll(list);
	}

	public void add(PropertyNode<T, ?> propertyNode) {
		nodes.add(propertyNode);
	}

	public TitledPane createTitledPane(List<PropertyEntry> entries, String title) {
		TitledPane titledPane = new TitledPane();
		PropertyGridPane gridPane = new PropertyGridPane(entries);
		titledPane.setText(title);
		titledPane.setContent(gridPane);
		return titledPane;
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

	public StringPropertyNode createTextField(Function<T, String> getter,
			BiConsumer<T, String> setter) {
		TextField node = new TextField();
		return createTextField(node, getter, setter);
	}

	public StringPropertyNode createLabel(Label label, Function<T, String> getter,
			BiConsumer<T, String> setter) {
		StringPropertyNode propertyNode = new StringPropertyNode(itemClass, label,
				node -> ((Label) node).getText(), (node, value) -> ((Label) node).setText(value)) {

			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}

	public StringPropertyNode createTextField(TextField textField, Function<T, String> getter,
			BiConsumer<T, String> setter) {
		StringPropertyNode propertyNode = new StringPropertyNode(itemClass, textField,
				node -> ((TextField) node).getText(), (node, s) -> ((TextField) node).setText(s)) {
			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}
		};
		nodes.add(propertyNode);
		return propertyNode;
	}

	public abstract class StringPropertyNode extends PropertyNode<T, String> {
		private final Node controlNode;
		private final Function<Node, String> getter;
		private final BiConsumer<Node, String> setter;

		public StringPropertyNode(Class<T> itemClass, Node controlNode,
				Function<Node, String> getter, BiConsumer<Node, String> setter) {
			super(itemClass, controlNode);
			this.controlNode = controlNode;
			this.getter = getter;
			this.setter = setter;
		}

		public StringPropertyNode setDefaultValue(String defaultValue) {
			setDefaultSetterRunnable(() -> setter.accept(controlNode, defaultValue));
			return this;
		}

		@Override
		protected void defaultSetterRunnableImpl() {
			setter.accept(controlNode, "");
		}

		@Override
		protected String entityToValueAddGetterImpl(T item) {
			return "";
		}

		@Override
		protected String nodeToValueGetterImpl() {
			return getter.apply(controlNode);
		}

		@Override
		protected void valueToNodeSetterImpl(String value) {
			setter.accept(controlNode, value);
		}

	}

	public PropertyNode<T, Boolean> createCheckBox(BiConsumer<T, Boolean> setter,
			Function<T, Boolean> getter, Function<T, Boolean> getterAdd, Boolean defaultValue) {

		CheckBox node = new CheckBox();
		return createNode(setter, getter, getterAdd, node, (value) -> node.setSelected(value),
				() -> node.isSelected(), () -> node.setSelected(false));
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
		private final ComboBox<U> controlNode;

		public ComboBoxCreator(Class<U> subItemClass, ComboBox<U> controlNode) {
			this.subItemClass = subItemClass;
			this.controlNode = controlNode;
		}

		public ComboBoxCreator(Class<U> subItemClass) {
			this.subItemClass = subItemClass;
			this.controlNode = new ComboBox<U>();
		}

		public abstract class ComboBoxPropertyNode extends PropertyNode<T, U> {
			private final ComboBox<U> controlNode;

			public ComboBoxPropertyNode(Class<T> itemClass, ComboBox<U> controlNode) {
				super(itemClass, controlNode);
				this.controlNode = controlNode;
			}

			public ComboBoxPropertyNode setMapper(Function<U, String> mapper) {
				Callback<ListView<U>, ListCell<U>> cellFactory =
						FxUtil.createComboBoxCellFactory(mapper);
				controlNode.setButtonCell(cellFactory.call(null));
				controlNode.setCellFactory(cellFactory);
				return this;
			}

			public ComboBoxPropertyNode addSelectListener(ChangeListener<? super U> listener) {
				getControlNode().getSelectionModel().selectedItemProperty().addListener(listener);
				return this;
			}

			@Override
			public ComboBox<U> getControlNode() {
				return controlNode;
			}

		}

		public ComboBoxPropertyNode of(Function<T, U> getter, BiConsumer<T, U> setter) {
			List<U> items = Connectors.get(subItemClass).readAllAsList();
			controlNode.setItems(FXCollections.observableList(items));
			FxUtil.setComboBoxCellFactory(controlNode, o -> o.toString());

			ComboBoxPropertyNode propertyNode = new ComboBoxPropertyNode(itemClass, controlNode) {

				@Override
				protected U entityToValueEditGetterImpl(T item) {
					return getter.apply(item);
				}

				@Override
				protected U entityToValueAddGetterImpl(T item) {
					((ComboBox<U>) getControlNode()).getSelectionModel().clearSelection();
					return null;
				}

				@Override
				protected void valueToEntitySetterImpl(T item, U value) {
					setter.accept(item, value);
				}

				@Override
				protected U nodeToValueGetterImpl() {
					return ((ComboBox<U>) getControlNode()).getSelectionModel().getSelectedItem();
				}

				@Override
				protected void valueToNodeSetterImpl(U value) {
					selectComboBoxItem(((ComboBox<U>) getControlNode()), value);
				}

				@Override
				protected void defaultSetterRunnableImpl() {
					((ComboBox<U>) getControlNode()).getSelectionModel().clearSelection();

					Callback<ListView<U>, ListCell<U>> cellFactory =
							FxUtil.createComboBoxCellFactory(item -> "");

					((ComboBox<U>) getControlNode()).setButtonCell(cellFactory.call(null));
				}

			};
			nodes.add(propertyNode);
			return propertyNode;
		}

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
			PropertyNode<T, Collection<U>> propertyNode = of(entityToValueEditGetter, setter);
			DbTableViewSelector<T, U> selector =
					(DbTableViewSelector<T, U>) propertyNode.getParentNode();

			TitledPane titledPane = new TitledPane();
			titledPane.setContent(selector);
			titledPane.setText(name);
			titledPane.setExpanded(false);
			propertyNode.setParentNode(titledPane);
			return propertyNode;
		}

		public PropertyNode<T, Collection<U>> of(Function<T, Collection<U>> entityToValueEditGetter,
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

	public List<PropertyNode<T, ?>> getNodes() {
		return nodes;
	}

	public T getItem() {
		return item;
	}

}
