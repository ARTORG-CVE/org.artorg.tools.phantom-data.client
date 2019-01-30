package org.artorg.tools.phantomData.client.controllers;

import static org.artorg.tools.phantomData.client.util.FxUtil.addMenuItem;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.DesktopFxBootApplication;
import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.admin.UserAdmin;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.scene.CssGlyph;
import org.artorg.tools.phantomData.client.scene.control.DbEntityView;
import org.artorg.tools.phantomData.client.scene.control.EntityView;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.tableView.DbTableView;
import org.artorg.tools.phantomData.client.scene.control.tableView.ProTableView;
import org.artorg.tools.phantomData.client.scene.control.treeTableView.DbTreeTableView;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;
import org.artorg.tools.phantomData.server.model.AbstractProperty;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.base.FileTag;
import org.artorg.tools.phantomData.server.models.base.person.AcademicTitle;
import org.artorg.tools.phantomData.server.models.base.person.Person;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;
import org.artorg.tools.phantomData.server.models.base.property.StringProperty;
import org.artorg.tools.phantomData.server.models.measurement.ExperimentalSetup;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.measurement.Project;
import org.artorg.tools.phantomData.server.models.measurement.Simulation;
import org.artorg.tools.phantomData.server.models.phantom.AnnulusDiameter;
import org.artorg.tools.phantomData.server.models.phantom.FabricationType;
import org.artorg.tools.phantomData.server.models.phantom.LiteratureBase;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Material;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;
import org.artorg.tools.phantomData.server.models.phantom.SimulationPhantom;
import org.artorg.tools.phantomData.server.models.phantom.Special;
import org.controlsfx.glyphfont.FontAwesome;

