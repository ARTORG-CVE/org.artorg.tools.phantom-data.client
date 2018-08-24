package org.artorg.tools.phantomData.client.table.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class FilterMenuButton extends MenuButton {
	private static final Image imgNormal, imgFilter;
	private final ButtonItemReset itemReset;
	private final CheckBoxItemSortAscending itemAscending;
	private final CheckBoxItemSortDescending itemDescending;
	private final TextItemSearch itemSearch;
	private final CheckBoxItemFilterAll itemFilterAll;
	private Supplier<List<String>> getters;
	private String regex;
	
	
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
	
	{
		itemReset = new ButtonItemReset();
		itemAscending = new CheckBoxItemSortAscending();
		itemDescending = new CheckBoxItemSortDescending();
		itemSearch = new TextItemSearch();
		itemFilterAll = new CheckBoxItemFilterAll();
	}
	
	public FilterMenuButton() {
		
		
		itemReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				itemAscending.getCheckBox().setSelected(false);
				itemDescending.getCheckBox().setSelected(false);
				itemSearch.getTextField().setText("");
				itemFilterAll.getCheckBox().setSelected(true);
				streamCheckBoxes().forEach(c -> c.setSelected(true));
				setImage(imgNormal);
			}
		});
		
		itemAscending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (itemAscending.getCheckBox().isSelected())
					itemDescending.getCheckBox().setSelected(false);
			}
		});
		
		itemDescending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (itemDescending.getCheckBox().isSelected())
					itemAscending.getCheckBox().setSelected(false);
			}
		});
		
		itemSearch.getTextField().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (!newValue.isEmpty()) {
					regex = newValue;
					setImage(imgFilter);
				}
				else 
					refreshImage();
			}
		});
		
		itemFilterAll.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				streamCheckBoxes().forEach(c -> 
					c.setSelected(itemFilterAll.getCheckBox().isSelected()));

				if (!itemFilterAll.getCheckBox().isSelected())
					setImage(imgFilter);
				else
					refreshImage();
			}
		});
		
		
		this.getItems().add(itemReset);
		this.getItems().add(new SeparatorMenuItem());
		this.getItems().add(itemAscending);
		this.getItems().add(itemDescending);
		this.getItems().add(itemSearch);	
		this.getItems().add(itemFilterAll);
		this.getItems().add(new SeparatorMenuItem());
		
		
		
	}
	
	public void setImage(Image image) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				sPane.getChildren().clear();
				ImageView imgView = new ImageView(image);
				sPane.getChildren().add(imgView);
			}
		});
	}
	
	public void refreshImage() {
		if (!itemFilterAll.getCheckBox().isSelected()) return;
		if (streamCheckBoxes().filter(c -> !c.isSelected()).findFirst().isPresent()) return;
		setImage(imgNormal);
	}
	
	
	
	private Stream<CheckBox> streamCheckBoxes() {
		return this.getItems().stream().filter(m -> m instanceof CheckBoxItemFilter)
			.map(c -> ((CheckBoxItemFilter)c).getCheckBox());
	}
	
	public void setGetters(Supplier<List<String>> getters) {
		this.getters = getters;
		
		List<CheckBoxItemFilter> list = getters.get().stream().distinct().map(s ->
			new CheckBoxItemFilter(s)).collect(Collectors.toList());
		
		if (!list.isEmpty()) {
			if (!(this.getItems().get(this.getItems().size()-1) instanceof SeparatorMenuItem))
				this.getItems().add(new SeparatorMenuItem());
		} else if ((this.getItems().get(this.getItems().size()-1) instanceof SeparatorMenuItem)) 
			this.getItems().remove(this.getItems().size()-1);
		
		this.getItems().addAll(list);
		
		streamCheckBoxes().forEach(c -> c.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { 
				if (!c.isSelected()) {
					setImage(imgFilter);
					itemFilterAll.getCheckBox().setSelected(false);
				} else {
					if (!streamCheckBoxes().filter(c -> !c.isSelected()).findFirst().isPresent()) {
						itemFilterAll.getCheckBox().setSelected(true);
						refreshImage();
					}
				}
			}
		}));
		
		
	}
	
	public class ButtonItemReset extends CustomMenuItem {
		
		public ButtonItemReset() {
			super(new Label("Reset Filter"));
			this.setHideOnClick(false);
		}
	}
	
	
	public class TextItemSearch extends CustomMenuItem {
		private final TextField textField;
		
		public TextItemSearch() {
			super(new TextField());
			textField = (TextField) this.getContent();
			this.setHideOnClick(false);
		}

		public TextField getTextField() {
			return textField;
		}
	}
	
	
	public class CheckBoxItemSortAscending extends CustomMenuItem {
		private final CheckBox checkBox;
		
		public CheckBoxItemSortAscending() {
			super(new CheckBox("Sort Ascending"));
			checkBox = (CheckBox) this.getContent();
			this.setHideOnClick(false);
		}
		
		public CheckBox getCheckBox() {
			return checkBox;
		}
	}
	
	public class CheckBoxItemSortDescending extends CustomMenuItem {
		private final CheckBox checkBox;
		
		public CheckBoxItemSortDescending() {
			super(new CheckBox("Sort Descending"));
			checkBox = (CheckBox) this.getContent();
			this.setHideOnClick(false);
		}
		
		public CheckBox getCheckBox() {
			return checkBox;
		}
	}
	
	public class CheckBoxItemFilterAll extends CustomMenuItem {
		private final CheckBox checkBox;
		
		public CheckBoxItemFilterAll() {
			super(new CheckBox("Select All"));
			checkBox = (CheckBox) this.getContent();
			checkBox.setSelected(true);
			this.setHideOnClick(false);
		}
		
		public CheckBox getCheckBox() {
			return checkBox;
		}
	}
	
	public class CheckBoxItemFilter extends CustomMenuItem {
		private final CheckBox checkBox;
		
		public CheckBoxItemFilter(String name) {
			super(new CheckBox(name));
			checkBox = (CheckBox) this.getContent();
			checkBox.setSelected(true);
			this.setHideOnClick(false);
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}
	}
	
	
	
	

}
