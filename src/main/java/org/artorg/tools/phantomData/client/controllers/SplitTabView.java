package org.artorg.tools.phantomData.client.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.io.IOutil;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.SmartTabPane;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.scene.layout.AddableToAnchorPane;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FxFactory;
import org.artorg.tools.phantomData.client.table.IDbFactoryTableView;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyEvent;

public class SplitTabView extends SplitPane implements AddableToAnchorPane {
	private SplitPane splitPane;
	private SmartTabPane tableTabPane;
	private TabPane itemAddEditTabPane;
	private TabPane scene3dTabPane;
	private boolean controlDown;
	private List<Runnable> menuItemUpdater;
	private Node rootNode;

	{
		splitPane = this;
		tableTabPane = new SmartTabPane(() -> splitPane.getItems());
		itemAddEditTabPane = new TabPane();
		scene3dTabPane = new TabPane();
		
		menuItemUpdater = new ArrayList<Runnable>();

		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().addAll(tableTabPane.getTabPane());

		Scene3D scene3d = new Scene3D();
		scene3d.loadFile(IOutil.readResourceAsFile("model.stl"));
		addTab(scene3dTabPane, scene3d, "3D Viewer");

		Platform.runLater(() -> {
			MainFx.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
				controlDown = event.isControlDown();
				menuItemUpdater.stream().forEach(rc -> rc.run());
			});
			MainFx.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
				controlDown = event.isControlDown();
				menuItemUpdater.stream().forEach(rc -> rc.run());
			});
		});

		Platform.runLater(() -> {
//			TabPane tabPane = tableTabPane;

			tableTabPane.init();
			tableTabPane.removeHeader();
			
//			Tab tab = tableTabPane.getSelectionModel().getSelectedItem();
//			rootNode = tab.getContent();
////			ObservableList<Node> nodes = splitPane.getItems();
////			for (int i = 0; i < nodes.size(); i++)
////				if (nodes.get(i) == tabPane) {
////					int index = nodes.indexOf(tabPane);
////					nodes.remove(index);
////					nodes.add(index, node);
////				}
//
//			Consumer<TabPane> removeHeader = tabPane -> {
//				if (tabPane.getTabs().size() < 2) {
////				Tab tab = tabPane.getSelectionModel().getSelectedItem();
////				Node node = tab.getContent();
//					ObservableList<Node> nodes = splitPane.getItems();
//					for (int i = 0; i < nodes.size(); i++)
//						if (nodes.get(i) == tabPane) {
//							int index = nodes.indexOf(tabPane);
//							nodes.remove(index);
//							nodes.add(index, node);
//						}
//				}
//			};
//
//			BiConsumer<TabPane, ListChangeListener<Tab>> showHeader = (tabPane, listener) -> {
//				ObservableList<Node> nodes2 = splitPane.getItems();
//				for (int i = 0; i < nodes2.size(); i++)
//					if (nodes2.get(i) == node) {
//						int index = nodes2.indexOf(node);
//						nodes2.remove(index);
//						TabPane tabPane2 = new TabPane();
//						tabPane2.getTabs().addListener(createListChangeListener(tableTabPane, removeHeader, showHeader));
//						setTableTabPane(tabPane2);
//						
//						tabPane2.getTabs()
//							.addAll(tabPane.getTabs());
//						nodes2.add(index, tabPane2);
//						tab.setContent(node);
//					}
//			};
//
//			ListChangeListener<Tab> changeListener = createListChangeListener(tableTabPane, removeHeader, showHeader);
////			new ListChangeListener<Tab>() {
////				@Override
////				public void onChanged(Change<? extends Tab> c) {
////					if (c.next()) {
////						if (c.wasAdded()) {
////							showHeader.accept(this);
//////							ObservableList<Node> nodes2 = splitPane.getItems();
//////							for (int i = 0; i < nodes2.size(); i++)
//////								if (nodes2.get(i) == node) {
//////									int index = nodes2.indexOf(node);
//////									nodes2.remove(index);
//////									TabPane tabPane2 = new TabPane();
//////									setTableTabPane(tabPane2);
//////									tableTabPane.getTabs().addListener(this);
//////									tabPane2.getTabs()
//////										.addAll(tabPane.getTabs());
//////									nodes2.add(index, tabPane2);
//////									tab.setContent(node);
//////								}
////						} else if (c.wasRemoved()) {
////							removeHeader.run();
//////							if (tableTabPane.getTabs().size()<2) {
////////								Tab tab = tabPane.getSelectionModel().getSelectedItem();
////////								Node node = tab.getContent();
//////								ObservableList<Node> nodes = splitPane.getItems();
//////								for (int i = 0; i < nodes.size(); i++)
//////									if (nodes.get(i) == tabPane) {
//////										int index = nodes.indexOf(tabPane);
//////										nodes.remove(index);
//////										nodes.add(index, node);
//////									}
//////							}
////						}
////					}
////				}
////			};
			
//			tableTabPane.getTabs().addListener(createListChangeListener(tableTabPane, tab));
//			removeHeader(tableTabPane, tab);
			
//			tableTabPane.getTabs().addListener(changeListener);
//
//			removeHeader.accept(tableTabPane);

		});

