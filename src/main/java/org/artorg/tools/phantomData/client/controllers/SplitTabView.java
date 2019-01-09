package org.artorg.tools.phantomData.client.controllers;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

import java.awt.Desktop;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.beans.NamedTreeItem;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.exceptions.DeleteException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PermissionDeniedException;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.scene.control.DbEntityView;
import org.artorg.tools.phantomData.client.scene.control.EntityView;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.SmartSplitTabPane;
import org.artorg.tools.phantomData.client.scene.control.SmartTabPane;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.DbPersistent;
import org.artorg.tools.phantomData.server.model.Identifiable;
import org.artorg.tools.phantomData.server.model.IdentifiableUUID;
import org.artorg.tools.phantomData.server.model.NameGeneratable;
import org.artorg.tools.phantomData.server.models.base.DbFile;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class SplitTabView extends SmartSplitTabPane implements AddableToPane {
	private final SplitPane splitPane;
	private final SmartTabPane tableTabPane;
	private final SmartTabPane itemAddEditTabPane;
	private final SmartTabPane viewerTabPane;
	private boolean controlDown;
	private List<Runnable> menuItemUpdater;

	{
		splitPane = super.getSplitPane();

		Consumer<TabPane> addTabPanePolicy = tabPane -> addTabPanePolicy(tabPane);
		tableTabPane = createSmartTabPane();
		itemAddEditTabPane = createSmartTabPane();
		viewerTabPane = createSmartTabPane();

		tableTabPane.getTabPane().setOnDragOver(event -> {
			Logger.debug.println("Drag over");
		});

		tableTabPane.getTabPane().setOnDragDropped(event -> {
			Logger.debug.println("Drag dropped");
		});

		super.setNodeAddPolicy(addTabPanePolicy);

		super.getTabPanes().add(tableTabPane);
		super.getTabPanes().add(itemAddEditTabPane);
		super.getTabPanes().add(viewerTabPane);

		menuItemUpdater = new ArrayList<Runnable>();

		splitPane.setOrientation(Orientation.HORIZONTAL);

	}

	public SplitTabView() {

		Platform.runLater(() -> {
			Main.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
				controlDown = event.isControlDown();
				menuItemUpdater.stream().forEach(rc -> rc.run());
			});
			Main.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
				controlDown = event.isControlDown();
				menuItemUpdater.stream().forEach(rc -> rc.run());
			});
		});
	}

	private SmartTabPane createSmartTabPane() {
		SmartTabPane smartTabPane = new SmartTabPane();
		smartTabPane.setParentItemsSupplier(() -> this.getSplitPane().getItems());
		smartTabPane.setNodeAddPolicy(tabPane -> addTabPanePolicy((TabPane) tabPane));
		smartTabPane.setNodeRemovePolicy(tabPane -> removeTabPanePolicy((TabPane) tabPane));
		return smartTabPane;
	}

	public <T> void openViewerTab(Tab tab) {
		setTab(viewerTabPane.getTabPane(), tab, tab.getContent());
	}

	public <T> void openTableTab(Tab tab) {
		setTab(tableTabPane.getTabPane(), tab, tab.getContent());
	}

	@SuppressWarnings("unchecked")
	private void changeToTreeTableView(EntityView view) {

		Collection<Object> items = view.getSelectedItems();
		DbTreeTableView<Object> treeTableView =
				((UIEntity<Object>) Main.getUIEntity(view.getItemClass()))
						.createProTreeTableView(items);
		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), view.getNode());
		if (controlDown) tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	@SuppressWarnings("unchecked")
	private void openTreeTableViewBelow(Class<?> itemClass, String tabName,
			Collection<Object> items) {
		DbTreeTableView<Object> treeTableView =
				((UIEntity<Object>) Main.getUIEntity(itemClass)).createProTreeTableView();
		treeTableView.setItems(items);

		SplitTabView splitTabView = Main.getMainController().getSplitTabViews().get(1);

//		String tabName = view.getItemClass().getSimpleName();
		if (items.size() == 1)
			tabName = ((DbPersistent<?, ?>) items.iterator().next()).getItemClass().getSimpleName();
		Tab tab = new Tab(tabName);
		setTab(splitTabView.tableTabPane.getTabPane(), tab, treeTableView);
	}

	private void changeToTableView(EntityView view) {
//		ProTableView<T> treeTableView = (ProTableView<T>)TableViewFactory.createTableView(
//			tableView.getItemClass(), DbTable.class, DbTableView.class,
//			tableView.getSelectionModel().getSelectedItems());

		Map<Class<?>, List<Object>> itemsMap = view.getSelectedItems().stream()
				.collect(Collectors.groupingBy(item -> item.getClass()));
		Class<?> itemClass = itemsMap.entrySet().stream().reduce((e1, e2) -> {
			if (e1.getValue().size() > e2.getValue().size()) return e1;
			return e2;
		}).get().getKey();

		List<?> items = itemsMap.get(itemClass);

		ProTableView<?> treeTableView = Main.getUIEntity(itemClass).createDbTableView(items);

		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), view.getNode());
		if (controlDown) tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	private Tab findTabByContent(TabPane tabPane, Node node) {
		return tabPane.getTabs().stream().filter(tab -> tab.getContent() == node).findFirst()
				.orElseThrow(() -> new NullPointerException());
	}

	@SuppressWarnings("unchecked")
	public <T> void setTab(TabPane tabPane, Tab tab, Node node) {
		Object content = null;
		try {
			content = tab.getContent();
		} catch (NullPointerException e) {}
		if (content == null || content != node) tab.setContent(node);
		if (!tabPane.getTabs().contains(tab)) tabPane.getTabs().add(tab);
		if (tabPane.getSelectionModel().getSelectedItem() != tab)
			tabPane.getSelectionModel().select(tab);

		if (node instanceof ProTableView) setTableTab(tab, (ProTableView<T>) node);

		if (node instanceof DbTreeTableView) setTreeTableTab(tab, (DbTreeTableView<T>) node);
	}

	@SuppressWarnings("unchecked")
	private <T> void setTableTab(Tab tab, ProTableView<T> view) {
		view.setRowFactory(v -> (TableRow<T>) addRowContextMenu(view, new TableRow<T>()));

		view.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {
					T selectedItem = newValue;
					if (newValue == null) return;
					FxUtil.runNewSingleThreaded(() -> {
						try {
							Platform.runLater(() -> {
								show3dInViewer(selectedItem);
							});
						} catch (Exception e) {}
					});
					FxUtil.runNewSingleThreaded(() -> {
						Platform.runLater(() -> {
							TabPane tableTabPane = Main.getMainController().getSplitTabViews()
									.get(1).getTableTabPane().getTabPane();
							DbTreeTableView<T> treeTableView = null;
							Node content;

							int n = 0;
							do {
								content = tableTabPane.getSelectionModel().getSelectedItem()
										.getContent();
								if (content instanceof DbTreeTableView
										&& ((DbTreeTableView<?>) content).getItemClass() == view
												.getItemClass())
									treeTableView = (DbTreeTableView<T>) content;
								else {
									SplitTabView.this.openTreeTableViewBelow(view.getItemClass(),
											view.getTable().getTableName(),
											Collections.emptyList());
								}
								if (n > 1) {
									Logger.warn.println("Tree table couldn't generated");
									break;
								}
							} while (treeTableView == null);
							treeTableView.setItem(selectedItem);
						});
					});
				});

		view.setContextMenu(createOutterContextMenu(tab, view));

	}

	private <T> void setTreeTableTab(Tab tab, DbTreeTableView<T> view) {
		view.setRowFactory(tableView -> (TreeTableRow<NamedTreeItem>) addRowContextMenu(view,
				new TreeTableRow<NamedTreeItem>()));
		view.setContextMenu(createOutterContextMenu(tab, view));
	}

	@SuppressWarnings("unchecked")
	private ContextMenu createOutterContextMenu(Tab tab, EntityView view) {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem;

		menuItem = new MenuItem("Refresh");
		menuItem.setOnAction(event -> view.refresh());
		contextMenu.getItems().addAll(menuItem);

		menuItem = new MenuItem("Reload");
		menuItem.setOnAction(event -> {
			if (view instanceof DbEntityView) ((DbEntityView) view).reload();
		});
		contextMenu.getItems().addAll(menuItem);

		menuItem = new MenuItem("Add item");
		menuItem.setOnAction(event -> {
			ItemEditor<Object> controller =
					((UIEntity<Object>) Main.getUIEntity(view.getItemClass())).createEditFactory();
			controller.showCreateMode();
			addTab(itemAddEditTabPane.getTabPane(), controller,
					"Add " + view.getTable().getItemName());
		});
		contextMenu.getItems().addAll(menuItem);

		menuItem = new MenuItem("Open Viewer");
		menuItem.setOnAction(event -> {
			Scene3D scene3d = new Scene3D();
			addTab(viewerTabPane.getTabPane(), scene3d, "Viewer");
		});
		contextMenu.getItems().addAll(menuItem);

		return contextMenu;
	}

	private <T> IndexedCell<T> addRowContextMenu(EntityView view, IndexedCell<T> row) {
		ContextMenu contextMenu = createRowContextMenu(view);

		// only display context menu for non-null items:
		row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
				.then(contextMenu).otherwise((ContextMenu) null));

		if (view.getItemClass() == DbFile.class) {
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1) {
					FxUtil.runNewSingleThreaded(() -> {
						File srcFile = ((DbFile) view.getEntityItem()).createFile();
						try {
							Desktop.getDesktop().open(srcFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
			});
		}
		return row;
	}

	@SuppressWarnings("unchecked")
	private ContextMenu createRowContextMenu(EntityView view) {
		final ContextMenu contextMenu = new ContextMenu();
		if (view.getItemClass() == DbFile.class) {
			addMenuItem(contextMenu, "Open", event -> {
				List<DbFile> selectedDbFiles =
						view.getSelectedItems().stream().filter(item -> item instanceof DbFile)
								.map(item -> ((DbFile) item)).collect(Collectors.toList());

				FxUtil.runNewSingleThreaded(() -> {
					selectedDbFiles.stream().forEach(dbFile -> {
						File srcFile = dbFile.createFile();
						try {
							Desktop.getDesktop().open(srcFile);
						} catch (IOException e) {
							e.printStackTrace();
						}

					});
				});
			});

			addMenuItem(contextMenu, "Download", event -> {
				List<DbFile> selectedDbFiles =
						view.getSelectedItems().stream().filter(item -> item instanceof DbFile)
								.map(item -> ((DbFile) item)).collect(Collectors.toList());

				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Select target dir");
				File desktopDir = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
				if (!desktopDir.exists())
					desktopDir = Paths.get(System.getProperty("user.home")).toFile();
				if (desktopDir.exists()) chooser.setInitialDirectory(desktopDir);
				File targetDir = chooser.showDialog(Main.getStage());
				if (targetDir != null) {
					FxUtil.runNewSingleThreaded(() -> {
						selectedDbFiles.stream().forEach(dbFile -> {
							File srcFile = dbFile.createFile();
							File destFile = Paths
									.get(targetDir.getPath(),
											dbFile.getName() + "." + dbFile.getExtension())
									.toFile();
							try {
								FileUtils.copyFile(srcFile, destFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					});
				}
			});

		}

		addMenuItem(contextMenu, "Refresh", event -> view.refresh());

		addMenuItem(contextMenu, "Reload", event -> {
			if (view instanceof DbTableView) ((DbTableView<?>) view).reload();
		});

		addMenuItem(contextMenu, "Edit item", event -> {
			ItemEditor<Object> controller = (ItemEditor<Object>) Main
					.getUIEntity(view.getEntityItem().getClass()).createEditFactory();
			if (controller == null) return;
			controller.showEditMode(view.getEntityItem());
			addTab(itemAddEditTabPane.getTabPane(), controller,
					"Edit " + view.getTable().getItemName());
		});

		addMenuItem(contextMenu, "Add item", event -> {
			ItemEditor<Object> controller = (ItemEditor<Object>) Main
					.getUIEntity(view.getEntityItem().getClass()).createEditFactory();
			controller.setCreateTemplate(view.getEntityItem());
			controller.showCreateMode();
			addTab(itemAddEditTabPane.getTabPane(), controller,
					"Add " + view.getTable().getItemName());
		});

		addMenuItem(contextMenu, "Delete", event -> {
			List<Object> items = view.getEntityItems();
			final List<Object> failuredItems = new ArrayList<>(items.size());
			final List<Exception> exceptions = new ArrayList<>(items.size());
			Platform.runLater(() -> {
				Task<Void> task = new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						for (int i = 0; i < items.size(); i++) {
							if (this.isCancelled()) break;
							Object item = items.get(i);
							ICrudConnector<Object> connector =
									(ICrudConnector<Object>) Connectors.get(item.getClass());
							try {
								if (connector.deleteById(((Identifiable<?>) item).getId()))
									view.getTable().getItems().remove(item);
							} catch (NoUserLoggedInException e) {
								Logger.warn.println(e.getMessage());
								e.showAlert();
								break;
							} catch (DeleteException e) {
								Logger.error.println(e.getMessage());
								failuredItems.add(item);
								exceptions.add(e);
							} catch (PermissionDeniedException e) {
								Logger.warn.println(e.getMessage());
								failuredItems.add(item);
								exceptions.add(e);
							}
							updateMessage(String.format("Deleting items %d/%d\n%s", i, items.size(),
									getItemName(item)));
							updateProgress(i + 1, items.size());
						}
						updateProgress(items.size(), items.size());

						if (!exceptions.isEmpty()) throw new Exception();
						return null;
					}

				};
				task.setOnFailed(event2 -> {
					if (!failuredItems.isEmpty()) {
						if (failuredItems.size() == 1) {
							Exception e = exceptions.get(0);
							if (e instanceof DeleteException) ((DeleteException) e).showAlert();
							else if (e instanceof PermissionDeniedException)
								((PermissionDeniedException) e).showAlert();
						} else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Delete failure");
							if (failuredItems.size() < 10) {
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < failuredItems.size(); i++) {
									sb.append("  - ");
									sb.append(exceptions.get(i).getClass().getSimpleName());
									sb.append(":   ");
									sb.append(getItemName(failuredItems.get(i)));
									if (i != failuredItems.size() - 1) sb.append("\n");
								}
								alert.setContentText(String.format("%d %s(s) couldn't deleted\n%s",
										failuredItems.size(), view.getItemClass().getSimpleName(),
										sb.toString()));
							} else {
								alert.setContentText(String.format("%d %s(s) couldn't deleted",
										failuredItems.size(), view.getItemClass().getSimpleName()));
							}
							alert.showAndWait();
						}

					}
				});

				SimpleProgressDialog dialog = new SimpleProgressDialog(task, "Deleting progress");
				dialog.showAndWait();

			});
		});

		addMenuItem(contextMenu, "Open Viewer", event -> {
			Scene3D scene3d = new Scene3D();
			File file = get3dFile(view.getEntityItem());
			if (file != null) {
				scene3d.loadFile(file);
				addTab(viewerTabPane.getTabPane(), scene3d, "Viewer");
			} else
				Logger.warn.println(
						"File not supported for viewer: " + getItemName(view.getEntityItem()));

		});

		if (view instanceof TableView) {
			MenuItem treeMenuItem = new MenuItem("Show Tree View");
			treeMenuItem.setOnAction(event -> changeToTreeTableView(view));
			menuItemUpdater.add(() -> {
				if (controlDown) treeMenuItem.setText("Show Tree View +");
				else
					treeMenuItem.setText("Show Tree View");
			});
			contextMenu.getItems().add(treeMenuItem);
		} else if (view instanceof TreeTableView) {
			MenuItem treeMenuItem = new MenuItem("Show Table View");
			treeMenuItem.setOnAction(event -> changeToTableView(view));
			menuItemUpdater.add(() -> {
				if (controlDown) treeMenuItem.setText("Show Table View +");
				else
					treeMenuItem.setText("Show Table View");
			});
			contextMenu.getItems().add(treeMenuItem);
		}

		addMenuItem(contextMenu, "Show Tree below", event -> {
			openTreeTableViewBelow(view.getItemClass(), view.getTable().getTableName(),
					(Collection<Object>) view.getSelectedItems());
		});

		return contextMenu;
	}

	private String getItemName(Object item) {
		if (item instanceof NameGeneratable) return ((NameGeneratable) item).toName();
		else if (item instanceof Identifiable<?>)
			return ((Identifiable<?>) item).getId().toString();
		else
			return item.toString();
	}

	private <T> void show3dInViewer(T item) {
		if (viewerTabPane.getTabPane().getTabs().size() > 0) {
			Tab tab = viewerTabPane.getTabPane().getSelectionModel().getSelectedItem();

			if (show3dInViewer(item, tab) && item instanceof NameGeneratable)
				tab.setText(((NameGeneratable) item).toName());
		}
	}

	private <T> boolean show3dInViewer(T item, Tab tab) {
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
				return ((DbFile) item).createFile();
			else
				return null;
		}

		List<DbFile> files = getFiles(item);
		if (files != null && !files.isEmpty()) {
			UUID previewId = IdentifiableUUID.getUuid("57d35e8c805c4b2693a89dd44f1c7a16");
			Optional<DbFile> optionalFile =
					files.stream().filter(dbFile -> dbFile.getExtension().equalsIgnoreCase("stl"))
							.filter(dbFile -> dbFile.getFileTags().stream()
									.filter(fileTag -> fileTag.getId().equals(previewId))
									.findFirst().isPresent())
							.findFirst();
			if (optionalFile.isPresent()) return optionalFile.get().createFile();
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
		EntityBeanInfo<T> beanInfo =
				Main.getUIEntity((Class<T>) item.getClass()).getEntityBeanInfo();
		List<PropertyDescriptor> descriptors = beanInfo.getAllPropertyDescriptors().stream()
				.filter(d -> d.getName().equals("files")).collect(Collectors.toList());

		if (descriptors.size() == 0) return null;
		if (descriptors.size() != 1) throw new IllegalArgumentException();

		U value = null;
		try {
			value = (U) descriptors.get(0).getReadMethod().invoke(item);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return value;
	}

	public void addTabPanePolicy(TabPane tabPane) {
		if (tabPane == getTableTabPane().getTabPane()) splitPane.getItems().add(0, tabPane);
		else if (tabPane == getItemAddEditTabPane().getTabPane())
			splitPane.getItems().add(1, tabPane);
		else if (tabPane == getViewerTabPane().getTabPane())
			splitPane.getItems().add(splitPane.getItems().size(), tabPane);
		updateDividerPositions(tabPane);
	}

	public void removeTabPanePolicy(TabPane tabPane) {
		splitPane.getItems().remove(tabPane);
		updateDividerPositions(tabPane);
	}

	public void updateDividerPositions(TabPane tabPane) {
		final double tablePortion = 0.7;
		final double viewerPortion = 0.3;
		final double itemEditPortion = 0.3;

		int nItems = splitPane.getItems().size();
		double[] positions = splitPane.getDividerPositions();

		if (nItems == 2) {
			if (tabPane == getTableTabPane().getTabPane()) positions[0] = tablePortion;
			if (tabPane == getItemAddEditTabPane().getTabPane()) positions[0] = 1 - itemEditPortion;
			if (tabPane == getViewerTabPane().getTabPane()) positions[0] = 1 - viewerPortion;
		}
		if (nItems == 3) {
			positions[1] = 1 - viewerPortion;
			positions[0] = positions[1] - itemEditPortion;
		}
		splitPane.setDividerPositions(positions);

	}

//	private double getPortion(TabPane tabPane, int n) {
//		if (tabPane == getTableTabPane().getTabPane()) return 0.6;
//	}

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
