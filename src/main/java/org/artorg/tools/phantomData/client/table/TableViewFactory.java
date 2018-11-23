package org.artorg.tools.phantomData.client.table;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.beans.DbNode;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.model.base.DbFile;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;

public class TableViewFactory {

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createInitializedTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {
		ProTreeTableView<T> tableView =
			createTreeTableView(itemClass, tableClass, tableViewClass);

		if (tableView instanceof DbTreeTableView)
			((DbTreeTableView<T>) tableView).reload();

		tableView.initTable();

		return tableView;
	}

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass, List<T> items) {
		ProTreeTableView<T> treeTableView =
			createTreeTableView(itemClass, tableClass, tableViewClass);
		treeTableView.setItems(items);
		return treeTableView;
	}

	public static <T extends DbPersistent<T, ?>, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTreeTableView<T>> ProTreeTableView<T>
		createTreeTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {

		TableBase<T> table = createTableBase(itemClass, tableClass);

		ProTreeTableView<T> tableView = null;
		try {
			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
		} catch (InstantiationException | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			| SecurityException e) {
			e.printStackTrace();
		}
		tableView.setTable(table);

		return tableView;
	}

	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createInitializedTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {
		ProTableView<T> tableView = createTableView(itemClass, tableClass, tableViewClass);

		if (tableView instanceof DbTableView) ((DbTableView<?>) tableView).reload();

		tableView.initTable();
		
		
		tableView.setRowFactory(
			view -> createTableViewContext(tableView, view));

		tableView.getSelectionModel().selectedItemProperty()
			.addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {
				try {
					show3dInViewer(newValue);
				} catch (Exception e) {}
			});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem;

		menuItem = new MenuItem("Add item");
		menuItem.setOnAction(event -> {
			FxFactory<T> controller = createFxFactory(tableView.getItemClass());
			Node node = controller.create(tableView.getItemClass());
			addTab(itemAddEditTabPane.getTabPane(), node,
				"Add " + tableView.getTable().getItemName());
		});
		contextMenu.getItems().addAll(menuItem);
		menuItem = new MenuItem("Reload");
		menuItem.setOnAction(event -> {
			if (tableView instanceof DbTableView)
				((DbTableView<?>) tableView).reload();
		});
		contextMenu.getItems().addAll(menuItem);

		tableView.setContextMenu(contextMenu);
		
		

		return tableView;
	}

	@SuppressWarnings("unchecked")
	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass, List<TreeItem<DbNode>> treeItems) {
		ProTableView<T> tableView = createTableView(itemClass, tableClass, tableViewClass);
		ObservableList<T> items = FXCollections.observableArrayList();
		for (int i = 0; i < treeItems.size(); i++)
			try {
				T item = (T) treeItems.get(i).getValue().getValue();
				items.add(item);
			} catch (Exception e) {}

		tableView.setItems(items);
		return tableView;
	}
	
	public static <T, TABLE extends TableBase<T>,
		TABLE_VIEW extends ProTableView<T>> ProTableView<T>
		createTableView(Class<?> itemClass, Class<TABLE> tableClass,
			Class<TABLE_VIEW> tableViewClass) {

		TableBase<T> table = createTableBase(itemClass, tableClass);
		ProTableView<T> tableView = null;
		try {
			tableView = tableViewClass.getConstructor(Class.class).newInstance(itemClass);
		} catch (InstantiationException | IllegalAccessException
			| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			| SecurityException e) {
			e.printStackTrace();
		}
		tableView.setTable(table);

		return tableView;
	}

	public static <T> TableBase<T>
		createTableBase(Class<?> itemClass, Class<? extends TableBase<T>> tableClass) {
		return Reflect.createInstanceByGenericAndSuperClass(tableClass, itemClass,
			Main.getReflections());
	}
	
	private <ITEM extends DbPersistent<ITEM, ?>> TableRow<ITEM> createTableViewContext(
		ProTableView<ITEM> tableViewSpring, TableView<ITEM> tableView) {
		final TableRow<ITEM> row = new TableRow<ITEM>();
		final ContextMenu rowMenu = new ContextMenu();

		if (tableViewSpring.getItemClass() == DbFile.class) {
			addMenuItem(rowMenu, "Open", event -> {
				ObservableList<ITEM> selectedItems =
					tableViewSpring.getSelectionModel().getSelectedItems();
				List<DbFile> selectedDbFiles =
					selectedItems.stream().filter(item -> item instanceof DbFile)
						.map(item -> ((DbFile) item)).collect(Collectors.toList());

				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						selectedDbFiles.stream().forEach(dbFile -> {
							File srcFile = dbFile.getFile();

							try {
								Desktop.getDesktop().open(srcFile);
							} catch (IOException e) {
								e.printStackTrace();
							}

						});

						return null;
					}
				};
				task.setOnSucceeded(taskEvent -> {});
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(task);
				executor.shutdown();
			});

			addMenuItem(rowMenu, "Download", event -> {
				ObservableList<ITEM> selectedItems =
					tableViewSpring.getSelectionModel().getSelectedItems();
				List<DbFile> selectedDbFiles =
					selectedItems.stream().filter(item -> item instanceof DbFile)
						.map(item -> ((DbFile) item)).collect(Collectors.toList());

				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Select target dir");
				File desktopDir =
					new File(System.getProperty("user.home") + "\\Desktop\\");
				chooser.setInitialDirectory(desktopDir);
				File targetDir = chooser.showDialog(Main.getStage());

				FxUtil.runNewSingleThreaded(() -> {
					selectedDbFiles.stream().forEach(dbFile -> {
						File srcFile = dbFile.getFile();
						File destFile = new File(targetDir + "\\" + dbFile.getName() + "."
							+ dbFile.getExtension());
						try {
							FileUtils.copyFile(srcFile, destFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				});
			});

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1) {
					Task<Void> task = new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							File srcFile = ((DbFile) row.getItem()).getFile();
							try {
								Desktop.getDesktop().open(srcFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							return null;
						}
					};
					task.setOnSucceeded(taskEvent -> {});

					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(task);
					executor.shutdown();
				}
			});

		}

		addMenuItem(rowMenu, "Refresh", event -> {
			tableViewSpring.refresh();
		});

		addMenuItem(rowMenu, "Reload", event -> {
			if (tableViewSpring instanceof DbTableView)
				((DbTableView<?>) tableViewSpring).reload();
		});

		addMenuItem(rowMenu, "Edit item", event -> {
			FxFactory<ITEM> controller = createFxFactory(tableViewSpring.getItemClass());
			Node node = controller.edit(row.getItem(), tableViewSpring.getItemClass());

			addTab(itemAddEditTabPane.getTabPane(), node,
				"Edit " + tableViewSpring.getTable().getItemName());
		});

		addMenuItem(rowMenu, "Add item", event -> {
			FxFactory<ITEM> controller = createFxFactory(tableViewSpring.getItemClass());
			Node node = controller.create(row.getItem(), tableViewSpring.getItemClass());
			addTab(itemAddEditTabPane.getTabPane(), node,
				"Add " + tableViewSpring.getTable().getItemName());
		});

		addMenuItem(rowMenu, "Delete", event -> {
			if (!UserAdmin.isUserLoggedIn())
				Main.getMainController().openLoginLogoutFrame();
			else {
				ICrudConnector<ITEM> connector = (ICrudConnector<ITEM>) Connectors
					.getConnector(tableViewSpring.getItemClass());
				if (connector.deleteById(row.getItem().getId()))
					tableViewSpring.getItems().remove(row.getItem());
			}
		});

		addMenuItem(rowMenu, "Open 3d Viewer", event -> {
			Scene3D scene3d = new Scene3D();
			scene3d.loadFile(get3dFile(row.getItem()));
			addTab(viewerTabPane.getTabPane(), scene3d, "3D Viewer");
		});

		MenuItem treeMenuItem = new MenuItem("Show Tree View");
		treeMenuItem.setOnAction(event -> changeToTreeTableView(tableViewSpring));
		menuItemUpdater.add(() -> {
			if (controlDown) treeMenuItem.setText("Show Tree View +");
			else treeMenuItem.setText("Show Tree View");
		});
		rowMenu.getItems().add(treeMenuItem);

		addMenuItem(rowMenu, "Show Tree below", event -> {
			openTreeTableViewBelow(tableViewSpring);
		});

		// only display context menu for non-null items:
		row.contextMenuProperty()
			.bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
				.otherwise((ContextMenu) null));
		return row;
	}
	
	

}
