package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.multiSelectCombo.FilterBox;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.spreadsheet.Filter;
import org.controlsfx.control.spreadsheet.FilterBase;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SpreadsheetViewCrud<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE>
			implements TableGui<TABLE, ITEM, ID_TYPE> {

	private SpreadsheetView spreadsheet;
	private Table<TABLE, ITEM, ID_TYPE> table;
	private ListChangeListener<ITEM> changeListener;
		
	@Override
	public void setTable(Table<TABLE, ITEM, ID_TYPE> table) {
//		changeListener = new ListChangeListener<ITEM>() {
//			@Override
//			public void onChanged(Change<? extends ITEM> c) {
//				refresh();
//			}
//		};
		
		changeListener = (item) -> refresh();
		
		
		table.getItems().addListener(changeListener);
		this.table = table;
		
		reload();
		
		
	}
	
	@Override
	public void autoResizeColumns() {
		// TODO Auto-generated method stub

	}

	@Override
	public Control getGraphic() {
		return spreadsheet;
	}

	@Override
	public void refresh() {
		spreadsheet = new SpreadsheetView();
		
		// create Grid
		int rowCount = table.getNrows() + 1 ;
		int columnCount = table.getNcols();
		GridBase grid = new GridBase(rowCount, columnCount);

		List<String> columnNames = table.getColumnNames();

		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
		
		
//		final ObservableList<SpreadsheetCell> rowColumnNames = FXCollections.observableArrayList();
//		for (int col = 0; col < columnCount; col++) {
//			SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(0, col, 1, 1, columnNames.get(col));
//			cell.setEditable(false);
//			rowColumnNames.add(cell);
//		}
//		rows.add(rowColumnNames);
		
		
		
		
		
		final ObservableList<SpreadsheetCell> rowItemFilter = FXCollections.observableArrayList();
		for (int col=0; col < columnCount; col++) {
			String value = table.getColumnNames().get(col);
			SpreadsheetCellBase cell = new SpreadsheetCellBase(0, col, 1, 1);
//			TextField label = new TextField();
			
			List<Runnable> getters = new ArrayList<Runnable>();
			for (int row=0; row<table.getNrows(); row++) {
				final int localRow = row;
				final int localCol = col;
				getters.add(() -> table.getValue(localRow, localCol));
			}
			
			
			FilterBox filterBox = new FilterBox(value, getters);
			
			
			
			
			
			
			
			
			
//			
//			ComboBox<Node> comboBox = new ComboBox<Node>();
//			comboBox.setPromptText(value);
//			comboBox.setStyle("-fx-background-color: transparent;");
//			
//			
//			Button buttonA = new Button("Sort Ascending");
//			buttonA.setStyle("-fx-background-color: transparent;");
////			buttonA.setPrefHeight(30);
//			Button buttonD = new Button("Sort Descending");
////			buttonD.setPrefHeight(30);
//			buttonD.setStyle("-fx-background-color: transparent;");
//			
//			comboBox.getItems().add(buttonA);
//			comboBox.getItems().add(buttonD);
//			
//			CheckBox checkBox = new CheckBox();
//			Label label = new Label();
//			
//			label.setText("Test");
//			
//			HBox hbox = new HBox();
//			
//			hbox.getChildren().add(checkBox);
//			hbox.getChildren().add(label);
//			
//			comboBox.getItems().add(hbox);
//			
//			
//			 // create the data to show in the CheckComboBox 
//			 final ObservableList<String> strings = FXCollections.observableArrayList();
//			 for (int i = 0; i <= 100; i++) {
//			     strings.add("Item " + i);
//			 }
//			 
//			 // Create the CheckComboBox with the data 
//			 final CheckComboBox<String> checkComboBox = new CheckComboBox<String>(strings);
//			 checkComboBox.sette
			
//			label.setText(value);
//			final int localCol = col;
			
//			label.setEditable(false);

//			label.focusedProperty().addListener(new ChangeListener<Boolean>() {
//			    @Override
//			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
//			        if (!newPropertyValue)
//			        	table.setValue(0, localCol, label.getText(),
//								s -> label.setText(s), 
//								s -> label.setText(s));
//			    }
//			});
//			label.setOnAction((event) -> {
//				table.setValue(0, localCol, label.getText(), 
//						s -> label.setText(s),
//						s -> label.setText(s));
//			});
			
			cell.setGraphic(filterBox);
			rowItemFilter.add(cell);
		}
		rows.add(rowItemFilter);
		
		
		
		
		
		
		
		
		for (int row=0; row < table.getNrows(); row++) {
			final ObservableList<SpreadsheetCell> rowItem = FXCollections.observableArrayList();

			for (int col=0; col < columnCount; col++) {
				String value = table.getValue(row, col);
				SpreadsheetCellBase cell = new SpreadsheetCellBase(row+1, col, 1, 1);
				TextField label = new TextField();
				
				label.setText(value);
				final int localRow = row;
				final int localCol = col;
				
//				label.setEditable(false);

				label.focusedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				        if (!newPropertyValue)
				        	table.setValue(localRow, localCol, label.getText(),
									s -> label.setText(s), 
									s -> label.setText(s));
				    }
				});
				label.setOnAction((event) -> {
					table.setValue(localRow, localCol, label.getText(), 
							s -> label.setText(s),
							s -> label.setText(s));
				});
				
				cell.setGraphic(label);
				rowItem.add(cell);
			}
			rows.add(rowItem);
		}
		grid.setRows(rows);

		spreadsheet.setGrid(grid);
		spreadsheet.setStyle("-fx-focus-color: transparent;");
		
//		spreadsheet.setFilteredRow(0);
//		for (int col=0; col < columnCount; col++) {
//			Filter filter = new FilterBase(spreadsheet, col);
//			spreadsheet.getColumns().get(col).setFilter(filter);
//		}
        
		
	}

	@Override
	public void reload() {
		table.getItems().removeListener(changeListener);
		table.readAllData();
		refresh();
		table.getItems().addListener(changeListener);
	}

}
