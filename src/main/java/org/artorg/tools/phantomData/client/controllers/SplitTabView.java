package org.artorg.tools.phantomData.client.controllers;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

import java.awt.Desktop;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.beans.DbNode;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.FxFactory;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.SmartSplitTabPane;
import org.artorg.tools.phantomData.client.scene.control.SmartTabPane;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.TableViewFactory;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.model.NameGeneratable;
import org.artorg.tools.phantomData.server.models.base.DbFile;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.DirectoryChooser;

public class SplitTabView extends SmartSplitTabPane implements AddableToPane {
	private final SplitPane splitPane;
	private final SmartTabPane tableTabPane;
	private final SmartTabPane itemAddEditTabPane;
	private final SmartTabPane viewerTabPane;
	private boolean controlDown;
	private List<Runnable> menuItemUpdater;
	private int index;
	private Function<Integer, SplitTabView> twinGetter;

	{
		splitPane = super.getSplitPane();

		Consumer<TabPane> addTabPanePolicy = tabPane -> addTabPanePolicy(tabPane);
		tableTabPane = createSmartTabPane();
		itemAddEditTabPane = createSmartTabPane();
		viewerTabPane = createSmartTabPane();

		super.setNodeAddPolicy(addTabPanePolicy);

		super.getTabPanes().add(tableTabPane);
		super.getTabPanes().add(itemAddEditTabPane);
		super.getTabPanes().add(viewerTabPane);

		menuItemUpdater = new ArrayList<Runnable>();

		splitPane.setOrientation(Orientation.HORIZONTAL);

	}

	public SplitTabView(int index, Function<Integer, SplitTabView> twinGetter) {
		this.index = index;
		this.twinGetter = twinGetter;

//		Platform.runLater(() -> {
//			Main.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//				controlDown = event.isControlDown();
//				menuItemUpdater.stream().forEach(rc -> rc.run());
//			});
//			Main.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
//				controlDown = event.isControlDown();
//				menuItemUpdater.stream().forEach(rc -> rc.run());
//			});
//		});
	}

	private SmartTabPane createSmartTabPane() {
		SmartTabPane smartTabPane = new SmartTabPane();
		smartTabPane.setParentItemsSupplier(() -> this.getSplitPane().getItems());
		smartTabPane.setNodeAddPolicy(tabPane -> addTabPanePolicy((TabPane) tabPane));
		return smartTabPane;
	}

	public <T> void openViewerTab(Tab tab) {
		setTab(viewerTabPane.getTabPane(), tab, tab.getContent());
	}

	public <T> void openTableTab(Tab tab) {
		setTab(tableTabPane.getTabPane(), tab, tab.getContent());
	}

	@SuppressWarnings("unchecked")
	private <T> void
		changeToTreeTableView(ProTableView<T> tableView) {
		ProTreeTableView<T> treeTableView =
			TableViewFactory.createTreeTableView(tableView.getItemClass(), DbTable.class,
				DbTreeTableView.class, tableView.getSelectionModel().getSelectedItems());
		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), tableView);
		if (controlDown) tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	@SuppressWarnings("unchecked")
	private <T> void
		openTreeTableViewBelow(ProTableView<T> tableView) {
		ProTreeTableView<T> treeTableView =
			TableViewFactory.createTreeTableView(tableView.getItemClass(), DbTable.class,
				DbTreeTableView.class, tableView.getSelectionModel().getSelectedItems());
		SplitTabView splitTabView = twinGetter.apply(index + 1);

		ObservableList<T> selectedItems =
			tableView.getSelectionModel().getSelectedItems();
		String tabName = tableView.getItemClass().getSimpleName();
		if (selectedItems.size() == 1)
			tabName = ((DbPersistent<?,?>)selectedItems.get(0)).getItemClass().getSimpleName();
		Tab tab = new Tab(tabName);
		setTab(splitTabView.tableTabPane.getTabPane(), tab, treeTableView);
	}

	@SuppressWarnings("unchecked")
	private <T> void
		changeToTableView(ProTreeTableView<T> tableView) {
		ProTableView<T> treeTableView = (ProTableView<T>)TableViewFactory.createTableView(
			tableView.getItemClass(), DbTable.class, DbFilterTableView.class,
			tableView.getSelectionModel().getSelectedItems());
		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), tableView);
		if (controlDown) tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	private Tab findTabByContent(TabPane tabPane, Node node) {
		return tabPane.getTabs().stream().filter(tab -> tab.getContent() == node)
			.findFirst().orElseThrow(() -> new NullPointerException());
	}

	@SuppressWarnings("unchecked")
	public <T> void setTab(TabPane tabPane, Tab tab,
		Node node) {
		Object content = null;
		try {
			content = tab.getContent();
		} catch (NullPointerException e) {}
		if (content == null || content != node) tab.setContent(node);
		if (!tabPane.getTabs().contains(tab)) tabPane.getTabs().add(tab);
		if (tabPane.getSelectionModel().getSelectedItem() != tab)
			tabPane.getSelectionModel().select(tab);

		if (node instanceof ProTableView) setTableTab(tab, (ProTableView<T>) node);

		if (node instanceof ProTreeTableView)
			setTreeTableTab(tab, (ProTreeTableView<T>) node);
	}

	private <T> void setTableTab(Tab tab,
		ProTableView<T> tableViewSpring) {
		tableViewSpring.setRowFactory(
			tableView -> createTableViewContext(tableViewSpring, tableView));

		tableViewSpring.getSelectionModel().selectedItemProperty()
			.addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {
				try {
					show3dInViewer(newValue);
				} catch (Exception e) {}
			});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem;

		menuItem = new MenuItem("Add item");
		menuItem.setOnAction(event -> {
			FxFactory<T> controller = createFxFactory(tableViewSpring.getItemClass());
			Node node = controller.create(tableViewSpring.getItemClass());
			addTab(itemAddEditTabPane.getTabPane(), node,
				"Add " + tableViewSpring.getTable().getItemName());
		});
		contextMenu.getItems().addAll(menuItem);
		menuItem = new MenuItem("Reload");
		menuItem.setOnAction(event -> {
			if (tableViewSpring instanceof DbTableView)
				((DbTableView<?>) tableViewSpring).reload();
		});
		contextMenu.getItems().addAll(menuItem);

		tableViewSpring.setContextMenu(contextMenu);

	}

