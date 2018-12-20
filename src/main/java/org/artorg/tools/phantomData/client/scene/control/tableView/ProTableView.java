package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.TableViewUtils;
import org.artorg.tools.phantomData.server.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ProTableView<T> extends javafx.scene.control.TableView<T> implements AddableToPane {
	private BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>> columnAddPolicy;
	private BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>> columnRemovePolicy;
	private final Class<T> itemClass;
	private TableBase<T> table;
	private List<FilterMenuButton<T, ?>> filterMenuButtons;

	{
		columnAddPolicy = (fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
		columnRemovePolicy =
				(fromColumn, toColumn) -> toColumn.getText().equals(fromColumn.getName());
		filterMenuButtons = new ArrayList<>();
	}

	public ProTableView(Class<T> itemClass) {
		this(itemClass, Main.getUIEntity(itemClass).createTableBase());
		if (!isFilterable()) super.setItems(getTable().getItems());
		else
			super.setItems(getTable().getFilteredItems());

		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		refreshColumns();
		autoResizeColumns();
		super.getSelectionModel().selectFirst();

		showFilterButtons();
		super.getSelectionModel().selectFirst();
		refresh();
		Platform.runLater(() -> {

			refresh();
		});
		refresh();
	}

	protected ProTableView(Class<T> itemClass, TableBase<T> table) {
		this.itemClass = itemClass;
		this.table = table;
	}

	@Override
	public void refresh() {
		Logger.debug.println(getItemClass());
		getTable().refresh();

		if (isFilterable()) {
			getFilterMenuButtons().forEach(filterMenuButton -> {
				filterMenuButton.refreshImage();
			});
		}

		refreshColumns();
//		super.refresh();
	}

	public void refreshColumns() {
		Logger.debug.println(getItemClass().getSimpleName());
		if (!isFilterable()) {
			CollectionUtil.addIfAbsent(table.getColumnCreator().apply(getItems()),
					super.getColumns(), columnAddPolicy,
					(baseColumn, index) -> createTableColumn(table, index));

			CollectionUtil.removeIfAbsent(table.getColumnCreator().apply(getItems()),
					super.getColumns(), columnRemovePolicy);
		} else {
			CollectionUtil.addIfAbsent(
					getTable().getColumnCreator().apply(table.getFilteredItems()),
					super.getColumns(), getColumnAddPolicy(),
					(baseColumn, index) -> createTableColumn(table, index));
			CollectionUtil.removeIfAbsent(
					getTable().getColumnCreator().apply(table.getFilteredItems()),
					super.getColumns(), getColumnRemovePolicy());
		}
	}

	public void removeHeaderRow() {
		this.getStyleClass().remove("header");
		this.getStyleClass().add("noheader");
	}

	public void showHeaderRow() {
		this.getStyleClass().remove("noheader");
		this.getStyleClass().add("header");

		if (isFilterable()) {
			showFilterButtons();
			getFilterMenuButtons().forEach(filterMenuButton -> {
				filterMenuButton.refreshImage();
			});
		}
	}

	public void showFilterButtons() {
		if (!isFilterable()) return;
		for (Node n : super.lookupAll(".column-header > .label"))
			if (n instanceof Label) {
				Label label = (Label) n;

				String columnName = label.getText();
				Optional<FilterMenuButton<T, ?>> filterMenuButton = getFilterMenuButtons().stream()
						.filter(f -> f.getText().equals(columnName)).findFirst();
				if (filterMenuButton.isPresent()) {
					filterMenuButton.get().prefWidthProperty().bind(label.widthProperty());
					filterMenuButton.get().getStyleClass().add("filter-menu-button");
					label.setGraphic(filterMenuButton.get());
					label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//					label.addEventHandler(eventType, eventHandler);
					
					filterMenuButton.get().refreshImage();

				}
			}
	}

	protected TableColumn<T, ?> createTableColumn(TableBase<T> table, int index) {
		if (!isFilterable()) {
			TableColumn<T, Object> tableColumn =
					new TableColumn<T, Object>(table.getColumns().get(index).getName());

			tableColumn.setSortable(false);
			tableColumn.setCellFactory(createCellFactory(tableColumn));
			tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
					cellData -> table.getValue(cellData.getValue(), index)));
			return tableColumn;
		} else {
			AbstractColumn<T, ?> baseColumn = table.getFilteredColumns().get(index);
			TableColumn<T, Object> tableColumn = new TableColumn<T, Object>(baseColumn.getName());
			tableColumn.setSortable(false);

			if (baseColumn instanceof AbstractFilterColumn) {
				AbstractFilterColumn<T, ?> filterColumn = (AbstractFilterColumn<T, ?>) baseColumn;
				filterColumn.setSortComparatorQueue(table.getSortComparatorQueue());

				filterColumn.setItems(getItems());
				getFilterMenuButtons().add(createFilterMenuButton(filterColumn, index));
			}

			tableColumn.setSortable(false);
			tableColumn.setCellFactory(createCellFactory(tableColumn));
			tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
					cellData -> table.getFilteredValue(cellData.getValue(), index)));
			return tableColumn;
		}
	}

	public <U> FilterMenuButton<T, ?>
			createFilterMenuButton(AbstractFilterColumn<T, U> filterColumn, int index) {

		FilterMenuButton<T, ?> filterMenuButton =
				new FilterMenuButton<T, U>(filterColumn, filterMenuButtons);
		filterMenuButton.setText(filterColumn.getName());
		filterMenuButton.setRefresh(() -> {
			table.applyFilter();
//			System.out.println("items before filtering: " +super.getItems().size());
//			super.getItems().clear();
//			super.getItems().addAll(table.getFilteredItems());
//			System.out.println("items after filtering: " +super.getItems().size());
//			super.refresh();
		});
		return filterMenuButton;
	}

	protected <U> Callback<TableColumn<T, U>, TableCell<T, U>>
			createCellFactory(TableColumn<T, U> tableColumn) {
		return column -> new TableCell<T, U>() {
			@Override
			protected void updateItem(U item, boolean empty) {
				if (item == null) {
					setText("");
					setGraphic(null);
					return;
				}
				if (item instanceof Node) {
					Node node = (Node) item;
					setGraphic(node);
					Platform.runLater(() -> {
						tableColumn.setPrefWidth(this.getTableRow().getHeight());
					});
				} else {
					setText(item.toString());
				}
			}
		};
	}

	protected <U> Callback<CellDataFeatures<T, U>, ObservableValue<U>> createCellValueFactory(
			TableColumn<T, U> tableColumn, Function<CellDataFeatures<T, U>, U> valueGetter) {
		return cellData -> new ReadOnlyObjectWrapper<U>(valueGetter.apply(cellData));
	}

