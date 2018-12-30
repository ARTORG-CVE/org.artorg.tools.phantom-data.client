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

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.Identifiable;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;

public class Creator<T> {
	private final Class<T> itemClass;
	private final ItemEditor<T> editor;
	private final List<PropertyEntry> propertyEntries;
	private final List<PropertyGridPane> propertyPanes;
	private final List<PropertyGridPane> readOnlyPropertyPanes;
	private final List<DbTableViewSelector<T,?>> selectors;
	private final List<DbTableViewSelector<T,?>> readOnlySelectors;
	
	{
		propertyEntries = new ArrayList<>();
		propertyPanes = new ArrayList<>();
		readOnlyPropertyPanes = Collections.unmodifiableList(propertyPanes);
		selectors = new ArrayList<>();
		readOnlySelectors = Collections.unmodifiableList(selectors);
	}
	
	Creator(Class<T> itemClass, ItemEditor<T> editor) {
		this.itemClass = itemClass;
		this.editor = editor;
	}
	
	public void addTitledPropertyPane(String title) {
		TitledPane titledPane = createTitledPropertyPane(title);
		editor.getvBox().getChildren().add(titledPane);
	}
	
	public TitledPane createTitledPropertyPane(String title) {
		TitledPane titledPane = new TitledPane();
		PropertyGridPane gridPane = createPropertyGridPane();
		titledPane.setText(title);
		titledPane.setContent(gridPane);
		return titledPane;
	}
	
	public PropertyGridPane createPropertyGridPane() {
		List<PropertyEntry> entries = new ArrayList<>();
		entries.addAll(propertyEntries);
		propertyEntries.clear();
		PropertyGridPane propertyPane = new PropertyGridPane(entries); 
		propertyPanes.add(propertyPane);
		return propertyPane;
	}
	
	public void addTitledSelectorPane(String title) {
		TitledPane titledPane = createTitledSelectorPane(title);
		editor.getvBox().getChildren().add(titledPane);
	}
	
	public TitledPane createTitledSelectorPane(String title) {
		TitledPane titledPane = new TitledPane();
		titledPane.setText(title);
		titledPane.setContent(selectors.get(0));
		selectors.remove(0);
		return titledPane;
	}
	
	public boolean addPropertyEntries(Collection<PropertyEntry> entries) {
		return propertyEntries.addAll(entries);
	}
	
	public boolean addPropertyEntry(PropertyEntry propertyEntry) {
		return propertyEntries.add(propertyEntry);
	}
	
	public boolean addSelector(DbTableViewSelector<T,?> selector) {
		return selectors.add(selector);
	}
	
	public StringPropertyNode textArea(Function<T, String> getter, BiConsumer<T, String> setter) {
		TextArea node = new TextArea();
		return createTextArea(node, getter, setter);
	}
	
	public void initTextArea(TextArea textArea) {
		textArea.setEditable(true);
		textArea.setWrapText(true);
	}
	
	public StringPropertyNode createTextArea(TextArea textArea, Function<T, String> getter, BiConsumer<T, String> setter) {
		initTextArea(textArea);
		StringPropertyNode propertyNode = new StringPropertyNode(itemClass, textArea,
				node -> ((TextArea) node).getText(), (node, s) -> ((TextArea) node).setText(s)) {
			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}
		};
		editor.addPropertyNode(propertyNode);
		return propertyNode;
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
		editor.addPropertyNode(propertyNode);
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
		editor.addPropertyNode(propertyNode);
		return propertyNode;
	}

	public abstract class StringPropertyNode extends PropertyNode<T, String> {
		private final Node controlNode;
		private final Function<Node, String> getter;
		private final BiConsumer<Node, String> setter;

		public StringPropertyNode(Class<T> itemClass, Node controlNode,
				Function<Node, String> getter, BiConsumer<Node, String> setter) {
			super(itemClass, controlNode, Creator.this);
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
				super(itemClass, controlNode, Creator.this);
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
			editor.addPropertyNode(propertyNode);
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
		PropertyNode<T, U> propertyNode = new PropertyNode<T, U>(itemClass, node, Creator.this) {

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
		editor.addPropertyNode(propertyNode);
		return propertyNode;
	}
	
	@SuppressWarnings("unchecked")
	public <U> PropertyNode<T, Collection<U>> titled(String name, Class<U> subItemClass,
			Function<T, Collection<U>> getter,
			BiConsumer<T, Collection<U>> setter) {
		PropertyNode<T, Collection<U>> propertyNode = selector(subItemClass, getter, setter);
		DbTableViewSelector<T, U> selector =
				(DbTableViewSelector<T, U>) propertyNode.getParentNode();

		TitledPane titledPane = new TitledPane();
		titledPane.setContent(selector);
		titledPane.setText(name);
		titledPane.setExpanded(false);
		propertyNode.setParentNode(titledPane);
		return propertyNode;
	}
	
	@SuppressWarnings("unchecked")
	public <U> void addSelector(String title, Class<U> subItemClass, Function<T, Collection<U>> getter,
			BiConsumer<T, Collection<U>> setter) {
		selector(subItemClass, getter, setter);
		TitledPane titledPane = new TitledPane();
		DbTableViewSelector<T,U> selector = (DbTableViewSelector<T, U>) selectors.get(selectors.size()-1);
		titledPane.setText(title);
		titledPane.setContent(selector);
		titledPane.setExpanded(false);
		editor.getvBox().getChildren().add(titledPane);
		selectors.remove(selector);
	}

	public <U> PropertyNode<T, Collection<U>> selector(Class<U> subItemClass, Function<T, Collection<U>> getter,
			BiConsumer<T, Collection<U>> setter) {
		DbTableViewSelector<T, U> selector =
				new DbTableViewSelector<T, U>(getItemClass(), subItemClass);
		selectors.add(selector);
		Node node = selector;
		PropertyNode<T, Collection<U>> propertyNode =
				new PropertyNode<T, Collection<U>>(itemClass, node, Creator.this) {

					@Override
					protected Collection<U> entityToValueEditGetterImpl(T item) {
						return getter.apply(item);
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
		editor.addPropertyNode(propertyNode);
		return propertyNode;

	}
	
	
	
	public Class<T> getItemClass() {
		return itemClass;
	}

	public List<PropertyGridPane> getPropertyGridPanes() {
		return readOnlyPropertyPanes;
	}
	
	public List<DbTableViewSelector<T,?>> getSelectors() {
		return readOnlySelectors;
	}
}
