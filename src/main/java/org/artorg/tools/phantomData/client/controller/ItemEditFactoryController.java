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

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.scene.control.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.specification.DbPersistentUUID;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public abstract class ItemEditFactoryController<ITEM extends DbPersistent<ITEM,?>> {
	private GridPane gridPane;
	protected Button applyButton;
	private int nRows = 0;
	private List<Node> rightNodes;
	private List<ISelector<ITEM, Object>> selectors;
	
	{
		gridPane = new GridPane();
		applyButton = new Button("Apply");
	}
	
	public abstract ITEM createItem();
	
	protected abstract void setTemplate(ITEM item);
	
	protected abstract void copy(ITEM from, ITEM to);
	
	protected abstract DbEditFilterTableView<ITEM> getTable();
	
	protected abstract AnchorPane createRootPane();
	
	protected abstract void addProperties(ITEM item);
	
	public abstract List<PropertyEntry> getPropertyEntries();
	
	protected void setSelectedChildItems(ITEM item, ISelector<ITEM, Object> selector) {
		Class<?> paramTypeClass = selector.getSelectedItems().getClass();
		Object arg = selector.getSelectedItems();
		Reflect.invokeGenericSetter(item, paramTypeClass, selector.getSubItemClass(), arg);
	}
	
	public List<ISelector<ITEM, Object>> getSelectors() {
		return selectors;
	}
	
	@SuppressWarnings("unchecked")
	private List<ISelector<ITEM, Object>> createSelectors(ITEM item) {
		List<ISelector<ITEM, Object>> selectors = new ArrayList<ISelector<ITEM, Object>>();
		
		List<Class<? extends DbPersistent<?,?>>> subItemClasses = Reflect.getCollectionSetterMethods(item.getClass())
				.map(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<? extends DbPersistent<?,?>> cls = (Class<? extends DbPersistent<?, ?>>) Reflect.getGenericTypeClass(type);
					return cls;
				})
				.filter(c -> c != null)
				.filter(c -> DbPersistentUUID.class.isAssignableFrom(c))
				.collect(Collectors.toList());
		
		subItemClasses.forEach(subItemClass -> {
			if (Reflect.containsCollectionSetter(item, subItemClass)) {
				CrudConnector<?,?> connector = Connectors.getConnector(subItemClass);
				Set<Object> selectableItemSet = (Set<Object>) connector.readAllAsSet();
				
				if (selectableItemSet.size() > 0) {
					TitledPaneTableViewSelector<ITEM> titledSelector = new TitledPaneTableViewSelector<ITEM>();
//					TitledPaneTableViewSelector2<ITEM> titledSelector = new TitledPaneTableViewSelector2<ITEM>(subItemClass);
					titledSelector.setSubItemClass((Class<?>) subItemClass);
					titledSelector.setSelectableItems(selectableItemSet);
					
					Method selectedMethod = Reflect.getMethodByGenericReturnType(item, subItemClass);
					Function<ITEM, Collection<Object>> subItemGetter2; 
					subItemGetter2 = i -> {
						try {
							return (Collection<Object>)(selectedMethod.invoke(i));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
						return null;
					};
					titledSelector.setSelectedItems(subItemGetter2.apply(item).stream().collect(Collectors.toSet()));
					
					titledSelector.getTitledPane().setText(subItemClass.getSimpleName());
					
					titledSelector.init();
					selectors.add(titledSelector);
				}
			}
		});
		
		return selectors;
	}
		
	public final CrudConnector<ITEM,?> getConnector() {
		return getTable().getConnector();
	}
	
	protected void initDefaultValues() {
		rightNodes.forEach(node -> {
			if (node instanceof ComboBox)
				((ComboBox<?>)node).getSelectionModel().clearSelection();
			else if (node instanceof TextField)
				((TextField)node).setText("");
			else if (node instanceof CheckBox)
				((CheckBox)node).setSelected(false);
		});
	}
	
	protected <T extends Comparable<T>> void selectComboBoxItem(ComboBox<T> comboBox, T item) {
		for (int i=0; i<comboBox.getItems().size(); i++)
			if (comboBox.getItems().get(i).compareTo(item) == 0) {
				comboBox.getSelectionModel().select(i);
				break;
			}
	}
	
	protected <T extends DbPersistent<T,ID>, ID> void createComboBox(ComboBox<T> comboBox, 
			CrudConnector<T,ID> connector, Function<T,String> mapper, Consumer<T> selectedItemChangedConsumer) {
    	createComboBox(comboBox, connector, mapper);
        
        ChangeListener<T> listener = (observable, oldValue, newValue) -> {
        	try {
        		selectedItemChangedConsumer.accept(newValue);
        	} catch(Exception e) {}
        };
        comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }
	
	protected <T extends DbPersistent<T,ID>, ID> void createComboBox(ComboBox<T> comboBox, 
			CrudConnector<T,ID> connector, Function<T,String> mapper) {
    	List<T> fabricationType = connector.readAllAsStream()
        		.distinct().collect(Collectors.toList());
    	comboBox.setItems(FXCollections.observableList(fabricationType));
    	comboBox.getSelectionModel().selectFirst();
        Callback<ListView<T>, ListCell<T>> cellFactory = createComboBoxCellFactory(mapper);
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
    }
    
    protected <T> Callback<ListView<T>, ListCell<T>> createComboBoxCellFactory(Function<T,String> mapper) {
    	return param -> {
        	return new ListCell<T>() {
				@Override
	            protected void updateItem(T item, boolean empty) {
	                super.updateItem(item, empty);
	                if (item == null || empty) {
	                    setGraphic(null);
	                } else {
	                    setText(mapper.apply(item));
	                }
	            }
			};
        };
    }
    
    protected void addProperty(String labelText, TextField textField, Runnable rc) {
    	textField.textProperty().addListener(event -> {
    		rc.run();
    	});
    	addProperty(labelText, textField);
    	
    }
    
    protected void addProperty(String labelText, Control node) {
    	int row = nRows;
    	nRows++;
    	
    	gridPane.add(new Label(labelText), 0, row, 1, 1);
		gridPane.add(node, 1, row, 1, 1);
		GridPane.setHgrow(node, Priority.ALWAYS);
		node.setMaxWidth(Double.MAX_VALUE);
    }
    
    public AnchorPane create() {
		return create(null);
    }
    
    public AnchorPane create(ITEM item) {
    	selectors = createSelectors(item);
		addProperties(item); 
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		AnchorPane pane = createRootPane();
		if (item != null)
			setTemplate(item);
		applyButton.setOnAction(event -> {
			ITEM newItem = createItem();
			selectors.forEach(selector -> selector.setSelectedChildItems(item));
			this.getTable().getItems().add(newItem);
			getConnector().create(newItem);
			this.initDefaultValues();
		});
		applyButton.setText("Create");
		return pane;
    }
    
    public AnchorPane edit(ITEM item) {
    	selectors = createSelectors(item);
    	Label label = new Label();
    	label.setText(item.getId().toString());
    	label.setDisable(true);
    	PropertyEntry idEntry = new PropertyEntry("Id", label);
    	getPropertyEntries().add(idEntry);
		addProperties(item); 
		createRightNodes(getPropertyEntries());
		initDefaultValues();
		AnchorPane pane = createRootPane();
		if (item != null)
			setTemplate(item);
		applyButton.setOnAction(event -> {
			ITEM item2 = createItem();
			copy(item2,item);
			selectors.forEach(selector -> selector.setSelectedChildItems(item));
			this.getTable().refresh();
			getConnector().update(item);
		});
		
		applyButton.setText("Save");
		return pane;
    }
    
    private void createRightNodes(List<PropertyEntry> entries) {
    	rightNodes = entries.stream().map(e -> e.getRightNode()).collect(Collectors.toList());
    }
    
    protected AnchorPane createButtonPane(Button button) {
    	button.setPrefHeight(25.0);
    	button.setMaxWidth(Double.MAX_VALUE);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPrefHeight(button.getPrefHeight()+20);
		buttonPane.setMaxHeight(buttonPane.getPrefHeight());
		buttonPane.setPadding(new Insets(10, 10, 10, 10));
		buttonPane.getChildren().add(button);
		FxUtil.setAnchorZero(button);
		return buttonPane;
    }
    
    public GridPane getGridPane() {
		return gridPane;
	}
    
    protected void setGridPane(GridPane gridPane) {
    	this.gridPane = gridPane;
    }
    
}
