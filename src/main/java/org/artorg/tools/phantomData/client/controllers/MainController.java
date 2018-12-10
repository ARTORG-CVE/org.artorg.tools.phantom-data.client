package org.artorg.tools.phantomData.client.controllers;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.scene.CssGlyph;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.ProTreeTableView;
import org.artorg.tools.phantomData.client.table.DbFilterTable;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.TableViewFactory;
import org.artorg.tools.phantomData.server.model.base.*;
import org.artorg.tools.phantomData.server.model.base.person.*;
import org.artorg.tools.phantomData.server.model.base.property.*;
import org.artorg.tools.phantomData.server.model.measurement.*;
import org.artorg.tools.phantomData.server.model.phantom.*;
import org.artorg.tools.phantomData.server.specification.DbPersistent;
import org.controlsfx.glyphfont.FontAwesome;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController extends StackPane {
	private MenuBar menuBar;
	private static String urlShutdownActuator;
	private Stage stage;
	private AnchorPane contentPane;
	private StackPane rootPane;
	private SplitPane splitPane;
	private ObservableList<SplitTabView> splitTabViews;
	private static String urlLocalhost;

	public MainController(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				close();
			}
		});

		VBox vBox = new VBox();
		menuBar = new MenuBar();
		rootPane = this;
		contentPane = new AnchorPane();
		splitPane = new SplitPane();

		vBox.getChildren().add(menuBar);
		vBox.getChildren().add(contentPane);
		VBox.setVgrow(splitPane, Priority.ALWAYS);
		VBox.setVgrow(contentPane, Priority.ALWAYS);

		splitPane.setOrientation(Orientation.VERTICAL);
		splitTabViews = FXCollections.<SplitTabView>observableArrayList();
		splitTabViews.addListener((ListChangeListener<SplitTabView>) c -> {
			if (c.next()) do {
				if (c.wasAdded()) splitPane.getItems()
					.addAll(c.getAddedSubList().stream()
						.map(splitTabView -> splitTabView.getSplitPane())
						.collect(Collectors.toList()));
				if (c.wasRemoved()) splitPane.getItems()
					.removeAll(c.getRemoved().stream()
						.map(splitTabView -> splitTabView.getSplitPane())
						.collect(Collectors.toList()));
			} while (c.next());
		});

		addSplitTabView();
		addSplitTabView();
		
		getOrCreate(0).openTableTab(createTableViewTab(Phantom.class));
		getOrCreate(0).openViewerTab(createScene3dTab(null));
		getOrCreate(1).openTableTab(createTreeTableViewTab(Phantom.class));

		FxUtil.addToPane(contentPane, splitPane);

		Menu menu;
		MenuItem menuItem;

		menu = new Menu("File");
		menuItem =
			new MenuItem("New", new CssGlyph("FontAwesome", FontAwesome.Glyph.FILE));
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Export...",
			new CssGlyph("FontAwesome", FontAwesome.Glyph.CARET_UP));
		menuItem.setOnAction(event -> loginLogout(event));
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Login/Logout...",
			new CssGlyph("FontAwesome", FontAwesome.Glyph.SIGN_IN));
		menuItem.setOnAction(event -> loginLogout(event));
		menu.getItems().add(menuItem);

