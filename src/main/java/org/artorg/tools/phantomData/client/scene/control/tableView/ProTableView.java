package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.scene.control.EntityView;
import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.client.util.TableViewUtils;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class ProTableView<T> extends javafx.scene.control.TableView<T>
		implements AddableToPane, EntityView {
	private final Class<T> itemClass;
	private Table<T> table;

	public ProTableView(Class<T> itemClass, Table<T> table) {
		this.itemClass = itemClass;
		this.table = table;
		super.setItems(table.getFilteredItems());

		if (isFilterable()) getTable().setFilterActivated(true);

		getItems().clear();
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		updateColumns();
		autoResizeColumns();

		sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue,
					Scene newValue) {
				if (newValue != null) {
					FxUtil.runNewSingleThreaded(() -> {
						Platform.runLater(() -> {
							showFilterButtons();
							getFilterMenuButtons()
									.forEach(filterMenuButton -> filterMenuButton.updateNodes());
						});
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(() -> {
							showFilterButtons();
						});
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(() -> {
							showFilterButtons();
						});
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(() -> {
							showFilterButtons();
						});
					});
				}
			}
		});
	}

	@Override
	public void refresh() {
		Logger.debug.println(getItemClass());
		getTable().refresh();

		updateColumns();

		Platform.runLater(() -> {
			showFilterButtons();
		});

		super.refresh();
	}

	@SuppressWarnings("unchecked")
	public void updateColumns() {
		long startTime = System.currentTimeMillis();
		getTable().updateColumns();
		List<AbstractColumn<T, ? extends Object>> baseTableColumns = table.getColumns();
		List<Integer> indexes = CollectionUtil.searchLeftNotInRight(super.getColumns(),
				baseTableColumns, (col1, col2) -> col1.getText().equals(col2.getName()));
		List<TableColumn<T, ?>> removableColumns =
				CollectionUtil.subList(super.getColumns(), indexes);
		super.getColumns().removeAll(removableColumns);

		for (int i = 0; i < baseTableColumns.size(); i++) {
			if (i == super.getColumns().size())
				super.getColumns().add(createTableColumn(baseTableColumns.get(i)));
			if (!super.getColumns().get(i).getText().equals(baseTableColumns.get(i).getName()))
				super.getColumns().add(i, createTableColumn(baseTableColumns.get(i)));
		}

		for (int i = 0; i < super.getColumns().size(); i++) {
			updateColumn((TableColumn<T, Object>) super.getColumns().get(i), table, i);
		}

		Logger.debug.println(String.format("%s - Columns updated in %d ms",
				getItemClass().getSimpleName(), System.currentTimeMillis() - startTime));
	}

	public void applyFilter() {
		getTable().applyFilter();
//		super.setItems(FXCollections.observableArrayList());
//		super.setItems(getTable().getFilteredItems());
	}

//	public void removeHeaderRow() {
//		this.getStyleClass().remove("header");
//		this.getStyleClass().add("noheader");
//	}

//	public void showHeaderRow() {
//		this.getStyleClass().remove("noheader");
//		this.getStyleClass().add("header");
//
//		if (isFilterable()) {
//			showFilterButtons();
//			getFilterMenuButtons().forEach(filterMenuButton -> {
//				filterMenuButton.refreshImage();
//			});
//		}
//	}

	public class TableBaseColumn<U, V> extends TableColumn<U, V> {
		private final AbstractColumn<U, ?> baseColumn;
		private final FilterMenuButton<U> filterMenuButton;

		public TableBaseColumn(AbstractColumn<U, ?> baseColumn) {
			super(baseColumn.getName());
			this.baseColumn = baseColumn;
			this.filterMenuButton = null;
		}

		public TableBaseColumn(AbstractFilterColumn<U, ?> baseColumn,
				FilterMenuButton<U> filterMenuButton) {
			super(baseColumn.getName());
			this.baseColumn = baseColumn;
			this.filterMenuButton = filterMenuButton;
		}

		public AbstractColumn<U, ?> getBaseColumn() {
			return baseColumn;
		}

		public FilterMenuButton<U> getFilterMenuButton() {
			return filterMenuButton;
		}

	}

	public void showFilterButtons() {
		Logger.debug.println("showFilterButtons");
		if (!isFilterable()) return;
		for (Node n : super.lookupAll(".column-header > .label"))
			if (n instanceof Label) {
				Label label = (Label) n;

				String columnName = label.getText();
				Optional<FilterMenuButton<T>> filterMenuButton = getFilterMenuButtons().stream()
						.filter(f -> f.getText().equals(columnName)).findFirst();
				if (filterMenuButton.isPresent()) {
					filterMenuButton.get().prefWidthProperty().bind(label.widthProperty());
					filterMenuButton.get().getStyleClass().add("filter-menu-button");
					label.setGraphic(filterMenuButton.get());
					label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//					label.addEventHandler(eventType, eventHandler);

					filterMenuButton.get().refreshImage();

				}
			} else {
				Logger.error.println("n not instance of Label");
			}
	}

	protected TableColumn<T, Object>
			createTableColumn(AbstractColumn<T, ? extends Object> baseColumn) {
		TableColumn<T, Object> tableColumn;
		if (isFilterable() && baseColumn instanceof AbstractFilterColumn) {
			AbstractFilterColumn<T, ?> filterColumn = (AbstractFilterColumn<T, ?>) baseColumn;
			tableColumn = new TableBaseColumn<>(filterColumn, createFilterMenuButton(filterColumn));
		} else
			tableColumn = new TableBaseColumn<>(baseColumn);
		tableColumn.setSortable(false);
		tableColumn.setCellFactory(createCellFactory(tableColumn));
		return tableColumn;
	}

	private void updateColumn(TableColumn<T, Object> tableColumn, Table<T> table, int index) {
		if (tableColumn instanceof TableBaseColumn) tableColumn
				.setText(((TableBaseColumn<T, Object>) tableColumn).getBaseColumn().getName());
		if (!isFilterable()) {
			tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
					cellData -> table.getValue(cellData.getValue(), index)));
		} else {
			tableColumn.setCellValueFactory(createCellValueFactory(tableColumn,
					cellData -> table.getFilteredValue(cellData.getValue(), index)));
		}
	}

	public <U> FilterMenuButton<T> createFilterMenuButton(AbstractFilterColumn<T, U> filterColumn) {
		FilterMenuButton<T> filterMenuButton =
				new FilterMenuButton<T>(filterColumn, () -> getFilterMenuButtons());
		filterMenuButton.setText(filterColumn.getName());
		filterMenuButton.setRefresh(() -> table.applyFilter());
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

	public boolean isFilterable() {
		return getTable().isEditable();
	}

	public void autoResizeColumns() {
		TableViewUtils.autoResizeColumns(this);
	}

	public Table<T> getTable() {
		return table;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	@SuppressWarnings("unchecked")
	public List<FilterMenuButton<T>> getFilterMenuButtons() {
		return getColumns().stream()
				.collect(StreamUtils.castFilter(column -> (TableBaseColumn<T, Object>) column))
				.map(column -> column.getFilterMenuButton())
				.filter(filterMenuButton -> filterMenuButton != null).collect(Collectors.toList());
	}

	@Override
	public Collection<Object> getSelectedItems() {
		return getSelectionModel().getSelectedItems().stream()
				.collect(Collectors.toCollection(() -> new ArrayList<Object>()));
	}

	@Override
	public Node getNode() {
		return this;
	}

}
