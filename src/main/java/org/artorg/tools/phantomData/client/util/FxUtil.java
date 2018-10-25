package org.artorg.tools.phantomData.client.util;

import static huma.io.IOutil.readResource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class FxUtil {
	private static Class<?> mainClass;

	static {
		mainClass = null;
	}
	
	public static TableColumn<Object, Void> createButtonCellColumn(String text, Consumer<Object> consumer) {
		TableColumn<Object, Void> column = new TableColumn<Object, Void>();
		column.setCellFactory(new Callback<TableColumn<Object, Void>, TableCell<Object, Void>>() {
			@Override
			public TableCell<Object, Void> call(final TableColumn<Object, Void> param) {
				return new TableCell<Object, Void>() {
					TableCell<Object, Void> cell = this;

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							cell.setGraphic(null);
							cell.setText("");
							cell.setOnMouseClicked(null);
						} else {
							cell.setOnMouseClicked(event -> consumer.accept(getTableView().getItems().get(cell.getIndex())));
							cell.setAlignment(Pos.CENTER);
							cell.setText(text);
						}
					}
				};
			}
		});
		double width = 15.0;
		column.setMinWidth(width);
		column.setPrefWidth(width);
		column.setMaxWidth(width);
		column.setSortable(false);
		
		return column;
	}

	public static <T extends DbPersistent<T, ID>, ID extends Comparable<ID>> void createDbComboBox(
		ComboBox<T> comboBox,
		ICrudConnector<T, ID> connector, Function<T, String> mapper) {
		List<T> items =
			connector.readAllAsStream().distinct().collect(Collectors.toList());
		comboBox.setItems(FXCollections.observableList(items));
		comboBox.getSelectionModel().selectFirst();
		Callback<ListView<T>, ListCell<T>> cellFactory =
			FxUtil.createComboBoxCellFactory(mapper);
		comboBox.setButtonCell(cellFactory.call(null));
		comboBox.setCellFactory(cellFactory);
	}

	public static <T> Callback<ListView<T>, ListCell<T>>
		createComboBoxCellFactory(Function<T, String> mapper) {
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

	public static void addMenuItem(ContextMenu rowMenu,
		String name,
		EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(eventHandler);
		rowMenu.getItems().add(menuItem);
	}

	public static void setMainFxClass(Class<?> mainClass) {
		if (FxUtil.mainClass != null) throw new UnsupportedOperationException();
		FxUtil.mainClass = mainClass;
	}

	public static <T> T loadFXML(String path, Object controller) {
		FXMLLoader loader =
			new FXMLLoader(getMainClass().getClassLoader().getResource(path));
		loader.setController(controller);
		try {
			return loader.<T>load();
		} catch (IOException e) {
		}
		throw new IllegalArgumentException("path: " + path);
	}

	public static String readCSSstylesheet(String path) {
		return readResource(path).toExternalForm();
	}

	public static Class<?> getMainClass() {
		if (FxUtil.mainClass == null)
			throw new IllegalArgumentException();
		return FxUtil.mainClass;
	}

	public static void addToAnchorPane(AnchorPane parentPane, Node child) {
		parentPane.getChildren().add(child);
		setAnchorZero(child);
	}

	public static void setAnchorZero(Node node) {
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setTopAnchor(node, 0.0);
	}

	public static void runNewSingleThreaded(Runnable rc) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					rc.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.setOnSucceeded(taskEvent -> {
		});
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(task);
		executor.shutdown();
	}
}
