package org.artorg.tools.phantomData.client.controllers;

import java.util.ArrayList;
import java.util.List;

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
	private SmartTabPane itemAddEditTabPane;
	private SmartTabPane viewerTabPane;
	private boolean controlDown;
	private List<Runnable> menuItemUpdater;

	{
		splitPane = this;
		tableTabPane = new SmartTabPane(() -> splitPane.getItems());
		itemAddEditTabPane = new SmartTabPane(() -> splitPane.getItems());
		viewerTabPane = new SmartTabPane(() -> splitPane.getItems());
		menuItemUpdater = new ArrayList<Runnable>();

		splitPane.setOrientation(Orientation.HORIZONTAL);
		
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
		
	}

	public <ITEM extends DbPersistent<ITEM, ?>> void openViewerTab(Tab tab) {
		setTab(viewerTabPane.getTabPane(), tab, tab.getContent());
	}
	
	public <ITEM extends DbPersistent<ITEM, ?>> void openTableTab(Tab tab) {
		setTab(tableTabPane.getTabPane(), tab, tab.getContent());
	}
	
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
			addTab(itemAddEditTabPane.getTabPane(), node,
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
				addTab(itemAddEditTabPane.getTabPane(), node, "Edit "
					+ tableViewSpring.getTable().getItemName());
			});

			addMenuItem(rowMenu, "Add item", event -> {
				FxFactory<ITEM> controller = ((IDbFactoryTableView<ITEM>) tableViewSpring)
					.createFxFactory();
				Node node = controller.create(row.getItem());
				addTab(itemAddEditTabPane.getTabPane(), node, "Add "
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
			addTab(viewerTabPane.getTabPane(), scene3d,
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
			if (tabePane == itemAddEditTabPane.getTabPane())
				splitPane.getItems().add(1, tabePane);
			if (tabePane == viewerTabPane.getTabPane())
				splitPane.getItems().add(splitPane.getItems().size(), tabePane);
		}
		tabePane.getTabs().add(tab);
		tabePane.getSelectionModel().select(tab);
	}

}