import huma.logging.Level;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController extends StackPane {
	private final MenuBar menuBar;
	private static String urlShutdownActuator;
	private final Stage stage;
	private final AnchorPane contentPane;
	private final StackPane rootPane;
	private final SplitPane splitPane;
	private final ObservableList<SplitTabView> splitTabViews;
	private final List<SplitTabView> readOnlySplitTabViews;
	private static String urlLocalhost;
	private final Label statusLabel;
	private final Rectangle coloredStatusBox;

	@SuppressWarnings("unchecked")
	public MainController(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				close();
			}
		});

		rootPane = this;
		contentPane = new AnchorPane();
		splitPane = new SplitPane();
		splitTabViews = FXCollections.<SplitTabView>observableArrayList();
		
		menuBar = new MenuBar();

		BorderPane desktopLayout = new BorderPane();
		desktopLayout.setTop(menuBar);
		desktopLayout.setCenter(contentPane);
		statusLabel = new Label();

		HBox hBox = new HBox();
		coloredStatusBox = new Rectangle();
		coloredStatusBox.setWidth(8.0);
		coloredStatusBox.setHeight(8.0);

		HBox.setMargin(coloredStatusBox, new Insets(0, 5, 0, 5));
		hBox.getChildren().addAll(coloredStatusBox, statusLabel);
		hBox.setAlignment(Pos.CENTER_LEFT);
		desktopLayout.setBottom(hBox);

		FxUtil.addToPane(rootPane, desktopLayout);
		VBox.setVgrow(splitPane, Priority.ALWAYS);
		VBox.setVgrow(contentPane, Priority.ALWAYS);
		splitPane.setOrientation(Orientation.VERTICAL);
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
		readOnlySplitTabViews = FXCollections.unmodifiableObservableList(splitTabViews);
		
		initMenuBar(menuBar);

		FxUtil.addToPane(contentPane, splitPane);

		while (!Main.isInitialized()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			splitTabViews.get(0).openTableTab(createTableViewTab(Phantom.class));
			splitTabViews.get(0).openViewerTab(createScene3dTab(null));
			splitTabViews.get(1).openTableTab(createTreeTableViewTab(Phantom.class));
			DbTableView<Phantom> phantomTable = (DbTableView<Phantom>) splitTabViews.get(0).getTableTabPane().getTabPane().getTabs().get(0).getContent();
			phantomTable.getSelectionModel().selectFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	MenuItem menuItemLoginLogout;

	void about(ActionEvent event) {

	}

	void close(ActionEvent event) {
		close();
	}

	private void close() {
		stage.hide();
		if (Main.getBooter().isServerStartedEmbedded()) Main.getBooter().shutdownSpringServer();
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
		if (menuBar.getMenus().stream().filter(menu -> menu.getText().equals("Dev. Tools"))
				.findFirst().isPresent())
			return;

		Menu menu = new Menu("Dev. Tools");
		MenuItem menuItem = new MenuItem("Execute Query");
		menuItem.setOnAction(event -> executeQuery());
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);
	}

	public void removeDevToolsMenu() {
		menuBar.getMenus().removeAll(menuBar.getMenus().stream()
				.filter(menu -> menu.getText().equals("Dev. Tools")).collect(Collectors.toList()));
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
		ObservableList<String> choiceBoxItems = FXCollections.observableArrayList(EXECUTE, UPDATE);
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
		SplitTabView splitTabView = new SplitTabView();
		ContextMenu contextMenu = new ContextMenu();

		Menu tableMenu = new Menu("Open Table");
		createTableMenu(tableMenu, splitTabView,
				(view, cls) -> view.openTableTab(createTableViewTab(cls)));
		contextMenu.getItems().add(tableMenu);

		Menu treeTableMenu = new Menu("Open Tree Table");
		createTableMenu(treeTableMenu, splitTabView,
				(view, cls) -> view.openTableTab(createTreeTableViewTab(cls)));
		contextMenu.getItems().add(treeTableMenu);

		splitTabView.getSplitPane().setContextMenu(contextMenu);

		splitTabViews.add(splitTabView);
	}

	private void createTableMenu(Menu menu, SplitTabView splitTabView,
			BiConsumer<SplitTabView, Class<?>> tableFactoryCreator) {

		addMenuItem(menu, "Phantoms", event -> {
			tableFactoryCreator.accept(splitTabView, Phantom.class);
		});
		addMenuItem(menu, "Measurements", event -> {
			tableFactoryCreator.accept(splitTabView, Measurement.class);
		});
		addMenuItem(menu, "Files", event -> {
			tableFactoryCreator.accept(splitTabView, DbFile.class);
		});
		addMenuItem(menu, "Property Fields", event -> {
			tableFactoryCreator.accept(splitTabView, PropertyField.class);
		});

		menu.getItems().add(new SeparatorMenuItem());

		{
			Menu subMenu = new Menu("Phantom");
			addMenuItem(subMenu, "Phantoms", event -> {
				tableFactoryCreator.accept(splitTabView, Phantom.class);
			});
			addMenuItem(subMenu, "Simulation Phantoms", event -> {
				tableFactoryCreator.accept(splitTabView, SimulationPhantom.class);
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
			addMenuItem(subMenu, "Specials", event -> {
				tableFactoryCreator.accept(splitTabView, Special.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "Manufacturings", event -> {
				tableFactoryCreator.accept(splitTabView, Manufacturing.class);
			});
			addMenuItem(subMenu, "Materials", event -> {
				tableFactoryCreator.accept(splitTabView, Material.class);
			});
			menu.getItems().add(subMenu);
		}

		{
			Menu subMenu = new Menu("Measurement");
			addMenuItem(subMenu, "Measurements", event -> {
				tableFactoryCreator.accept(splitTabView, Measurement.class);
			});
			addMenuItem(subMenu, "Simulations", event -> {
				tableFactoryCreator.accept(splitTabView, Simulation.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "Projects", event -> {
				tableFactoryCreator.accept(splitTabView, Project.class);
			});
			addMenuItem(subMenu, "Experimental Setups", event -> {
				tableFactoryCreator.accept(splitTabView, ExperimentalSetup.class);
			});
			menu.getItems().add(subMenu);
		}

		{
			Menu subMenu = new Menu("File");
			addMenuItem(subMenu, "Files", event -> {
				tableFactoryCreator.accept(splitTabView, DbFile.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "File tags", event -> {
				tableFactoryCreator.accept(splitTabView, FileTag.class);
			});
			menu.getItems().add(subMenu);
		}

		{
			Menu subMenu = new Menu("Person");
			addMenuItem(subMenu, "Persons", event -> {
				tableFactoryCreator.accept(splitTabView, Person.class);
			});
			subMenu.getItems().add(new SeparatorMenuItem());
			addMenuItem(subMenu, "Academic Titles", event -> {
				tableFactoryCreator.accept(splitTabView, AcademicTitle.class);
			});
			menu.getItems().add(subMenu);
		}

		{
			Menu subMenu = new Menu("Properties");
			addMenuItem(subMenu, "Properties", event -> {
				tableFactoryCreator.accept(splitTabView, AbstractProperty.class);
			});
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
			addMenuItem(subMenu, "Double Propertyie", sevent -> {
				tableFactoryCreator.accept(splitTabView, DoubleProperty.class);
			});
			addMenuItem(subMenu, "String Properties", event -> {
				tableFactoryCreator.accept(splitTabView, StringProperty.class);
			});
			menu.getItems().add(subMenu);
		}

		menu.getItems().add(new SeparatorMenuItem());

		addMenuItem(menu, "All", event -> {
			tableFactoryCreator.accept(splitTabView, AbstractPersonifiedEntity.class);
		});
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
		ProTableView<?> table = Main.getUIEntity(itemClass).createDbTableView();
		openTableTab(row, table, table.getTable().getTableName());
	}

	private Tab createScene3dTab(File file) {
		Tab tab = new Tab("Viewer");
		Scene3D scene3d = new Scene3D();
		if (file != null) scene3d.loadFile(file);
		tab.setContent(scene3d);
		return tab;
	}

	private Tab createTableViewTab(Class<?> itemClass) {
		ProTableView<?> table = Main.getUIEntity(itemClass).createDbTableView();
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);

		if (table.getItemClass() == DbFile.class) addFilesDragNDropListener(table);

		return tab;
	}

	@SuppressWarnings("unchecked")
	private Tab createTreeTableViewTab(Class<?> itemClass) {
		DbTreeTableView<Object> table = (DbTreeTableView<Object>) Main.getUIEntity(itemClass).createProTreeTableView();
		Collection<Object> items = (Collection<Object>) Connectors.get(itemClass).readAllAsList();
		table.setItems(items);
		Tab tab = new Tab(table.getTable().getTableName());
		tab.setContent(table);
		tab.setText(table.getTable().getTableName());
		return tab;
	}

	public void openTableTab(int row, ProTableView<?> table, String name) {
		Tab tab = new Tab(name);
		tab.setContent(table);
		tab.setText(table.getTable().getTableName());
		splitTabViews.get(row).openTableTab(tab);
	}

//	private Button createTabButton(String iconName) {
//        Button button = new Button();
//        ImageView imageView = new ImageView(new Image(IOutil.readResourceAsStream(iconName),
//                16, 16, false, true));
//        button.setGraphic(imageView);
//        button.getStyleClass().add("tab-button");
//        return button;
//    }

	private void addFilesDragNDropListener(EntityView view) {
		view.getNode().setOnDragOver(event -> {
			if (event.getGestureSource() != view.getNode() && event.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});

		view.getNode().setOnDragDropped(new EventHandler<DragEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					List<File> files = db.getFiles();
					event.setDropCompleted(true);

					ICrudConnector<DbFile> connector = Connectors.get(DbFile.class);

					List<DbFile> dbFiles = files.stream().map(file -> new DbFile(file))
							.collect(Collectors.toList());

					Platform.runLater(() -> {
						if (!UserAdmin.isUserLoggedIn()) {
							openLoginLogoutFrame();
							return;
						}

						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Adding Files");
						if (dbFiles.size() < 5) {
							String filesText = dbFiles.stream()
									.map(file -> file.getName() + "." + file.getExtension())
									.collect(Collectors.joining("\n  - ", "  - ", ""));
							alert.setContentText(String.format("Start import of %d files:\n%s",
									dbFiles.size(), filesText));
						} else
							alert.setContentText(
									String.format("Start import of %d files", dbFiles.size()));
						alert.showAndWait();

						if (alert.getResult() != ButtonType.OK) return;

						Task<Void> task = new Task<Void>() {
							@Override
							protected Void call() throws Exception {

								for (int i = 0; i < dbFiles.size(); i++) {
									if (this.isCancelled()) break;
									DbFile dbFile = dbFiles.get(i);
									File file = files.get(i);
									try {
										dbFile.putFile(file);
										connector.create(dbFile);
									} catch (NoUserLoggedInException e) {
										Logger.warn.println(e.getMessage());
										e.showAlert();
									} catch (PostException e) {
										Logger.error.println(e.getMessage());
										e.printStackTrace();
										e.showAlert();
									}
									updateMessage(String.format("Copying files  %d/%d\n%s", i,
											dbFiles.size(),
											dbFile.getName() + "." + dbFile.getExtension()));
									updateProgress(i + 1, dbFiles.size());
								}
								return null;
							}
						};

						SimpleProgressDialog dialog =
								new SimpleProgressDialog(task, "Copying progress");
						dialog.showAndWait();

						if (view instanceof DbEntityView) ((DbEntityView) view).reload();
						List<DbFile> items = ((Table<DbFile>) view.getTable()).getItems();
						List<UUID> newIds = dbFiles.stream().map(dbFile -> dbFile.getId())
								.collect(Collectors.toList());
						List<Integer> newIndexes = new ArrayList<>();
						for (int j = 0; j < items.size(); j++) {
							DbFile item = items.get(j);
							if (newIds.stream().filter(id -> id.equals(item.getId())).findFirst()
									.isPresent())
								newIndexes.add(j);
						}

						if (newIndexes.size() == 1)
							view.getSelectionModel().select(newIndexes.get(0));
						else if (newIndexes.size() > 1) {
							int row = newIndexes.get(0);
							int[] rows = new int[newIndexes.size() - 1];
							for (int k = 0; k < rows.length; k++) {
								rows[k] = newIndexes.get(k + 1);
							}
							view.getSelectionModel().selectIndices(row, rows);
						}
					});
				}

				event.consume();
			}
		});

	}

	public static String getUrlLocalhost() {
		return urlLocalhost;
	}

	public static void setUrlLocalhost(String urlLocalhost) {
		MainController.urlLocalhost = urlLocalhost;
	}

	public static String getUrlShutdownActuator() {
		return urlShutdownActuator;
	}

	public static void setUrlShutdownActuator(String urlShutdownActuator) {
		MainController.urlShutdownActuator = urlShutdownActuator;
	}

	private void initMenuBar(MenuBar menuBar) {
		Menu menu;
		MenuItem menuItem;

		menu = new Menu("File");

		menuItem = new MenuItem("Login/Logout...",
				new CssGlyph("FontAwesome", FontAwesome.Glyph.USER));
		menuItem.setOnAction(event -> loginLogout(event));
		menu.getItems().add(menuItem);

//		menu.getItems().add(new MenuItem("Preference"));
		menuItem = new MenuItem("Close");
		menuItem.setOnAction(event -> close(event));
		menu.getItems().add(menuItem);
		menuBar.getMenus().add(menu);

		menu = new Menu("Table");
		createTableMenu(menu, splitTabViews.get(0),
				(view, cls) -> view.openTableTab(createTableViewTab(cls)));
		menuBar.getMenus().add(menu);

		menu = new Menu("TreeTable");
		createTableMenu(menu, splitTabViews.get(0),
				(view, cls) -> view.openTableTab(createTreeTableViewTab(cls)));
		menuBar.getMenus().add(menu);

		menu = new Menu("Window");
		menuItem = new MenuItem("Open Viewer");
		menuItem.setOnAction(event -> {
			Scene3D scene3d = new Scene3D();
			splitTabViews.get(0).addTab(splitTabViews.get(0).getViewerTabPane().getTabPane(),
					scene3d, "Viewer");
		});
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Show LoggingPane");
		menuItem.setOnAction(event -> {
			Main.getBooter().getConsoleFrame().setVisible(true);
		});
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		menu = new Menu("Help");
		menuItem = new MenuItem("About");
		menuItem.setOnAction(event -> {
			AboutController controller = new AboutController(stage);
			Parent parent =
					FxUtil.loadFXML("fxml/About.fxml", controller, DesktopFxBootApplication.class);
			FxUtil.openFrame("About Phantom Database", parent);
		});
		menu.getItems().add(menuItem);
		menuBar.getMenus().add(menu);
	}

	public void setStatus(String text, boolean status) {
		final Pattern pattern1 = Pattern.compile(
				"(\\d+-\\d+-\\d+)\\W*(\\d+.?\\d+.?\\d+)\\W+(\\d+)ms\\W*(\\w*)\\W*(.*)(org.artorg.*)");
		final Pattern pattern2 = Pattern
				.compile("(\\d+-\\d+-\\d+)\\W*(\\d+.?\\d+.?\\d+)\\W+(\\d+)ms\\W*(\\w*)\\W*(.*)");
		Matcher matcher = pattern1.matcher(text);
		String logLevel = null;

		if (matcher.find()) {
//			String date = matcher.group(1);
			String time = matcher.group(2);
			String millis = matcher.group(3);
			logLevel = matcher.group(4);
			String message = matcher.group(5);
//			String trace = matcher.group(6);
			text = String.format("%s, %sms, %s : %s", time, millis, logLevel, message);
		} else {
			Matcher matcher2 = pattern2.matcher(text);
			if (matcher2.find()) {
//				String date = matcher.group(1);
				String time = matcher2.group(2);
				String millis = matcher2.group(3);
				logLevel = matcher2.group(4);
				String message = matcher2.group(5);
				text = String.format("%s, %sms, %s : %s", time, millis, logLevel, message);
			}
		}
		Level level = null;

		if (logLevel != null) {
			if (logLevel.equals("DEBUG")) level = Level.DEBUG;
			else if (logLevel.equals("INFO")) level = Level.INFO;
			else if (logLevel.equals("WARN")) level = Level.WARN;
			else if (logLevel.equals("ERROR")) level = Level.ERROR;
			else if (logLevel.equals("FATAL")) level = Level.FATAL;
			else
				level = null;
		}

		if (logLevel == null) {
			if (text.contains("DEBUG")) level = Level.DEBUG;
			else if (text.contains("INFO")) level = Level.INFO;
			else if (text.contains("WARN")) level = Level.WARN;
			else if (text.contains("ERROR")) level = Level.ERROR;
			else if (text.contains("FATAL")) level = Level.FATAL;
		}

		if (level == null) {
			if (!status) coloredStatusBox.setFill(Color.RED);
			return;
		}

		statusLabel.setText(text);
		if (!status) coloredStatusBox.setFill(Color.RED);

		else if (level == Level.ERROR || level == Level.FATAL) {
			coloredStatusBox.setFill(Color.RED);
		} else if (level == Level.WARN) coloredStatusBox.setFill(Color.ORANGE);
		else
			coloredStatusBox.setFill(Color.GREEN);
	}

	public List<SplitTabView> getSplitTabViews() {
		return readOnlySplitTabViews;
	}

}