//	private static Map<Class<?>, Class<?>> factoryClassMap = new HashMap<>();
	
//	public static void searchFactoryClasses(Collection<Class<?>> itemClasses) {
//		itemClasses.forEach(itemClass -> {
//			try {
//				findFactoryClass(itemClass);
//			} catch (Exception e) {}
//		});
//	}

	@SuppressWarnings("unchecked")
	public static <T> FxFactory<T> createFxFactory(Class<T> itemClass) {
		return (FxFactory<T>) Main.getUIEntity(((Class<DbPersistent<T,?>>)itemClass)).createEditFactory();
	}
		
		
//		FxFactory<T> fxFactory = null;
//
//		Class<?> factoryClass = null;
//
//		if (factoryClassMap.containsKey(itemClass)) {
//			factoryClass = factoryClassMap.get(itemClass);
//		}
//		else {
//			try {
//				factoryClass = findFactoryClass(itemClass);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		try {
//			fxFactory = (FxFactory<T>) factoryClass.newInstance();
//		} catch (InstantiationException | IllegalAccessException e) {
//			e.printStackTrace();
//		}
//
//		return fxFactory;
//	}

//	private static Class<?> findFactoryClass(Class<?> itemClass) throws Exception {
//		
//		
//		
//			Class<?> factoryClass = Reflect.getClassByGenericAndSuperClass(ItemEditFactoryController.class,
//				itemClass, 0, Main.getReflections());
//			factoryClassMap.put(itemClass, factoryClass);
//			return factoryClass;
//	}

	private <T> void setTreeTableTab(Tab tab,
		ProTreeTableView<T> proTreeTableView) {

		proTreeTableView.setRowFactory(
			tableView -> createTreeTableViewContext(proTreeTableView, tableView));

		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem;

		menuItem = new MenuItem("Add item");
		menuItem.setOnAction(event -> {
			FxFactory<?> controller = createFxFactory(proTreeTableView.getItemClass());
			Node node = controller.create(proTreeTableView.getItemClass());
			addTab(itemAddEditTabPane.getTabPane(), node,
				"Add " + proTreeTableView.getTable().getItemName());
		});
		contextMenu.getItems().addAll(menuItem);

		menuItem = new MenuItem("Reload");
		menuItem.setOnAction(event -> {
			if (proTreeTableView instanceof DbTreeTableView)
				((DbTreeTableView<?>) proTreeTableView).reload();
		});
		contextMenu.getItems().addAll(menuItem);

		proTreeTableView.setContextMenu(contextMenu);
	}

	private <T> TableRow<T> createTableViewContext(
		ProTableView<T> tableViewSpring, TableView<T> tableView) {
		final TableRow<T> row = new TableRow<T>();
		final ContextMenu rowMenu = new ContextMenu();

		if (tableViewSpring.getItemClass() == DbFile.class) {
			addMenuItem(rowMenu, "Open", event -> {
				ObservableList<T> selectedItems =
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
				ObservableList<T> selectedItems =
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
			FxFactory<T> controller = createFxFactory(tableViewSpring.getItemClass());
			Node node = controller.edit(row.getItem(), tableViewSpring.getItemClass());

			addTab(itemAddEditTabPane.getTabPane(), node,
				"Edit " + tableViewSpring.getTable().getItemName());
		});

		addMenuItem(rowMenu, "Add item", event -> {
			FxFactory<T> controller = createFxFactory(tableViewSpring.getItemClass());
			Node node = controller.create(row.getItem(), tableViewSpring.getItemClass());
			addTab(itemAddEditTabPane.getTabPane(), node,
				"Add " + tableViewSpring.getTable().getItemName());
		});

		addMenuItem(rowMenu, "Delete", event -> {
			if (!UserAdmin.isUserLoggedIn())
				Main.getMainController().openLoginLogoutFrame();
			else {
				ICrudConnector<T> connector = (ICrudConnector<T>) Connectors
					.getConnector(tableViewSpring.getItemClass());
				if (connector.deleteById(((Identifiable<?>)row.getItem()).getId()))
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

	private <T> void show3dInViewer(T item) {
		if (viewerTabPane.getTabPane().getTabs().size() > 0) {
			Tab tab = viewerTabPane.getTabPane().getSelectionModel().getSelectedItem();

			if (show3dInViewer(item, tab) && item instanceof NameGeneratable)
				tab.setText(((NameGeneratable) item).toName());
		}
	}

	private <T> boolean show3dInViewer(T item,
		Tab tab) {
		if (tab != null) {
			File file = get3dFile(item);
			if (file != null) {
				show3dInViewer(file, tab);
				return true;
			}
		}
		return false;
	}

	private <T> File get3dFile(T item) {
		if (item instanceof DbFile) {
			if (((DbFile) item).getExtension().equalsIgnoreCase("stl"))
				return ((DbFile) item).getFile();
			else return null;
		}

		List<DbFile> files = getFiles(item);
		if (files != null && !files.isEmpty()) {
			Optional<DbFile> optionalFile = files.stream()
				.filter(dbFile -> dbFile.getExtension().equalsIgnoreCase("stl"))
				.findFirst();
			if (optionalFile.isPresent()) return optionalFile.get().getFile();
		}
		return null;
	}

	private <T> void show3dInViewer(File file, Tab tab) {
		Platform.runLater(() -> {
			Scene3D newScene3d = new Scene3D();
			newScene3d.loadFile(file);
			tab.setContent(newScene3d);
		});

	}

	private <T> List<DbFile> getFiles(T item) {
		if (item == null) return new ArrayList<DbFile>();

		List<DbFile> files = null;
		try {
			files = getValue(item, "files");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (files == null) return new ArrayList<DbFile>();

		return files;
	}

	@SuppressWarnings("unchecked")
	private <T, U> U getValue(T item, String name) {
		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(item.getClass());
		List<PropertyDescriptor> descriptors =
			beanInfo.getAllPropertyDescriptors().stream()
				.filter(d -> d.getName().equals("files")).collect(Collectors.toList());

		if (descriptors.size() == 0) return null;
		if (descriptors.size() != 1) throw new IllegalArgumentException();

		U value = null;
		try {
			value = (U) descriptors.get(0).getReadMethod().invoke(item);
		} catch (IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e) {
			e.printStackTrace();
		}

		return value;
	}

	private <T> TreeTableRow<DbNode>
		createTreeTableViewContext(ProTreeTableView<T> proTreeTableView,
			TreeTableView<DbNode> tableView) {
		final TreeTableRow<DbNode> row = new TreeTableRow<DbNode>();
		final ContextMenu rowMenu = new ContextMenu();

		addMenuItem(rowMenu, "Show Table View", event -> {
			changeToTableView(proTreeTableView);
		});

		addMenuItem(rowMenu, "Reload", event -> {
			if (proTreeTableView instanceof DbTreeTableView)
				((DbTreeTableView<?>) proTreeTableView).reload();
		});

		// only display context menu for non-null items:
		row.contextMenuProperty()
			.bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
				.otherwise((ContextMenu) null));

		return row;
	}

	public void addTabPanePolicy(TabPane tabPane) {
		if (tabPane == getTableTabPane().getTabPane())
			splitPane.getItems().add(0, tabPane);
		else if (tabPane == getItemAddEditTabPane().getTabPane())
			splitPane.getItems().add(1, tabPane);
		else if (tabPane == getViewerTabPane().getTabPane())
			splitPane.getItems().add(splitPane.getItems().size(), tabPane);
		dividerPositionsPolicy(tabPane);
	}

	public void dividerPositionsPolicy(TabPane tabPane) {
		final double tablePortion = 0.7;
		final double viewerPortion = 0.3;
		final double itemEditPortion = 0.3;

		int nItems = splitPane.getItems().size();
		double[] positions = splitPane.getDividerPositions();

		if (nItems == 2) {
			if (tabPane == getTableTabPane().getTabPane()) positions[0] = tablePortion;
			if (tabPane == getItemAddEditTabPane().getTabPane())
				positions[0] = 1 - itemEditPortion;
			if (tabPane == getViewerTabPane().getTabPane())
				positions[0] = 1 - viewerPortion;
		}
		if (nItems == 3) {
			positions[1] = 1 - viewerPortion;
			positions[0] = positions[1] - itemEditPortion;
		}
		splitPane.setDividerPositions(positions);

	}

	public SmartTabPane getTableTabPane() {
		return tableTabPane;
	}

	public SmartTabPane getItemAddEditTabPane() {
		return itemAddEditTabPane;
	}

	public SmartTabPane getViewerTabPane() {
		return viewerTabPane;
	}

}