//		menu.getItems().add(new MenuItem("Preference"));
		menuItem = new MenuItem("Close");
		menuItem.setOnAction(event -> close(event));
		menu.getItems().add(menuItem);
		menuBar.getMenus().add(menu);

		menu = new Menu("Edit");
		menuItem = new MenuItem("Undo",
			new CssGlyph("FontAwesome", FontAwesome.Glyph.ROTATE_LEFT));
		menuItem.setAccelerator(
			new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		menuItem.setOnAction(event -> undo(event));
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Redo",
			new CssGlyph("FontAwesome", FontAwesome.Glyph.ROTATE_RIGHT));
		menuItem.setAccelerator(
			new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		menuItem.setOnAction(event -> redo(event));
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Refresh",
			new CssGlyph("FontAwesome", FontAwesome.Glyph.REFRESH));
		menuItem.setOnAction(event -> refresh(event));
		menu.getItems().add(menuItem);

		menuItem =
			new MenuItem("Delete", new CssGlyph("FontAwesome", FontAwesome.Glyph.REMOVE));
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		menu = new Menu("Table");
		createTableMenu(menu, splitTabViews.get(0),
			(view, cls) -> view.openTableTab(createTableViewTab(castClass(cls))));
		menuBar.getMenus().add(menu);

		menu = new Menu("TreeTable");
		createTableMenu(menu, splitTabViews.get(0),
			(view, cls) -> view.openTableTab(createTreeTableViewTab(castClass(cls))));
		menuBar.getMenus().add(menu);

		menu = new Menu("Window");
		menuItem = new MenuItem("Open SplitTabPane");
		menuItem.setOnAction(event -> newSplitTabPane(event));
		menu.getItems().add(menuItem);
		menuItem = new MenuItem("Show LoggingPane");
		menuItem.setOnAction(event -> {
			Main.getBooter().getConsoleFrame().setVisible(true);
		});
		menu.getItems().add(menuItem);
		menuBar.getMenus().add(menu);

