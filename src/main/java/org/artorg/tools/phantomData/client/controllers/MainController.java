package org.artorg.tools.phantomData.client.controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.boot.MainFx;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.TableViewFactory;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.DbFile;
import org.artorg.tools.phantomData.server.model.person.AcademicTitle;
import org.artorg.tools.phantomData.server.model.person.Person;
import org.artorg.tools.phantomData.server.model.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.model.phantom.Phantom;
import org.artorg.tools.phantomData.server.model.phantom.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.model.property.StringProperty;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import huma.io.IOutil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

public class MainController {
	private static String urlLocalhost;
	private static String urlShutdownActuator;
	private Stage stage;

	private SplitPane splitPane;
	private ObservableList<SplitTabView> splitTabViews;

	public MainController(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private StackPane rootPane;

	@FXML
	private AnchorPane contentPane;

	@FXML
	private Menu menuTables;

	@FXML
	MenuItem menuItemLoginLogout;

	@FXML
	void about(ActionEvent event) {

	}

	@FXML
	void close(ActionEvent event) {
		close();
	}

	private void close() {
		stage.hide();

		if (Main.getClientBooter().getServerBooter().isServerStartedEmbedded())
			Main.getClientBooter().getServerBooter().shutdownSpringServer();

		Platform.exit();

		System.exit(0);
	}

	@FXML
	void refresh(ActionEvent event) {

	}

	@FXML
	void save(ActionEvent event) {
//    	table.getTable().getUndoManager().save();
	}

	@FXML
	void undo(ActionEvent event) {
//    	table.getTable().getUndoManager().undo();
	}

	@FXML
	void redo(ActionEvent event) {
//    	table.getTable().getUndoManager().redo();
	}

	@FXML
	void executeQuery(ActionEvent event) {
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 400, 400);
		Stage stage = new Stage();
		stage.setScene(scene);

		TextField textField = new TextField();
		textField.setAlignment(Pos.TOP_LEFT);
		VBox vBox = new VBox();
		ChoiceBox<String> choiceBox = new ChoiceBox<String>();
		String EXECUTE = "execute";
		String UPDATE = "executeUpdate";
		ObservableList<String> choiceBoxItems =
			FXCollections.observableArrayList(EXECUTE, UPDATE);
		choiceBox.setItems(choiceBoxItems);
		Button button = new Button("Execute Query");
		button.setOnAction(buttonEvent -> {
			Connection conn = Main.getClientBooter().getServerBooter().getConnection();
			String statementChoice = choiceBox.getSelectionModel().getSelectedItem();
			Statement statement = null;

			if (statementChoice.equals(EXECUTE)) {
				try {
					statement = conn.createStatement();
					statement.execute(textField.getText());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (statementChoice.equals(UPDATE)) {
				try {
					statement = conn.createStatement();
					statement.executeUpdate(textField.getText());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		});
		vBox.getChildren().addAll(choiceBox, textField, button);

		FxUtil.addToAnchorPane(root, vBox);

		stage.show();
	}

	@FXML
	void initialize() {
		assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'Table.fxml'.";
		assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'Table.fxml'.";
		assert menuItemLoginLogout != null : "fx:id=\"menuItemLoginLogout\" was not injected: check your FXML file 'Table.fxml'.";
	}

	public void init() {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				close();
			}
		});

		this.splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		splitTabViews = FXCollections.<SplitTabView>observableArrayList();
		splitTabViews.addListener(new ListChangeListener<SplitTabView>() {
			@Override
			public void onChanged(Change<? extends SplitTabView> c) {
				if (c.next())
					do {
					if (c.wasAdded())
						splitPane.getItems()
							.addAll(c.getAddedSubList().stream()
								.map(splitTabView -> splitTabView.getSplitPane())
								.collect(Collectors.toList()));
					if (c.wasRemoved())
						splitPane.getItems()
							.removeAll(c.getRemoved().stream()
								.map(splitTabView -> splitTabView.getSplitPane())
								.collect(Collectors.toList()));
				} while (c.next());
			}
		});

		addSplitTabView();
		addSplitTabView();
		getOrCreate(0).openTableTab(createTableViewTab(Phantom.class));
		File file = IOutil.readResourceAsFile("model.stl");
		getOrCreate(0).openViewerTab(createScene3dTab(file));
		getOrCreate(1).openTableTab(createTreeTableViewTab(Phantom.class));

		FxUtil.addToAnchorPane(contentPane, splitPane);

	}

	@FXML
	void export(ActionEvent event) {

	}

	@FXML
	void newEntity(ActionEvent event) {

	}

	@FXML
	void newSplitTabPane(ActionEvent event) {
		addSplitTabView();
	}

	private void addSplitTabView() {
		SplitTabView splitTabView = new SplitTabView();

		ContextMenu contextMenu = new ContextMenu();
		addMenuItem(contextMenu, "Close", event -> {
			splitTabViews.remove(splitTabView);
		});
		splitTabView.getSplitPane().setContextMenu(contextMenu);

		splitTabViews.add(splitTabView);
	}

	@FXML
	void openPerspectiveComparePhantoms(ActionEvent event) {

	}

	@FXML
	void preferences(ActionEvent event) {

	}

	@FXML
	void resetPerspective(ActionEvent event) {

	}

	@FXML
	void loginLogout(ActionEvent event) {
		MainFx.openFrame("Login/Logout", new LoginController());
	}

	public <T extends DbPersistent<T, ?>> void openTable(Class<T> itemClass) {
		openTableViewTab(0, itemClass);
	}

	public <T extends DbPersistent<T, ?>> void openTableViewTab(int row,
		Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		openTableTab(row, table, table.getTable().getTableName());
	}

	private Tab createScene3dTab(File file) {
		Tab tab = new Tab("3D Viewer");
		Scene3D scene3d = new Scene3D();
		scene3d.loadFile(file);
		tab.setContent(scene3d);
		return tab;
	}

	private <T extends DbPersistent<T, ?>> Tab createTableViewTab(Class<T> itemClass) {
		ProTableView<T> table = createTable(itemClass);
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);
		return tab;
	}

