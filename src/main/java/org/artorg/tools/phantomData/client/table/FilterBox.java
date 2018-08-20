package org.artorg.tools.phantomData.client.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class FilterBox extends ComboBox<Node> {
	private ObservableList<Node> nodes;
	private static final Image imgNormal, imgFilter;
	private final Supplier<List<String>> getters;

	
	static {
		InputStream normalStream = null;
		try {
			normalStream = new FileInputStream(new File("src/main/resources/arrow.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imgNormal = new Image(normalStream);

		InputStream filterStream = null;
		try {
			filterStream = new FileInputStream(new File("src/main/resources/filter.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imgFilter = new Image(filterStream);
	}
	
	public FilterBox(String name, Supplier<List<String>> getters) {
		this.getters = getters;
		
		setPromptText(name);
		
		List<Node> nodes = new ArrayList<Node>();
		
		nodes.add(createCheckBoxAll());

		Separator separator = new Separator(Orientation.HORIZONTAL);
		separator.setPrefHeight(1);
		nodes.add(separator);
		
		getters.get().stream().distinct().forEach(s -> nodes.add(createCheckBoxItem(() -> s)));

		setNodes(nodes);

		this.setStyle("-fx-background-color: transparent;");
	}
	
	
	public List<String> getFilterItemValues() {
		List<String> selectedValues = getCheckBoxItemStream().map(c -> c.nameGetter.get())
				.filter(s -> !s.equals("")).collect(Collectors.toList());
		return selectedValues;
	}

	public List<String> getSelectedValues() {
		List<String> selectedValues = getCheckBoxItemStream().filter(c -> c.isSelected())
				.map(c -> c.nameGetter.get()).filter(s -> !s.equals(""))
				.collect(Collectors.toList());
		return selectedValues;
	}
	
	public void updateNodes() {
		List<String> selectableValues = getters.get().stream().distinct().collect(Collectors.toList());
		nodes.removeAll(getCheckBoxItemStream()
				.filter(c -> {
					String name = c.nameGetter.get();
					Optional<String> name2 =selectableValues.stream().filter(s -> s.equals(name)).findFirst();
					if (!name2.isPresent())
						return true;
					return false;
				}).collect(Collectors.toList()));
		
		nodes.addAll(selectableValues.stream().filter(tableItem -> 
			!getCheckBoxItemStream()
					.filter(boxItem -> boxItem.nameGetter.get().equals(tableItem)).findFirst().isPresent())
				.map(tableItem -> createCheckBoxItem(() -> tableItem)).collect(Collectors.toList()));
	}

	public Stream<CheckBoxItem> getCheckBoxItemStream() {
		return nodes.stream().filter(n -> n instanceof CheckBoxItem).map(n -> ((CheckBoxItem) n));
	}

	private class CheckBoxItem extends CheckBox {
		private Supplier<String> nameGetter;

		private CheckBoxItem(Supplier<String> nameGetter) {
			this.nameGetter = nameGetter;

			this.setText(nameGetter.get());
			this.setSelected(true);
			CheckBoxItem reference = this;

			this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					CheckBox chk = (CheckBox) event.getSource();
					reference.setSelected(chk.isSelected());

					if (!reference.isSelected()) {
						setFilterImage();
						nodes.stream().filter(n -> n instanceof CheckBoxAll)
								.map(n -> ((CheckBoxAll) n)).findFirst()
								.ifPresent(cba -> cba.setSelected(false));
					} else {
						if (!nodes.stream().filter(n -> n instanceof CheckBoxItem)
								.map(n -> ((CheckBoxItem) n)).filter(c -> !c.isSelected())
								.findFirst().isPresent())
							setArrowImage();
					}
				}
			});

		}
	}

	private class CheckBoxAll extends CheckBox {
		private CheckBoxAll() {
			this.setSelected(true);
			this.setText("Select All");

			CheckBoxAll reference = this;

			this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					CheckBox chk = (CheckBox) event.getSource();
					reference.setSelected(chk.isSelected());

					nodes.stream().filter(n -> n instanceof CheckBoxItem)
							.map(n -> ((CheckBoxItem) n))
							.forEach(c -> c.setSelected(reference.isSelected()));

					if (reference.isSelected())
						setArrowImage();
					else
						setFilterImage();
				}
			});

		}
	}

	public CheckBoxItem createCheckBoxItem(Supplier<String> nameGetter) {
		return new CheckBoxItem(nameGetter);
	}

	public CheckBoxAll createCheckBoxAll() {
		return new CheckBoxAll();
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = FXCollections.observableArrayList(nodes);
		setItems(this.nodes);

		Callback<ListView<Node>, ListCell<Node>> cb = new Callback<ListView<Node>, ListCell<Node>>() {
			@Override
			public ListCell<Node> call(ListView<Node> param) {
				ListCell<Node> cell = new ListCell<Node>() {
					@Override
					protected void updateItem(Node item, boolean empty) {
						super.updateItem(item, empty);
						setGraphic(item);
					}
				};
				return cell;
			}
		};

		setButtonCell(buttonCell);
		setCellFactory(cb);
	}

	private ListCell<Node> buttonCell = new ListCell<Node>() {
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
		}
	};

	@SuppressWarnings("restriction")
	@Override
	protected Skin<?> createDefaultSkin() {
		return new ComboBoxListViewSkin<Node>(this){
			@Override
		    protected boolean isHideOnClickEnabled() {
				return false;
		    }
		};
	}

	private void setFilterImage() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				sPane.getChildren().clear();
				ImageView imgView = new ImageView(imgFilter);
				sPane.getChildren().add(imgView);
			}
		});
	}

	private void setArrowImage() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				sPane.getChildren().clear();
				ImageView imgView = new ImageView(imgNormal);
				sPane.getChildren().add(imgView);
			}
		});
	}

	// // Getters & Setters
	public Image getImgFilter() {
		return imgFilter;
	}

	public Image getImgNormal() {
		return imgNormal;
	}

	public List<Node> getListProvider() {
		return nodes;
	}

}