//		menu = new Menu("Help");
//		menuItem = new MenuItem("About");
//		menu.getItems().add(menuItem);
//		menuBar.getMenus().add(menu);

		FxUtil.addToPane(rootPane, vBox);

	}

	MenuItem menuItemLoginLogout;

	void about(ActionEvent event) {

	}

	void close(ActionEvent event) {
		close();
	}

	private void close() {
		stage.hide();

		if (Main.getBooter().isServerStartedEmbedded())
			Main.getBooter().shutdownSpringServer();

		Platform.exit();

		System.exit(0);
	}

	void refresh(ActionEvent event) {

	}

	void undo(ActionEvent event) {
//    	table.getTable().getUndoManager().undo();
	}

	void redo(ActionEvent event) {
//    	table.getTable().getUndoManager().redo();
	}

	public void addDevToolsMenu() {
		if (menuBar.getMenus().stream()
			.filter(menu -> menu.getText().equals("Dev. Tools")).findFirst().isPresent())
			return;

		Menu menu = new Menu("Dev. Tools");
		MenuItem menuItem = new MenuItem("Execute Query");
		menuItem.setOnAction(event -> executeQuery());
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);
	}

	public void removeDevToolsMenu() {
		menuBar.getMenus()
			.removeAll(menuBar.getMenus().stream()
				.filter(menu -> menu.getText().equals("Dev. Tools"))
				.collect(Collectors.toList()));
	}

	private void executeQuery() {
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
			Connection conn = Main.getBooter().getConnection();
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

		FxUtil.addToPane(root, vBox);

		stage.show();
	}

	void export(ActionEvent event) {

	}

	void newEntity(ActionEvent event) {

	}

	void newSplitTabPane(ActionEvent event) {
		addSplitTabView();
	}

	private void addSplitTabView() {
		SplitTabView splitTabView =
			new SplitTabView(splitTabViews.size(), i -> splitTabViews.get(i));
		ContextMenu contextMenu = new ContextMenu();

		Menu tableMenu = new Menu("Open Table");
		createTableMenu(tableMenu, splitTabView,
			(view, cls) -> view.openTableTab(createTableViewTab(castClass(cls))));
		contextMenu.getItems().add(tableMenu);

		Menu treeTableMenu = new Menu("Open Tree Table");
		createTableMenu(treeTableMenu, splitTabView,
			(view, cls) -> view.openTableTab(createTreeTableViewTab(castClass(cls))));
		contextMenu.getItems().add(treeTableMenu);
		
		addMenuItem(contextMenu, "Close", event -> {
			splitTabViews.remove(splitTabView);
		});
		splitTabView.getSplitPane().setContextMenu(contextMenu);

		splitTabViews.add(splitTabView);
	}

	@SuppressWarnings("unchecked")
	private <T extends DbPersistent<T, ?>> Class<T> castClass(Class<?> cls) {
		return (Class<T>) cls;
	}

	private void createTableMenu(Menu menu, SplitTabView splitTabView,
		BiConsumer<SplitTabView, Class<?>> tableFactoryCreator) {

		addMenuItem(menu, "Phantom", event -> {
			tableFactoryCreator.accept(splitTabView, Phantom.class);
		});
		addMenuItem(menu, "Measurement", event -> {
			tableFactoryCreator.accept(splitTabView, Measurement.class);
		});
		addMenuItem(menu, "Experimental Setups", event -> {
			tableFactoryCreator.accept(splitTabView, ExperimentalSetup.class);
		});
		addMenuItem(menu, "Project", event -> {
			tableFactoryCreator.accept(splitTabView, Project.class);
		});
		menu.getItems().add(new SeparatorMenuItem());
		addMenuItem(menu, "Files", event -> {
			tableFactoryCreator.accept(splitTabView, DbFile.class);
		});
		menu.getItems().add(new SeparatorMenuItem());
		addMenuItem(menu, "Persons", event -> {
			tableFactoryCreator.accept(splitTabView, Person.class);
		});
		addMenuItem(menu, "Academic Titles", event -> {
			tableFactoryCreator.accept(splitTabView, AcademicTitle.class);
		});
		addMenuItem(menu, "Note", event -> {
			tableFactoryCreator.accept(splitTabView, Note.class);
		});
		menu.getItems().add(new SeparatorMenuItem());
		{
			Menu subMenu = new Menu("Phantominas");
			addMenuItem(subMenu, "Manufacturings", event -> {
				tableFactoryCreator.accept(splitTabView, Manufacturing.class);
			});
			addMenuItem(subMenu, "Phantominas", event -> {
				tableFactoryCreator.accept(splitTabView, Phantomina.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "Annulus Diameters", event -> {
				tableFactoryCreator.accept(splitTabView, AnnulusDiameter.class);
			});
			addMenuItem(subMenu, "Fabrication Types", event -> {
				tableFactoryCreator.accept(splitTabView, FabricationType.class);
			});
			addMenuItem(subMenu, "Literature Bases", event -> {
				tableFactoryCreator.accept(splitTabView, LiteratureBase.class);
			});
			addMenuItem(subMenu, "Special", event -> {
				tableFactoryCreator.accept(splitTabView, Special.class);
			});
			menu.getItems().add(subMenu);
		}
		{
			Menu subMenu = new Menu("Properties");
			addMenuItem(subMenu, "Property Fields", event -> {
				tableFactoryCreator.accept(splitTabView, PropertyField.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "Boolean Properties", event -> {
				tableFactoryCreator.accept(splitTabView, BooleanProperty.class);
			});
			addMenuItem(subMenu, "Integer Properties", event -> {
				tableFactoryCreator.accept(splitTabView, IntegerProperty.class);
			});
			addMenuItem(subMenu, "Double Properties", event -> {
				tableFactoryCreator.accept(splitTabView, DoubleProperty.class);
			});
			addMenuItem(subMenu, "String Properties", event -> {
				tableFactoryCreator.accept(splitTabView, StringProperty.class);
			});
			menu.getItems().add(subMenu);
		}
	}

	void openPerspectiveComparePhantoms(ActionEvent event) {

	}

	void preferences(ActionEvent event) {

	}

	void resetPerspective(ActionEvent event) {

	}

	void loginLogout(ActionEvent event) {
		openLoginLogoutFrame();
	}

	public void openLoginLogoutFrame() {
		FxUtil.openFrame("Login/Logout", new LoginController());
	}

	public void openTable(Class<?> itemClass) {
		openTableViewTab(0, itemClass);
	}

	public void openTableViewTab(int row, Class<?> itemClass) {
		ProTableView<?> table = createTable(itemClass);
		openTableTab(row, table, table.getTable().getTableName());
	}

	private Tab createScene3dTab(File file) {
		Tab tab = new Tab("3D Viewer");
		Scene3D scene3d = new Scene3D();
		if (file != null) scene3d.loadFile(file);
		tab.setContent(scene3d);
		return tab;
	}

	private Tab createTableViewTab(Class<?> itemClass) {
		ProTableView<?> table = createTable(itemClass);
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);
		return tab;
	}

	private Tab createTreeTableViewTab(Class<?> itemClass) {
		ProTreeTableView<?> table = createTreeTable(itemClass);
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);
		return tab;
	}

	@SuppressWarnings("unchecked")
	private ProTableView<?> createTable(Class<?> itemClass) {
		return TableViewFactory.createInitializedTableView(itemClass,
			DbFilterTable.class,
			DbEditFilterTableView.class);
	}

	@SuppressWarnings("unchecked")
	private ProTreeTableView<?> createTreeTable(Class<?> itemClass) {
		return TableViewFactory.createInitializedTreeTableView(itemClass, DbTable.class,
			DbTreeTableView.class);
	}

	public <T extends DbPersistent<T, ?>> void openTableTab(int row, Node table,
		String name) {
		Tab tab = new Tab(name);
		tab.setContent(table);
		getOrCreate(row).openTableTab(tab);
	}

	public SplitTabView getOrCreate(int row) {
		if (splitTabViews.size() - 1 < row)
			for (int i = splitTabViews.size() - 1; i < row; i++)
			splitTabViews
				.add(new SplitTabView(splitTabViews.size(), j -> splitTabViews.get(j)));
		return splitTabViews.get(row);
	}

	void openTableFiles(ActionEvent event) {
		openTable(DbFile.class);
	}

	void openTablePhantoms(ActionEvent event) {
		openTable(Phantom.class);
	}

	void openTablePhantominas(ActionEvent event) {
		openTable(Phantomina.class);
	}

//	private Button createTabButton(String iconName) {
//        Button button = new Button();
//        ImageView imageView = new ImageView(new Image(IOutil.readResourceAsStream(iconName),
//                16, 16, false, true));
//        button.setGraphic(imageView);
//        button.getStyleClass().add("tab-button");
//        return button;
//    }

	void openTableSpecials(ActionEvent event) {
		openTable(Special.class);
	}

	void openTableAnnulusDiameter(ActionEvent event) {
		openTable(AnnulusDiameter.class);
	}

	void openTableFabricationTypes(ActionEvent event) {
		openTable(FabricationType.class);
	}

	void openTableLiteratureBases(ActionEvent event) {
		openTable(LiteratureBase.class);
	}

	void openTablePropertyFields(ActionEvent event) {
		openTable(PropertyField.class);
	}

	void openTableAcademicTitles(ActionEvent event) {
		openTable(AcademicTitle.class);
	}

	void openTablePersons(ActionEvent event) {
		openTable(Person.class);
	}

	void openTableBooleanProperties(ActionEvent event) {
		openTable(BooleanProperty.class);
	}

	void openTableDoubleProperties(ActionEvent event) {
		openTable(DoubleProperty.class);
	}

	void openTableIntegerProperties(ActionEvent event) {
		openTable(IntegerProperty.class);
	}

	void openTableStringProperties(ActionEvent event) {
		openTable(StringProperty.class);
	}

	void openTableFileTags(ActionEvent event) {
		openTable(FileTag.class);
	}

	void openTableMeasurement(ActionEvent event) {
		openTable(Measurement.class);
	}

	public static String getUrlLocalhost() {
		return urlLocalhost;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		System.out.println("MainController: " + urlLocalhost);
		MainController.urlLocalhost = urlLocalhost;
	}

	public static String getUrlShutdownActuator() {
		return urlShutdownActuator;
	}

	public static void setUrlShutdownActuator(String urlShutdownActuator) {
		MainController.urlShutdownActuator = urlShutdownActuator;
	}

}
