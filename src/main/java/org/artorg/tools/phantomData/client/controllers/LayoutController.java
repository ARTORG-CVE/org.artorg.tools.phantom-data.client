package org.artorg.tools.phantomData.client.controllers;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.TableBase;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class LayoutController {
	private MainSplitPane mainSplitPane;
	private SecondTable secondTable;
	private Scene3D scene3d;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane parentPane, mainTablePane, bottomTablePane, pane3d;

	@FXML
	void initialize() {
		assert parentPane != null : "fx:id=\"parentPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
		assert mainTablePane != null : "fx:id=\"mainTablePane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
		assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
		assert bottomTablePane != null : "fx:id=\"tableBottomPane\" was not injected: check your FXML file 'PhantomLayout.fxml'.";
	}

	public void init() {
		mainSplitPane = new MainSplitPane();
		mainSplitPane.addTo(mainTablePane);
		secondTable = new SecondTable();
		secondTable.addTo(bottomTablePane);
		scene3d = new Scene3D();
		scene3d.addTo(pane3d);
	}

	public <T extends DbPersistent<T,?>> void openMainTableTab(Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		openMainTableTab(table, table.getTable().getTableName());
	}

	public <T extends DbPersistent<T,?>> void setSecondTable(Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		setSecondTable(table);
	}
	
	public void setSecondTreeTable(ProTreeTableView treeTableView) {
		secondTable.setTreeTableView(treeTableView);
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T,?>> ProTableView<T> createTable(
			Class<T> itemClass) {
		return TableViewFactory.createInitializedTable(itemClass, DbUndoRedoFactoryEditFilterTable.class, DbUndoRedoAddEditControlFilterTableView.class);
	}

	private <T extends DbPersistent<T, ?>> void openMainTableTab(ProTableView<T> table,
			String name) {
		mainSplitPane.getMainTableTabPane().openTableTab(table, name);
	}

	private <T extends DbPersistent<T, ?>> void setSecondTable(ProTableView<T> table) {
		secondTable.setTableView(table);
	}

	public void set3dFile(File file) {
		scene3d.loadFile(file);
	}

	public SecondTable getSecondTable() {
		return secondTable;
	}

	public Scene3D getScene3d() {
		return scene3d;
	}

}