//	protected TableColumn<ITEM, ?> createHeaderColumn() {
//		TableColumn<ITEM, ?> headerColumn = new TableColumn<ITEM, Object>();
//
////		headerColumn.setCellFactory(col -> {
////			TableCell<ITEM, String> cell = new TableCell<ITEM, String>();
////			cell.getStyleClass().add("row-header-cell");
////			return cell;
////		});
//
//		headerColumn.setSortable(false);
//		return headerColumn;
//	}

//	public void setTable(TableBase<T> table) {
//		this.table = table;
//		initTable();
//	}

	public BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>>
			getColumnAddPolicy() {
		return columnAddPolicy;
	}

	public void setColumnAddPolicy(
			BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>> addColumnPolicy) {
		this.columnAddPolicy = addColumnPolicy;
	}

	public BiPredicate<AbstractColumn<T, ? extends Object>, TableColumn<T, ?>>
			getColumnRemovePolicy() {
		return columnRemovePolicy;
	}

	public void setColumnRemovePolicy(BiPredicate<AbstractColumn<T, ? extends Object>,
			TableColumn<T, ?>> removeColumnPolicy) {
		this.columnRemovePolicy = removeColumnPolicy;
	}

	public boolean isFilterable() {
		return getTable().isEditable();
	}

	public void autoResizeColumns() {
		TableViewUtils.autoResizeColumns(this);
	}

	public TableBase<T> getTable() {
		return table;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public List<FilterMenuButton<T, ?>> getFilterMenuButtons() {
		return filterMenuButtons;
	}

}
