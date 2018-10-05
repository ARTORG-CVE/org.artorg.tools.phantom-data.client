package org.artorg.tools.phantomData.client.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.TableView;
import org.artorg.tools.phantomData.client.table.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

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
		TableView<T,?> table = createTable(itemClass);
		openMainTableTab(table, table.getTable().getTableName());
	}

	public <T extends DbPersistent<T,?>> void setSecondTable(Class<T> itemClass) {
		TableView<T,?> table = createTable(itemClass);
		setSecondTable(table);
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T,?>> TableView<T,?> createTable(
			Class<T> itemClass) {
		DbUndoRedoEditFilterTable<T> table = Reflect.createInstanceByGenericAndSuperClass(
				DbUndoRedoEditFilterTable.class, itemClass, Main.getReflections());
		DbUndoRedoAddEditControlFilterTableView<T> tableView = new DbUndoRedoAddEditControlFilterTableView<T>(itemClass);
		tableView.setTable(table);

		return tableView;
	}

	private <T extends DbPersistent<T, ?>> void openMainTableTab(TableView<T,?> table,
			String name) {
		mainSplitPane.getMainTableTabPane().openTableTab(table, name);
	}

	private <T extends DbPersistent<T, ?>> void setSecondTable(TableView<T,?> table) {
		secondTable.setTable(table);
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
