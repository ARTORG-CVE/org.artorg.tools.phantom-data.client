package org.artorg.tools.phantomData.client.specification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class Table<TABLE extends Table<TABLE, ITEM, ID_TYPE>, ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE> 
		extends UndoManager {
	private final ObservableList<ITEM> items;
	private final List<List<Object>> data;
	private final List<PropertyUndoable<ITEM,ID_TYPE, Object>> properties;
	private final List<Function<ITEM,Object>> getters;
	private final List<BiConsumer<ITEM, Object>> setters;
	private final UndoManager undoManager;
	
	{
		Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
		items = FXCollections.observableArrayList(itemSet);
		data  = new ArrayList<List<Object>>();
		undoManager = new UndoManager();
		properties = createProperties();
		getters = createGetters();
		setters = createSetters();
	}
	
//	public abstract List<String> createColumnNames();
	
	public abstract HttpDatabaseCrud<ITEM, ID_TYPE> getConnector();
	
//	public abstract List<PropertyUndoable<ITEM,ID_TYPE, Object>> createProperties();
	
//	public abstract List<Function<ITEM,Object>> createPropertyGetters();
	
	public abstract List<Column<ITEM, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2();
	
	public List<TableColumn<ITEM,?>> createColumns() {
		List<TableColumn<ITEM,?>> columns = new ArrayList<TableColumn<ITEM,?>>();
		List<String> columnNames = createColumnNames();
		
		int nCols = getNumOfColumns();
		for ( int i=0; i<nCols; i++) {
			TableColumn<ITEM, String> column = new TableColumn<ITEM, String>(columnNames.get(i));
			int j = i;
		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
		    		String.valueOf(getValue(cellData.getValue(), j))));
		    columns.add(column);
		}
		
	    return columns;
	}
	
	public ObservableList<ITEM> getItems() {
		return items;
	}
	
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	public final List<Object> getValues(ITEM item) {
		return getGetters().stream().map(g -> g.apply(item))
				.collect(Collectors.toList());
	}
	
	public PropertyUndoable<ITEM, ID_TYPE, Object> createProperty(BiConsumer<ITEM,Object> setter, Function<ITEM,Object> getter) {
		return new PropertyUndoable<ITEM, ID_TYPE, Object>(setter, getter, getConnector(), undoManager);
	}
	
	public final List<Function<ITEM,Object>> createGetters() {
		List<PropertyUndoable<ITEM, ID_TYPE, Object>> properties = getProperties();
		return properties.stream().map(p -> p.getGetter()).collect(Collectors.toList());
	}
	
	public final List<BiConsumer<ITEM, Object>> createSetters() {
		List<PropertyUndoable<ITEM, ID_TYPE, Object>> properties = getProperties();
		return properties.stream().map(p -> p.getSetter()).collect(Collectors.toList());
	}
	
	public final Object getValue(ITEM item, int col) {
		return getValues(item).get(col);
	}
	
	public final void setValue(ITEM item, int col, Object value) {
		getSetters().get(col).accept(item, value);
	}
	
	public int getNumOfColumns() {
		return createColumnNames().size();
	}
	
	public Object getValue(int row, int col) {
		return data.get(row+1).get(col);
		
//		return getValue(items.get(row),col);
	}
	
	public void setValue(int row, int col, Object value) {
		data.get(row+1).set(col, value);
//		setValue(items.get(row), col, value);
	}
	
	public void createData() { 
        int nCols = getNumOfColumns();
        data.add(createColumnNames().stream().map(s -> (Object)s)
        		.collect(Collectors.toList()));
        
        List<ITEM> items = new ArrayList<ITEM>();
		items.addAll(getConnector().readAllAsList());
        
        for (int row=1; row<items.size(); row++) {
        	List<Object> testList2 = new ArrayList<Object>();
            for (int col = 0; col < nCols; col++) {
            	Object value = getValue(items.get(row), col);
            	if ( value instanceof String) 
            		testList2.add((String)value);
            }
            data.add(testList2);
        }

	}
	
	public TableView<ITEM> createTableView(TableView<ITEM> table) {
		table.getColumns().removeAll(table.getColumns());

	    List<TableColumn<ITEM,?>> columns = createColumns();
	    table.getColumns().addAll(columns);
	    
	    Set<ITEM> itemSet = new HashSet<ITEM>();
		itemSet.addAll(getConnector().readAllAsSet());
	    ObservableList<ITEM> items = FXCollections.observableArrayList(itemSet);
	    ObservableList<ITEM> data = FXCollections.observableArrayList(items);
	    table.setItems(data);
	    
	    this.setSortOrder(table);
	    this.autoResizeColumns(table);
	    
		return table;
	}
	
	public void setSortOrder(TableView<ITEM> table) {
		table.getSortOrder().addAll(table.getColumns());
	}
	
	public void autoResize(TableView<ITEM> table, Stage stage) {
		double width = table.getColumns().stream().mapToDouble(c -> c.getPrefWidth()).sum();
		stage.setWidth(width + 17.0d + 50.0d);
	}
	
	public void autoResizeColumns(TableView<ITEM> table) {
	    table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
	    table.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth();
	        for ( int i = 0; i < table.getItems().size(); i++ ) {
	            if ( column.getCellData( i ) != null ) {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth();
	                if ( calcwidth > max )
	                    max = calcwidth;
	            }
	        }
	        column.setPrefWidth( max + 35.0d );
	    } );
	}
	
	public TableView<ITEM> createTableView() {
		return createTableView(new TableView<ITEM>());
	}
	
	public SpreadsheetView createSpreadsheetView() {
		return createSpreadsheetView(createTableView());
	}
	
	
	
	public SpreadsheetView createSpreadsheetView(TableView<ITEM> table) {
		// create Grid
		int rowCount = table.getItems().size()+1;
        int columnCount = table.getColumns().size();
        GridBase grid = new GridBase(rowCount, columnCount);
        
        
        List<String> columnNames = table.getColumns().stream()
    			.map(tc -> tc.getText()).collect(Collectors.toList());
        final ObservableList<SpreadsheetCell> list1 = FXCollections.observableArrayList();
        
        List<Object> testList = new ArrayList<Object>();
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        
        for (int col = 0; col < columnCount; col++) {
        	list1.add(SpreadsheetCellType.STRING.createCell(0, col, 1, 1,columnNames.get(col)));
        	testList.add(columnNames.get(col));
        }
        rows.add(list1);
        data.add(testList);
        
        
        
        Integer row=0;
        while (row<items.size()) {
        	final ObservableList<SpreadsheetCell> list2 = FXCollections.observableArrayList();
        	List<Object> testList2 = new ArrayList<Object>();
        	
            for (int col = 0; col < columnCount; col++) {
            	Object value = table.getColumns().get(col).getCellData(items.get(row));
            	if ( value instanceof String) {
            		SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
            		TextField label = new TextField();
            		
            		label.setText((String)value);
            		testList2.add((String)value);
            		
            		final int tempRow = row;
            		final int tempCol = col;
            		label.setOnAction((event) -> {
            			data.get(tempRow+1).set(tempCol, label.getText());
            			this.setValue(items.get(tempRow), tempCol, label.getText());
            			data.stream().flatMap(l -> l.stream()).forEach(System.out::println);
            			properties.get(tempCol).set(items.get(tempRow), label.getText());
            			
            			System.out.println("--//--//--//--// items in Table start");
            			items.stream().forEach(System.out::println);
            			System.out.println("--//--//--//--// items in Table end");
            			
            			System.out.println("--//--//--//--// data in Table start");
            			data.stream().forEach(l -> l.stream().forEach(System.out::println));
            			System.out.println("--//--//--//--// data in Table end");
            		});
            		cell.setGraphic(label);
            		list2.add(cell);
            	}
//            	else if ( value instanceof Boolean) {
//            		SpreadsheetCellBase cell = new SpreadsheetCellBase(row, col, 1, 1);
//            		CheckBox checkBox = new CheckBox();
//            		checkBox.setSelected((boolean)value);
//            		cell.setGraphic(checkBox);
//            		list2.add(cell);
//            	}
//            	else {
//            		list2.add(SpreadsheetCellType.STRING.createCell(row, col, 1, 1, String.valueOf(value)));
//                
//                
//            	}
            }
            rows.add(list2);
            data.add(testList2);
            row++;
        }
        grid.setRows(rows);
		        
        // create SpreadsheetView
        SpreadsheetView spreadsheet = new SpreadsheetView();
        
        spreadsheet.setGrid(grid);
        
        return spreadsheet; 
	}

	public List<PropertyUndoable<ITEM, ID_TYPE, Object>> getProperties() {
		return properties;
	}
	
	public List<Function<ITEM, Object>> getGetters() {
		return getters;
	}

	public List<BiConsumer<ITEM, Object>> getSetters() {
		return setters;
	}

	public List<List<Object>> getData() {
		return data;
	}

}
