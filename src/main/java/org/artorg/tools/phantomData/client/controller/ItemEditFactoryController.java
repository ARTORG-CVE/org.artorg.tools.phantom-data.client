package org.artorg.tools.phantomData.client.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.connector.PersonalizedHttpConnectorSpring;
import org.artorg.tools.phantomData.client.scene.control.TitledPaneTableViewSelector;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.artorg.tools.phantomData.server.util.Reflect;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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

public abstract class ItemEditFactoryController<ITEM extends DbPersistent<ITEM,?>> implements FxFactory<ITEM> {
	private GridPane gridPane;
	protected Button applyButton;
	private int nRows = 0;
	private List<Node> rightNodes;
	private List<AbstractTableViewSelector<ITEM>> selectors;
	private ProTableView<ITEM> table;
	private AnchorPane pane;
	
	{
		gridPane = new GridPane();
		applyButton = new Button("Apply");
	}
	
	public abstract ITEM createItem();
	
	protected abstract void setTemplate(ITEM item);
	
	protected abstract void copy(ITEM from, ITEM to);
	
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
		
		List<Class<? extends DbPersistent<?,?>>> subItemClasses = Reflect.getCollectionSetterMethods(itemClass)
				.map(m -> {
					Type type = m.getGenericParameterTypes()[0];
					Class<? extends DbPersistent<?,?>> cls = (Class<? extends DbPersistent<?, ?>>) Reflect.getGenericTypeClass(type);
					return cls;
				})
				.filter(c -> c != null)
				.filter(c -> DbPersistent.class.isAssignableFrom(c))
				.collect(Collectors.toList());
		
		subItemClasses.forEach(subItemClass -> {
			if (Reflect.containsCollectionSetter(itemClass, subItemClass)) {
				ICrudConnector<?,?> connector = Connectors.getConnector(subItemClass);
				Set<Object> selectableItemSet = (Set<Object>) connector.readAllAsSet();
				
				if (selectableItemSet.size() > 0) {
					AbstractTableViewSelector<ITEM> titledSelector = new TitledPaneTableViewSelector<ITEM>(subItemClass);
					titledSelector.setSelectableItems(selectableItemSet);
					
					Method selectedMethod = Reflect.getMethodByGenericReturnType(itemClass, subItemClass);
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
					
					titledSelector.init();
					selectors.add(titledSelector);
				}
			}
		});
		
		return selectors;
	}
		
	@SuppressWarnings("unchecked")
	public final ICrudConnector<ITEM,?> getConnector() {
		if (getTableView().getTable() instanceof IDbTable)
			return ((IDbTable<ITEM>)getTableView().getTable()).getConnector();
		return null;
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
			Class<T> itemClass, Function<T,String> mapper) {
		createComboBox(comboBox, itemClass, mapper, item -> {});
	}
	@SuppressWarnings("unchecked")
	protected <T extends DbPersistent<T,ID>, ID> void createComboBox(ComboBox<T> comboBox, 
			Class<T> itemClass, Function<T,String> mapper, Consumer<T> selectedItemChangedConsumer) {
		ICrudConnector<T,ID> connector = (ICrudConnector<T, ID>) PersonalizedHttpConnectorSpring.getOrCreate(itemClass);
    	createComboBox(comboBox, connector, mapper);
        
        ChangeListener<T> listener = (observable, oldValue, newValue) -> {
        	try {
        		selectedItemChangedConsumer.accept(newValue);
        	} catch(Exception e) {}
        };
        comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }
	
	protected <T extends DbPersistent<T,ID>, ID> void createComboBox(ComboBox<T> comboBox, 
			ICrudConnector<T,ID> connector, Function<T,String> mapper) {
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
			setTemplate(item);
		applyButton.setOnAction(event -> {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					ITEM newItem = createItem();
					selectors.forEach(selector -> selector.setSelectedChildItems(item));
					getTableView().getItems().add(newItem);
					getConnector().create(newItem);
					initDefaultValues();
					return null;
				}
			};
			task.setOnSucceeded(taskEvent -> {
			});
			ExecutorService executor = Executors.newSingleThreadExecutor(); 
			executor.submit(task);
			try {
				executor.awaitTermination(30, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
			setTemplate(item);
		applyButton.setOnAction(event -> {
			ITEM item2 = createItem();
			copy(item2,item);
			selectors.forEach(selector -> selector.setSelectedChildItems(item));
//			this.getTable().refresh();
			
			System.out.println("connector: " +getConnector());
			System.out.println("item: " +item);
			
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
