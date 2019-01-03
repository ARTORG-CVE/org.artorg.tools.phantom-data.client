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
import org.artorg.tools.phantomData.server.model.AbstractPropertifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.DateProperty;
import org.artorg.tools.phantomData.server.models.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.models.base.property.StringProperty;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class Creator<T> extends AnchorPane implements IPropertyNode {
	private final Class<T> itemClass;
	private final VBox vBox = new VBox();
	private final List<IPropertyNode> propertyNodes;

	{
		FxUtil.addToPane(this, vBox);
		propertyNodes = new ArrayList<>();
	}

	public Creator(Class<T> itemClass) {
		this.itemClass = itemClass;
	}

	public void add(IPropertyNode propertyNode) {
		addPropertyNode(propertyNode);
		getvBox().getChildren().add(propertyNode.getNode());
	}

	public TitledPane createTitledPane(String title, Node node) {
		TitledPane titledPane = new TitledPane();
		titledPane.setText(title);
		titledPane.setContent(node);
		titledPane.setExpanded(false);
		return titledPane;
	}

	@SuppressWarnings("rawtypes")
	public AbstractEditor<T, List<AbstractProperty>> createPropertySelector() {
		DbPropertySelector<T> propertySelector = new DbPropertySelector<T>(itemClass);
		return new AbstractEditor<T, List<AbstractProperty>>(itemClass, propertySelector) {

			@SuppressWarnings("unchecked")
			@Override
			protected List<AbstractProperty> entityToValueEditGetterImpl(T item) {
				AbstractPropertifiedEntity propertyItem = (AbstractPropertifiedEntity)item;
				List<AbstractProperty> items = new ArrayList<>();
				items.addAll(propertyItem.getBooleanProperties());
				items.addAll(propertyItem.getIntegerProperties());
				items.addAll(propertyItem.getDoubleProperties());
				items.addAll(propertyItem.getStringProperties());
				items.addAll(propertyItem.getDateProperties());
				return items;
			}

			@Override
			protected List<AbstractProperty> entityToValueAddGetterImpl(T item) {
				return new ArrayList<>();
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void valueToEntitySetterImpl(T item, List<AbstractProperty> value) {
				AbstractPropertifiedEntity propertyItem = (AbstractPropertifiedEntity)item;
				List<BooleanProperty> boolProps = new ArrayList<>();
				List<IntegerProperty> intProps = new ArrayList<>();
				List<DoubleProperty> doubleProps = new ArrayList<>();
				List<StringProperty> stringProps = new ArrayList<>();
				List<DateProperty> dateProps = new ArrayList<>();
				for (int i=0; i<value.size(); i++) {
					AbstractProperty<T,?> property = (AbstractProperty<T,?>)value.get(i);
					if (property instanceof BooleanProperty)
						boolProps.add((BooleanProperty) property);
					else if (property instanceof IntegerProperty)
						intProps.add((IntegerProperty) property);
					else if (property instanceof DoubleProperty)
						doubleProps.add((DoubleProperty) property);
					else if (property instanceof StringProperty)
						stringProps.add((StringProperty) property);
					else if (property instanceof DateProperty)
						dateProps.add((DateProperty) property);
					else
						throw new IllegalArgumentException();
				}
				propertyItem.setBooleanProperties(boolProps);
				propertyItem.setIntegerProperties(intProps);
				propertyItem.setDoubleProperties(doubleProps);
				propertyItem.setStringProperties(stringProps);
				propertyItem.setDateProperties(dateProps);
			}

			@Override
			protected List<AbstractProperty> nodeToValueGetterImpl() {
				return propertySelector.getPropertyItems();
			}

			@Override
			protected void valueToNodeSetterImpl(List<AbstractProperty> value) {
				propertySelector.setPropertyItems(value);

			}

			@Override
			protected void defaultSetterRunnableImpl() {
				propertySelector.setPropertyItems(new ArrayList<>());
			}

		};
	}
	
//	
//	public AbstractEditor<T, AbstractPropertifiedEntity<?>> createPropertySelector(
//			Class<T> itemClass, Function<T, AbstractPropertifiedEntity<?>> getter) {
//		DbPropertySelector<T> propertySelector = new DbPropertySelector<T>(itemClass);
//		return new AbstractEditor<T, AbstractPropertifiedEntity<?>>(itemClass, propertySelector) {
//
//			@Override
//			protected AbstractPropertifiedEntity<?> entityToValueEditGetterImpl(T item) {
//				return getter.apply(item);
//			}
//
//			@Override
//			protected AbstractPropertifiedEntity<?> entityToValueAddGetterImpl(T item) {
//				propertySelector.setPropertyItems(null);
//				return null;
//			}
//
//			@Override
//			protected void valueToEntitySetterImpl(T item, AbstractPropertifiedEntity<?> value) {}
//
//			@Override
//			protected AbstractPropertifiedEntity<?> nodeToValueGetterImpl() {
//				throw new UnsupportedOperationException();
//			}
//
//			@Override
//			protected void valueToNodeSetterImpl(AbstractPropertifiedEntity<?> value) {
//				propertySelector.setPropertyItems(value);
//			}
//
//			@Override
//			protected void defaultSetterRunnableImpl() {
//				propertySelector.setPropertyItems(null);
//			}
//
//		};
//	}
//	

	public <U> AbstractEditor<T, Collection<U>> createSelector(Class<U> subItemClass,
			Function<T, Collection<U>> getter, BiConsumer<T, Collection<U>> setter) {
		DbTableViewSelector<T, U> selector =
				new DbTableViewSelector<T, U>(getItemClass(), subItemClass);
		return new AbstractEditor<T, Collection<U>>(itemClass, selector) {

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
	}

	public StringPropertyNode createTextArea(Function<T, String> getter,
			BiConsumer<T, String> setter) {
		TextArea node = new TextArea();
		return create(node, getter, setter);
	}

	public StringPropertyNode createTextField(Function<T, String> getter,
			BiConsumer<T, String> setter) {
		TextField node = new TextField();
		return create(node, getter, setter);
	}

	public StringPropertyNode createLabel(Function<T, String> getter,
			BiConsumer<T, String> setter) {
		Label node = new Label();
		return create(node, getter, setter);
	}

	public StringPropertyNode create(TextArea controlNode, Function<T, String> getter,
			BiConsumer<T, String> setter) {
		controlNode.setEditable(true);
		controlNode.setWrapText(true);
		return new StringPropertyNode(controlNode, () -> controlNode.getText(),
				value -> controlNode.setText(value)) {
			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}

		};
	}

	public StringPropertyNode create(TextField controlNode, Function<T, String> getter,
			BiConsumer<T, String> setter) {
		return new StringPropertyNode(controlNode, () -> controlNode.getText(),
				(s) -> controlNode.setText(s)) {
			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}
		};
	}

	public StringPropertyNode create(Label controlNode, Function<T, String> getter,
			BiConsumer<T, String> setter) {
		return new StringPropertyNode(controlNode, () -> controlNode.getText(),
				(value) -> controlNode.setText(value)) {

			@Override
			protected String entityToValueEditGetterImpl(T item) {
				return getter.apply(item);
			}

			@Override
			protected void valueToEntitySetterImpl(T item, String value) {
				setter.accept(item, value);
			}
		};
	}

	public abstract class StringPropertyNode extends AbstractEditor<T, String> {
		private final Supplier<String> getter;
		private final Consumer<String> setter;

		public StringPropertyNode(Node controlNode, Supplier<String> getter,
				Consumer<String> setter) {
			super(itemClass, controlNode);
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		protected void defaultSetterRunnableImpl() {
			setter.accept("");
		}

		@Override
		protected String entityToValueAddGetterImpl(T item) {
			return "";
		}

		@Override
		protected String nodeToValueGetterImpl() {
			return getter.get();
		}

		@Override
		protected void valueToNodeSetterImpl(String value) {
			setter.accept(value);
		}

	}

	public AbstractEditor<T, Boolean> createCheckBox(Function<T, Boolean> getter,
			BiConsumer<T, Boolean> setter, Boolean defaultValue) {

		CheckBox node = new CheckBox();
		return createNode(getter, setter, item -> defaultValue, node,
				(value) -> node.setSelected(value), () -> node.isSelected(),
				() -> node.setSelected(false));
	}

	public AbstractEditor<T, LocalDate> createDatePicker(BiConsumer<T, Date> setter,
			Function<T, Date> getter) {
		Function<LocalDate, Date> localToDate =
				local -> Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Function<Date, LocalDate> dateToLocal =
				date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		BiConsumer<T, LocalDate> setterL =
				(item, local) -> setter.accept(item, localToDate.apply(local));
		Function<T, LocalDate> getterL = item -> dateToLocal.apply(getter.apply(item));

		DatePicker node = new DatePicker();
		return createNode(getterL, setterL, item -> LocalDate.now(), node,
				(value) -> node.setValue(value), () -> node.getValue(),
				() -> node.setValue(LocalDate.now()));
	}

	

	public <U> ComboBoxCreator<U> createComboBox(Class<U> subItemClass, Function<T, U> getter,
			BiConsumer<T, U> setter) {
		ComboBox<U> controlNode = new ComboBox<U>();
		return createComboBox(controlNode, subItemClass, getter, setter);
	}
	
	public <U> ComboBoxCreator<U> createComboBox(ComboBox<U> controlNode,
			Function<T, U> getter, BiConsumer<T, U> setter) {
		return new ComboBoxCreator<U>(controlNode, getter, setter);
	}

	public <U> ComboBoxCreator<U> createComboBox(ComboBox<U> controlNode, Class<U> subItemClass,
			Function<T, U> getter, BiConsumer<T, U> setter) {
		List<U> items = Connectors.get(subItemClass).readAllAsList();
		controlNode.setItems(FXCollections.observableList(items));
		FxUtil.setComboBoxCellFactory(controlNode, o -> o.toString());
		return new ComboBoxCreator<U>(controlNode, getter, setter);
	}

	public class ComboBoxCreator<U> extends AbstractEditor<T, U> {
		private final ComboBox<U> controlNode;
		private final Function<T, U> getter;
		private final BiConsumer<T, U> setter;

		public ComboBoxCreator(ComboBox<U> controlNode, Function<T, U> getter,
				BiConsumer<T, U> setter) {
			super(itemClass, controlNode);
			this.controlNode = controlNode;
			this.getter = getter;
			this.setter = setter;
		}

		public ComboBoxCreator<U> setMapper(Function<U, String> mapper) {
			FxUtil.setComboBoxCellFactory(controlNode, mapper);
//			Callback<ListView<U>, ListCell<U>> cellFactory =
//					FxUtil.createComboBoxCellFactory(mapper);
//			controlNode.setButtonCell(cellFactory.call(null));
//			controlNode.setCellFactory(cellFactory);
			return this;
		}

		public ComboBoxCreator<U> addSelectListener(ChangeListener<? super U> listener) {
			controlNode.getSelectionModel().selectedItemProperty().addListener(listener);
			return this;
		}

		@Override
		protected U entityToValueEditGetterImpl(T item) {
			return getter.apply(item);
		}

		@Override
		protected U entityToValueAddGetterImpl(T item) {
			controlNode.getSelectionModel().clearSelection();
			return null;
		}

		@Override
		protected void valueToEntitySetterImpl(T item, U value) {
			setter.accept(item, value);
		}

		@Override
		protected U nodeToValueGetterImpl() {
			return controlNode.getSelectionModel().getSelectedItem();
		}

		@Override
		protected void valueToNodeSetterImpl(U value) {
			selectComboBoxItem(controlNode, value);
		}

		@Override
		protected void defaultSetterRunnableImpl() {
			controlNode.getSelectionModel().clearSelection();
			FxUtil.setComboBoxCellFactory(controlNode, item -> "");
//			Callback<ListView<U>, ListCell<U>> cellFactory =
//					FxUtil.createComboBoxCellFactory(item -> "");
//			controlNode.setButtonCell(cellFactory.call(null));
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

	public <U, N extends Node> AbstractEditor<T, U> createNode(
			Function<T, U> entityToValueEditGetter, BiConsumer<T, U> valueToEntitySetter,
			Function<T, U> entityToValueAddGetter, N controlNode, Consumer<U> valueToNodeSetter,
			Supplier<U> nodeToValueGetter, Runnable defaultSetterRunnable) {
		return new AbstractEditor<T, U>(itemClass, controlNode) {

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
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public VBox getvBox() {
		return vBox;
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public List<IPropertyNode> getChildrenProperties() {
		return propertyNodes;
	}

}
