package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.io.IOutil;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
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
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.AnchorPane;

public class SplitTabView extends SplitPane implements AddableToAnchorPane {
	private SplitPane splitPane;
	private TabPane tableTabPane;
	private TabPane itemAddEditTabPane;
	private TabPane scene3dTabPane;

	{
		tableTabPane = new TabPane();
		itemAddEditTabPane = new TabPane();
		scene3dTabPane = new TabPane();
		splitPane = this;

		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().addAll(tableTabPane);

		Scene3D scene3d = new Scene3D();
		scene3d.loadFile(IOutil.readResourceAsFile("model.stl"));
		addTab(scene3dTabPane, scene3d, "3D Viewer");
	}
	
	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void changeToTreeTableView(ProTableView<ITEM> tableView) {
		ProTreeTableView<ITEM> treeTableView = TableViewFactory
			.createInitializedTreeTable(tableView.getItemClass(), DbTable.class, DbTreeTableView.class);
		Tab tab = findTabByContent(tableTabPane, tableView);
		setTab(tableTabPane, tab, treeTableView);
	}
	
	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void changeToTableView(ProTreeTableView<ITEM> tableView) {
		ProTableView<ITEM> treeTableView = TableViewFactory
			.createInitializedTable(tableView.getItemClass(), DbUndoRedoFactoryEditFilterTable.class, DbUndoRedoAddEditControlFilterTableView.class);
		Tab tab = findTabByContent(tableTabPane, tableView);
		setTab(tableTabPane, tab,treeTableView);
	}
	
	private Tab findTabByContent(TabPane tabPane, Node node) {
		return tabPane.getTabs().stream()
			.filter(tab -> tab.getContent() == node)
			.findFirst()
			.orElseThrow(() ->
			new NullPointerException());
	}

	public <ITEM extends DbPersistent<ITEM, ?>, TABLE extends ProTableView<ITEM>> void openTab(
		TABLE tableViewSpring, String name) {
		Tab tab = new Tab(name);
		setTab(tableTabPane, tab, tableViewSpring);
	}
	
	public <ITEM extends DbPersistent<ITEM, ?>, TABLE extends ProTreeTableView<ITEM>> void openTab(
		TABLE proTreeTableView, String name) {
		Tab tab = new Tab(name);
		setTab(tableTabPane, tab, proTreeTableView);
	}
	
	@SuppressWarnings("unchecked")
	public <ITEM extends DbPersistent<ITEM, ?>> void setTab(TabPane tabPane, Tab tab, Node node) {
		Object content = null;
		try {
			content = tab.getContent();
		} catch (NullPointerException e) {}
		if (content == null || content != node)
			tab.setContent(node);
		if (!tabPane.getTabs().contains(tab))
			tabPane.getTabs().add(tab);
		if (tabPane.getSelectionModel().getSelectedItem() != tab)
			tabPane.getSelectionModel().select(tab);
		
		if (node instanceof ProTableView)
			setTableTab(tab, (ProTableView<ITEM>)node);
		
		if (node instanceof ProTreeTableView)
			setTreeTableTab(tab, (ProTreeTableView<ITEM>)node);
	}
	
	@SuppressWarnings("unchecked")
	private <ITEM extends DbPersistent<ITEM, ?>> void setTableTab(Tab tab, ProTableView<ITEM> tableViewSpring) {
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
	
	private <ITEM extends DbPersistent<ITEM, ?>> void setTreeTableTab(Tab tab, ProTreeTableView<ITEM> proTreeTableView) {
		
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

		addMenuItem(rowMenu, "Show Tree View", event -> {
			changeToTreeTableView(tableViewSpring);
		});

		// only display context menu for non-null items:
		row.contextMenuProperty()
			.bind(Bindings
				.when(Bindings.isNotNull(row.itemProperty()))
				.then(rowMenu).otherwise((ContextMenu) null));
		return row;
	}
	
	private <ITEM extends DbPersistent<ITEM, ?>> TreeTableRow<Object> createTreeTableViewContext(
		ProTreeTableView<ITEM> proTreeTableView, TreeTableView<Object> tableView) {
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
	
	public TabPane getTableTabPane() {
		return tableTabPane;
	}

	public void setTableTabPane(TabPane tabPane) {
		this.tableTabPane = tabPane;
	}

	@Override
	public void addTo(AnchorPane pane) {
		FxUtil.addToAnchorPane(pane, this);
	}

}
