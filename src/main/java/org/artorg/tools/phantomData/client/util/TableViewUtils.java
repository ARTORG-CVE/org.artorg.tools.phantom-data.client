package org.artorg.tools.phantomData.client.util;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TableViewUtils {

	public static <T> TableColumn<T, Void> createButtonCellColumn(String text, Consumer<T> consumer) {
		TableColumn<T, Void> column = new TableColumn<T, Void>();
		column.setCellFactory(new Callback<TableColumn<T, Void>, TableCell<T, Void>>() {
			@Override
			public TableCell<T, Void> call(final TableColumn<T, Void> param) {
				return new TableCell<T, Void>() {
					TableCell<T, Void> cell = this;

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							cell.setGraphic(null);
							cell.setText("");
							cell.setOnMouseClicked(null);
						} else {
							cell.setOnMouseClicked(
									event -> consumer.accept(getTableView().getItems().get(cell.getIndex())));
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

	public static <T> void autoResizeColumns(TableView<T> tableView) {
		System.out.println("autoresizecolumns");

		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

//		tableView.setColumnResizePolicy(new Callback<ResizeFeatures, Boolean>() {
//	        @Override public String toString() {
//	            return "unconstrained-resize";
//	        }
//
//	        @Override public Boolean call(ResizeFeatures prop) {
//	        	return false;
//	        }
//	    });

		tableView.getColumns().stream().forEach((column) -> {
//			Node node = column.getGraphic();
//			
//			if (node != null)
//			System.out.println("node: " +node);
//			else
//				System.out.println("null");

			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth() + 45.0;
			for (int i = 0; i < tableView.getItems().size(); i++) {
				if (column.getCellData(i) != null) {

					Object cellData = column.getCellData(i);
					if (cellData instanceof Node) {
					} else if (cellData instanceof String) {
						t = new Text((String)column.getCellData(i));
						double calcwidth = t.getLayoutBounds().getWidth() + 10;
						if (calcwidth > max)
							max = calcwidth;
					}
				}
			}
			column.setPrefWidth(max);
		});
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public static void removeColumnHeaders(TableView<?> tableView) {
		tableView.getStyleClass().add("noheader");
	}

}