//		.addListener(event -> {
//			
//		});

//		Platform.runLater(() -> {
//		List<Node> nodes1 = tableTabPane.getChildrenUnmodifiable();
//		
//		System.out.println(nodes1.size());
//		
//		tableTabPane.setStyle("-fx-tab-max-height: 0px ;");
//		nodes1.get(1).setStyle("visibility: hidden ;");
//		});

	}
	
	
	
//	private void removeHeader(TabPane tabPane, Tab tab) {
//		if (tabPane.getTabs().size() == 1) {
//				ObservableList<Node> nodes = splitPane.getItems();
//				for (int i = 0; i < nodes.size(); i++)
//					if (nodes.get(i) == tabPane) {
//						int index = nodes.indexOf(tabPane);
//						nodes.remove(index);
//						rootNode = tabPane.getTabs().get(0).getContent();
//						nodes.add(index, rootNode);
//					}
//			}
//	}
//	
//	private void showHeader(TabPane tabPane, Tab tab) {
//		ObservableList<Node> nodes2 = splitPane.getItems();
//		int selectedTabIndex = getTableTabPane().getSelectionModel().getSelectedIndex();
//		for (int i = 0; i < nodes2.size(); i++)
//			if (nodes2.get(i) == rootNode) {
//				int index = nodes2.indexOf(rootNode);
//				nodes2.remove(index);
//				
//				TabPane tabPane2 = new TabPane();
//				setTableTabPane(tabPane2);
//				nodes2.add(index, tabPane2);
//				List<Tab> tabs = tabPane.getTabs();
//				for (int j=0; j<tabs.size(); j++) {
//					tabPane2.getTabs().add(tabs.get(j));
//					tabPane2.getSelectionModel().select(j);
//				}
//				tabPane2.getSelectionModel().select(selectedTabIndex);
//				tabPane2.getTabs().addListener(createListChangeListener(tabPane2, tab));
//			}
//	}
//	
//	private ListChangeListener<Tab> createListChangeListener(TabPane tabPane, Tab tab) {
//		ListChangeListener<Tab> changeListener = new ListChangeListener<Tab>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void onChanged(Change<? extends Tab> c) {
//				if (c.next()) {
//					if (c.wasAdded()) {
//						showHeader(tabPane, tab);
//						List<Tab> tabs = (List<Tab>) c.getAddedSubList();
//						getTableTabPane().getSelectionModel().select(tabs.get(tabs.size()-1));
//					} else if (c.wasRemoved()) {
//						removeHeader(tabPane, tab);
//					}
//				}
//				getTableTabPane().getSelectionModel().select(getTableTabPane().getTabs().size()-1);
//				
//			}
//		};
//		return changeListener;
//	}

	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void changeToTreeTableView(
		ProTableView<ITEM> tableView) {
		ProTreeTableView<ITEM> treeTableView = TableViewFactory
			.createTreeTable(tableView.getItemClass(), DbTable.class,
				DbTreeTableView.class,
				tableView.getSelectionModel().getSelectedItems());
		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), tableView);
		if (controlDown)
			tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void changeToTableView(
		ProTreeTableView<ITEM> tableView) {
		ProTableView<ITEM> treeTableView = TableViewFactory
			.createTable(tableView.getItemClass(),
				DbUndoRedoFactoryEditFilterTable.class,
				DbUndoRedoAddEditControlFilterTableView.class,
				tableView.getSelectionModel().getSelectedItems());
		Tab tab;
		tab = findTabByContent(tableTabPane.getTabPane(), tableView);
		if (controlDown)
			tab = new Tab(tab.getText());
		setTab(tableTabPane.getTabPane(), tab, treeTableView);
	}

	private Tab findTabByContent(TabPane tabPane, Node node) {
		return tabPane.getTabs().stream()
			.filter(tab -> tab.getContent() == node)
			.findFirst()
			.orElseThrow(() -> new NullPointerException());
	}

	public <ITEM extends DbPersistent<ITEM, ?>, TABLE extends ProTableView<ITEM>> void openTab(
		TABLE tableViewSpring, String name) {
		Tab tab = new Tab(name);
		setTab(tableTabPane.getTabPane(), tab, tableViewSpring);
	}

	public <ITEM extends DbPersistent<ITEM, ?>, TABLE extends ProTreeTableView<ITEM>> void openTab(
		TABLE proTreeTableView, String name) {
		Tab tab = new Tab(name);
		setTab(tableTabPane.getTabPane(), tab, proTreeTableView);
	}

	@SuppressWarnings("unchecked")
	public <ITEM extends DbPersistent<ITEM, ?>> void setTab(TabPane tabPane,
		Tab tab, Node node) {
		Object content = null;
		try {
			content = tab.getContent();
		} catch (NullPointerException e) {
		}
		if (content == null || content != node)
			tab.setContent(node);
		if (!tabPane.getTabs().contains(tab))
			tabPane.getTabs().add(tab);
		if (tabPane.getSelectionModel().getSelectedItem() != tab)
			tabPane.getSelectionModel().select(tab);

		if (node instanceof ProTableView)
			setTableTab(tab, (ProTableView<ITEM>) node);

		if (node instanceof ProTreeTableView)
			setTreeTableTab(tab, (ProTreeTableView<ITEM>) node);
	}

	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void setTableTab(Tab tab,
		ProTableView<ITEM> tableViewSpring) {
		tableViewSpring.setRowFactory(
			tableView -> createTableViewContext(tableViewSpring, tableView));

		ContextMenu contextMenu = new ContextMenu();
		MenuItem editItem = new MenuItem("Add item");
		editItem.setOnAction(event -> {
			FxFactory<ITEM> controller = ((IDbFactoryTableView<ITEM>) tableViewSpring)
				.createFxFactory();
			Node node = controller.create();
			addTab(itemAddEditTabPane, node,
				"Add " + tableViewSpring.getTable().getItemName());
		});
		contextMenu.getItems().addAll(editItem);

		tableViewSpring.setContextMenu(contextMenu);

	}

	private <ITEM extends DbPersistent<ITEM, ?>> void setTreeTableTab(Tab tab,
		ProTreeTableView<ITEM> proTreeTableView) {

		proTreeTableView.setRowFactory(tableView -> createTreeTableViewContext(
			proTreeTableView, tableView));

		ContextMenu contextMenu = new ContextMenu();
		proTreeTableView.setContextMenu(contextMenu);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <ITEM extends DbPersistent<ITEM, ?>> TableRow<ITEM> createTableViewContext(
		ProTableView<ITEM> tableViewSpring, TableView<ITEM> tableView) {
		final TableRow<ITEM> row = new TableRow<ITEM>();
		final ContextMenu rowMenu = new ContextMenu();

		addMenuItem(rowMenu, "Refresh", event -> {
			tableViewSpring.refresh();
		});

		if (tableViewSpring instanceof IDbFactoryTableView) {
			addMenuItem(rowMenu, "Edit item", event -> {
				FxFactory<ITEM> controller = ((IDbFactoryTableView<ITEM>) tableViewSpring)
					.createFxFactory();
				Node node = controller.edit(row.getItem());
				addTab(itemAddEditTabPane, node, "Edit "
					+ tableViewSpring.getTable().getItemName());
			});

			addMenuItem(rowMenu, "Add item", event -> {
				FxFactory<ITEM> controller = ((IDbFactoryTableView<ITEM>) tableViewSpring)
					.createFxFactory();
				Node node = controller.create(row.getItem());
				addTab(itemAddEditTabPane, node, "Add "
					+ tableViewSpring.getTable().getItemName());
			});
		}

		addMenuItem(rowMenu, "Delete", event -> {
			tableViewSpring.getItems().remove(row.getItem());
		});

		addMenuItem(rowMenu, "Open 3d Viewer", event -> {
			Scene3D scene3d = new Scene3D();
			scene3d
				.loadFile(IOutil.readResourceAsFile("model.stl"));
			addTab(scene3dTabPane, scene3d,
				"3D Viewer - "
					+ ((AbstractBaseEntity) row.getItem())
						.createName());
		});

		MenuItem treeMenuItem = new MenuItem("Show Tree View");
		treeMenuItem
			.setOnAction(event -> changeToTreeTableView(tableViewSpring));
		menuItemUpdater.add(() -> {
			if (controlDown)
				treeMenuItem.setText("Show Tree View +");
			else
				treeMenuItem.setText("Show Tree View");
		});
		rowMenu.getItems().add(treeMenuItem);

		// only display context menu for non-null items:
		row.contextMenuProperty()
			.bind(Bindings
				.when(Bindings.isNotNull(row.itemProperty()))
				.then(rowMenu).otherwise((ContextMenu) null));
		return row;
	}

	private <ITEM extends DbPersistent<ITEM, ?>> TreeTableRow<Object> createTreeTableViewContext(
		ProTreeTableView<ITEM> proTreeTableView,
		TreeTableView<Object> tableView) {
		final TreeTableRow<Object> row = new TreeTableRow<Object>();
		final ContextMenu rowMenu = new ContextMenu();

		addMenuItem(rowMenu, "Show Table View", event -> {
			changeToTableView(proTreeTableView);
		});

		// only display context menu for non-null items:
		row.contextMenuProperty()
			.bind(Bindings
				.when(Bindings.isNotNull(row.itemProperty()))
				.then(rowMenu).otherwise((ContextMenu) null));

		return row;
	}

	private <ITEM extends DbPersistent<ITEM, ?>> void addMenuItem(
		ContextMenu rowMenu, String name,
		EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setOnAction(eventHandler);
		rowMenu.getItems().add(menuItem);
	}

	private void addTab(TabPane tabePane, Node node, String tabName) {
		Tab tab = new Tab(tabName);
		tab.setContent(node);
		tab.setOnClosed(closeEvent -> {
			if (tabePane.getTabs().size() == 0)
				splitPane.getItems().remove(tabePane);
		});

		if (tabePane.getTabs().size() == 0) {
			if (tabePane == itemAddEditTabPane)
				splitPane.getItems().add(1, tabePane);
			if (tabePane == scene3dTabPane)
				splitPane.getItems().add(splitPane.getItems().size(), tabePane);
		}
		tabePane.getTabs().add(tab);
		tabePane.getSelectionModel().select(tab);
	}

//	public TabPane getTableTabPane() {
//		return tableTabPane;
//	}
//
//	public void setTableTabPane(TabPane tabPane) {
//		this.tableTabPane = tabPane;
//	}

}
