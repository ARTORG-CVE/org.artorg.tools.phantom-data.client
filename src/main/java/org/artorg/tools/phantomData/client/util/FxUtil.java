package org.artorg.tools.phantomData.client.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.Main;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class FxUtil extends org.artorg.tools.phantomData.server.util.FxUtil {

	public static void openFrame(String title, Parent parent) {
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		scene.setRoot(parent);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UTILITY);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.sizeToScene();
		stage.setResizable(false);
		stage.setTitle(title);
		FxUtil.addMouseMovingSupport(stage, parent);
		Rectangle2D bounds = new Rectangle2D(Main.getStage().getX(), Main.getStage().getY(),
				Main.getStage().getWidth(), Main.getStage().getHeight());
		stage.show();
		stage.setX(bounds.getMinX() + bounds.getWidth() / 2.0 - stage.getWidth() / 2.0);
		stage.setY(bounds.getMinY() + bounds.getHeight() / 2.0 - stage.getHeight() / 2.0);
		
	}

	public static Image getFileIcon(File file) {
		final String ext = IOutil.getFileExt(file.getPath());
		Image fileIcon = null;
		javax.swing.Icon jswingIcon = null;

		if (file.exists()) {
			jswingIcon = SwingUtil.getJSwingIconFromFileSystem(file);
		} else {
			File tempFile = null;
			try {
				tempFile = File.createTempFile("icon", ext);
				jswingIcon = SwingUtil.getJSwingIconFromFileSystem(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (tempFile != null) tempFile.delete();
			}
		}

		if (jswingIcon != null) fileIcon = jswingIconToImage(jswingIcon);

		return fileIcon;
	}

	public static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
		BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(),
				jswingIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		return SwingFXUtils.toFXImage(bufferedImage, null);
	}

	public static <T> TableColumn<T, Void> createButtonCellColumn(String text,
			Consumer<T> consumer) {
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
							cell.setOnMouseClicked(event -> consumer
									.accept(getTableView().getItems().get(cell.getIndex())));
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

	public static <T> void setComboBoxCellFactory(ComboBox<T> comboBox,
			Function<T, String> mapper) {
		Callback<ListView<T>, ListCell<T>> cellFactory = FxUtil.createComboBoxCellFactory(mapper);
		comboBox.setButtonCell(cellFactory.call(null));
		comboBox.setCellFactory(cellFactory);
	}

	private static <T> Callback<ListView<T>, ListCell<T>>
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

	public static void addMenuItem(Menu menu, String name, EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(eventHandler);
		menu.getItems().add(menuItem);
	}

	public static void addMenuItem(ContextMenu rowMenu, String name,
			EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(eventHandler);
		rowMenu.getItems().add(menuItem);
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
//		task.setOnSucceeded(taskEvent -> {
//		});
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(task);
		executor.shutdown();
	}
}