	private <T extends DbPersistent<T, ?>> Tab
		createTreeTableViewTab(Class<T> itemClass) {
		ProTreeTableView<T> table = createTreeTable(itemClass);
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);
		return tab;
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T, ?>> ProTableView<T> createTable(
		Class<T> itemClass) {
		return TableViewFactory.createInitializedTable(itemClass,
			DbUndoRedoFactoryEditFilterTable.class,
			DbUndoRedoAddEditControlFilterTableView.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T, ?>> ProTreeTableView<T> createTreeTable(
		Class<T> itemClass) {
		return TableViewFactory.createInitializedTreeTable(itemClass,
			DbTable.class, DbTreeTableView.class);
	}

	public <T extends DbPersistent<T, ?>> void openTableTab(int row,
		Node table, String name) {
		Tab tab = new Tab(name);
		tab.setContent(table);
		getOrCreate(row).openTableTab(tab);
	}

	public SplitTabView getOrCreate(int row) {
		if (splitTabViews.size() - 1 < row)
			for (int i = splitTabViews.size() - 1; i < row; i++)
			splitTabViews.add(new SplitTabView());
		return splitTabViews.get(row);
	}

	@FXML
	void openTableFileTypes(ActionEvent event) {
		openTable(FileType.class);
	}

	@FXML
	void openTableFiles(ActionEvent event) {
		System.out.println(event.getSource().getClass());
		openTable(DbFile.class);
	}

	@FXML
	void openTablePhantoms(ActionEvent event) {
		openTable(Phantom.class);
	}

//	private Button createTabButton(String iconName) {
//        Button button = new Button();
//        ImageView imageView = new ImageView(new Image(IOutil.readResourceAsStream(iconName),
//                16, 16, false, true));
//        button.setGraphic(imageView);
//        button.getStyleClass().add("tab-button");
//        return button;
//    }

	@FXML
	void openTableSpecials(ActionEvent event) {
		openTable(Special.class);
	}

	@FXML
	void openTableAnnulusDiameter(ActionEvent event) {
		openTable(AnnulusDiameter.class);
	}

	@FXML
	void openTableFabricationTypes(ActionEvent event) {
		openTable(FabricationType.class);
	}

	@FXML
	void openTableLiteratureBases(ActionEvent event) {
		openTable(LiteratureBase.class);
	}

	@FXML
	void openTablePropertyFields(ActionEvent event) {
		openTable(PropertyField.class);
	}

	@FXML
	void openTableAcademicTitles(ActionEvent event) {
		openTable(AcademicTitle.class);
	}

	@FXML
	void openTablePersons(ActionEvent event) {
		openTable(Person.class);
	}

	@FXML
	void openTableBooleanProperties(ActionEvent event) {
		openTable(BooleanProperty.class);
	}

	@FXML
	void openTableDoubleProperties(ActionEvent event) {
		openTable(DoubleProperty.class);
	}

	@FXML
	void openTableIntegerProperties(ActionEvent event) {
		openTable(IntegerProperty.class);
	}

	@FXML
	void openTableStringProperties(ActionEvent event) {
		openTable(StringProperty.class);
	}

	public static String getUrlLocalhost() {
		return urlLocalhost;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		System.out.println(urlLocalhost);
		MainController.urlLocalhost = urlLocalhost;
	}

	public static String getUrlShutdownActuator() {
		return urlShutdownActuator;
	}

	public static void setUrlShutdownActuator(String urlShutdownActuator) {
		MainController.urlShutdownActuator = urlShutdownActuator;
	}

}
