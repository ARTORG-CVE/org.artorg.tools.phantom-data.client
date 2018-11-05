package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.util.IOutil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class FilterMenuButton<ITEM> extends MenuButton {
	private static final Image imgNormal, imgFilter;
	private final ButtonItemReset itemReset;
	private final CheckBoxItemSortAscending itemAscending;
	private final CheckBoxItemSortDescending itemDescending;
	private final TextItemSearch itemSearch;
	private final CheckBoxItemFilterAll itemFilterAll;
	private final List<CheckBoxItemFilter> itemsFilter;
	private String regex;
	private AbstractFilterColumn<ITEM> column;
	private Runnable refresh;

	static {
		imgNormal = IOutil.readResourceAsImage("img/arrow.png");
		imgFilter = IOutil.readResourceAsImage("img/filter.png");
	}

	{
		itemReset = new ButtonItemReset();
		itemAscending = new CheckBoxItemSortAscending();
		itemDescending = new CheckBoxItemSortDescending();
		itemSearch = new TextItemSearch();
		itemFilterAll = new CheckBoxItemFilterAll();
		itemsFilter = new ArrayList<CheckBoxItemFilter>();
		regex = "";
		refresh = () -> {};
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
				
				if (column != null) {
					column.resetFilter();
				}
			}
		});

		itemAscending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (itemAscending.getCheckBox().isSelected())
					itemDescending.getCheckBox().setSelected(false);
			}
		});

		itemDescending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (itemDescending.getCheckBox().isSelected())
					itemAscending.getCheckBox().setSelected(false);
			}
		});

		itemSearch.getTextField().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					setRegex(newValue);
					setImage(imgFilter);
				} else
					refreshImage();
			}
		});

		itemFilterAll.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				streamCheckBoxes().forEach(c -> c.setSelected(itemFilterAll.getCheckBox().isSelected()));

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
	
	public AbstractColumn<?> getColumn() {
		return column;
	}

	public void setColumn(AbstractFilterColumn<ITEM> column, Runnable refresh) {
		this.column = column;		
		this.refresh = refresh;
		this.addEventHandler(ComboBox.ON_HIDDEN, event -> {
			Predicate<ITEM> itemFilter = item -> getSelectedValues().stream()
					.filter(value -> column.get(item).equals(value)).findFirst().isPresent();
			Predicate<ITEM> textFilter;
			if (this.getRegex().isEmpty())
				textFilter = item -> true;
			else {
				final Pattern p = Pattern.compile("(?i)" +this.getRegex());
				textFilter = item -> p.matcher(column.get(item)).find();
			}
			Predicate<ITEM> filter = itemFilter.and(textFilter);
			column.setFilterPredicate(filter);
			
			Comparator<ITEM> comparator = null;
			if (itemAscending.getCheckBox().isSelected())
				comparator = column.getAscendingSortComparator();
			else if (itemDescending.getCheckBox().isSelected()) {
				Comparator<ITEM> ascendingSortComparator = column.getAscendingSortComparator(); 
				if (ascendingSortComparator != null)
					comparator = ascendingSortComparator.reversed();
			}
			
			if (comparator != null)
				column.setSortComparator(comparator);
			refresh.run();
		});
		this.addEventHandler(ComboBox.ON_SHOWING, event -> {
			this.updateNodes();
			itemAscending.getCheckBox().setSelected(false);
			itemDescending.getCheckBox().setSelected(false);
		});
		
		addAction(itemReset, refresh);
		addAction(itemAscending, refresh);
		addAction(itemDescending, refresh);
		addAction(itemSearch, refresh);
		addAction(itemFilterAll, refresh);
	}
	
	private void addAction(CustomMenuItem menuItem, Runnable rc) {
		EventHandler<ActionEvent> eventHandler = menuItem.getOnAction();
		menuItem.setOnAction(event -> {
			if (eventHandler != null)
				eventHandler.handle(event);
			rc.run();
		});
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
		if (!itemFilterAll.getCheckBox().isSelected())
			return;
		if (streamCheckBoxes().filter(c -> !c.isSelected()).findFirst().isPresent())
			return;
		setImage(imgNormal);
	}

	private Stream<CheckBox> streamCheckBoxes() {
		return itemsFilter.stream().map(i -> i.getCheckBox());
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
		private final Comparator<String> comparator;

		{
			comparator = (s1, s2) -> {
				try {
					Long l1 = Long.valueOf(s1);
					Long l2 = Long.valueOf(s2);
					return l1.compareTo(l2);
				} catch (Exception e) {
				}
				try {
					Double d1 = Double.valueOf(s1);
					Double d2 = Double.valueOf(s2);
					return d1.compareTo(d2);
				} catch (Exception e) {
				}
				return s1.compareTo(s2);
			};
		}

		public CheckBoxItemSortAscending() {
			super(new CheckBox("Sort Ascending"));
			checkBox = (CheckBox) this.getContent();
			this.setHideOnClick(false);
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public Comparator<String> getComparator() {
			return comparator;
		}
	}

	public class CheckBoxItemSortDescending extends CustomMenuItem {
		private final CheckBox checkBox;
		private final Comparator<String> comparator;

		{
			comparator = (s1, s2) -> {
				try {
					Long l1 = Long.valueOf(s1);
					Long l2 = Long.valueOf(s2);
					return l2.compareTo(l1);
				} catch (Exception e) {
				}
				try {
					Double d1 = Double.valueOf(s1);
					Double d2 = Double.valueOf(s2);
					return d2.compareTo(d1);
				} catch (Exception e) {
				}
				return s2.compareTo(s1);
			};
		}

		public CheckBoxItemSortDescending() {
			super(new CheckBox("Sort Descending"));
			checkBox = (CheckBox) this.getContent();
			this.setHideOnClick(false);
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public Comparator<String> getComparator() {
			return comparator;
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
			this(name, true);
		}
		
		public CheckBoxItemFilter(String name, boolean selected) {
			super(new CheckBox(name));
			checkBox = (CheckBox) this.getContent();
			checkBox.setSelected(selected);
			this.setHideOnClick(false);
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}
	}

	public String getRegex() {
		return regex;
	}

	private void setRegex(String regex) {
		this.regex = regex;
	}

	public List<String> getSelectedValues() {
		return streamCheckBoxes().filter(c -> c.isSelected()).map(c -> c.getText()).collect(Collectors.toList());
	}

	public boolean isSortComparatorSet() {
		if (itemAscending.getCheckBox().isSelected())
			return true;
		if (itemDescending.getCheckBox().isSelected())
			return true;
		return false;
	}

	public void updateNodes() {
		this.getItems().removeAll(itemsFilter);
		itemsFilter.clear();
		
		List<String> filteredValues = column.getFilteredValues();
		itemsFilter.addAll(
				column.getValues().stream().distinct().map(g -> {
					boolean isFiltered = filteredValues.contains(g);
					return new CheckBoxItemFilter(g, isFiltered);
				}).collect(Collectors.toList()));
		
		if (!itemsFilter.isEmpty()) {
			if (!(this.getItems().get(this.getItems().size() - 1) instanceof SeparatorMenuItem))
				this.getItems().add(new SeparatorMenuItem());
		} else if ((this.getItems().get(this.getItems().size() - 1) instanceof SeparatorMenuItem))
			this.getItems().remove(this.getItems().size() - 1);

		this.getItems().addAll(itemsFilter);
		
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
				refresh.run();
			}
		}));
	}

}