package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.scene.CssGlyph;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.controlsfx.glyphfont.FontAwesome;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.artorg.tools.phantomData.server.logging.Logger;

public class FilterMenuButton<ITEM, R> extends MenuButton {
	private final Node iconDefault, iconAscending, iconDescending, iconFilter;
	private final ButtonItemReset itemReset;
	private final CheckBoxItemSortAscending itemAscending;
	private final CheckBoxItemSortDescending itemDescending;
	private final TextItemSearch itemSearch;
	private final CheckBoxItemFilterAll itemFilterAll;
	private final List<CheckBoxItemFilter> itemsFilter;
	private final AbstractFilterColumn<ITEM, ?> column;
	private Runnable refresh;
	private final List<FilterMenuButton<ITEM, ?>> parentList;
	private String regex;

	{
		iconDefault = new CssGlyph("FontAwesome", FontAwesome.Glyph.ANGLE_DOWN);
		iconAscending = new CssGlyph("FontAwesome", FontAwesome.Glyph.SORT_ALPHA_ASC);
		iconDescending = new CssGlyph("FontAwesome", FontAwesome.Glyph.SORT_ALPHA_DESC);
		iconFilter = new CssGlyph("FontAwesome", FontAwesome.Glyph.FILTER);
		itemReset = new ButtonItemReset();
		itemAscending = new CheckBoxItemSortAscending();
		itemDescending = new CheckBoxItemSortDescending();
		itemSearch = new TextItemSearch();
		itemFilterAll = new CheckBoxItemFilterAll();
		itemsFilter = new ArrayList<CheckBoxItemFilter>();
		regex = "";
		refresh = () -> {};
		itemSearch.setOnAction(event -> {
			setRegex(itemSearch.getTextField().getText());
			applyFilter();
		});
		Platform.runLater(() -> setImage(iconDefault));
	}

	public Runnable getRefresher() {
		return refresh;
	}

	public FilterMenuButton(AbstractFilterColumn<ITEM, R> column,
			List<FilterMenuButton<ITEM, ?>> parentList) {
		this.column = column;
		this.parentList = parentList;
		itemReset.setOnAction(event -> {
			itemAscending.getCheckBox().setSelected(false);
			itemDescending.getCheckBox().setSelected(false);
			itemSearch.getTextField().setText("");
			itemFilterAll.getCheckBox().setSelected(true);
			streamCheckBoxes().forEach(c -> c.setSelected(true));
			setRegex("");
			if (column != null) column.resetFilter();
			refreshImage();
		});

		itemAscending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (itemAscending.getCheckBox().isSelected())
				itemDescending.getCheckBox().setSelected(false);
			unsortOther();
			refreshImage();
			getRefresher().run();
		});

		itemDescending.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (itemDescending.getCheckBox().isSelected())
				itemAscending.getCheckBox().setSelected(false);
			unsortOther();
			refreshImage();
			getRefresher().run();
		});

		itemSearch.getTextField().textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) setRegex(newValue);
					refreshImage();
					getRefresher().run();
				});

		itemFilterAll.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			streamCheckBoxes()
					.forEach(c -> c.setSelected(itemFilterAll.getCheckBox().isSelected()));
			refreshImage();
			getRefresher().run();
		});

		this.getItems().add(itemReset);
		this.getItems().add(new SeparatorMenuItem());
		this.getItems().add(itemAscending);
		this.getItems().add(itemDescending);
		this.getItems().add(itemSearch);
		this.getItems().add(itemFilterAll);
		this.getItems().add(new SeparatorMenuItem());

		this.addEventHandler(ComboBox.ON_HIDDEN, event -> applyFilter());
		this.addEventHandler(ComboBox.ON_SHOWING, event -> updateNodes());

	}

	public void setRefresh(Runnable refresh) {
		this.refresh = refresh;

		addAction(itemReset, getRefresher());
		addAction(itemAscending, getRefresher());
		addAction(itemDescending, getRefresher());
		addAction(itemSearch, getRefresher());
		addAction(itemFilterAll, getRefresher());
	}

	private void unsortOther() {
		parentList.stream().filter(menuButton -> menuButton != FilterMenuButton.this)
				.forEach(menuButton -> {
					menuButton.itemAscending.getCheckBox().setSelected(false);
					menuButton.itemDescending.getCheckBox().setSelected(false);
					menuButton.refreshImage();
				});
	}

	public boolean isFilterSetted() {
		if (!itemSearch.getTextField().getText().isEmpty()) return true;
		if (!itemFilterAll.getCheckBox().isSelected()) return true;
		return false;
	}

	public AbstractColumn<ITEM, ?> getColumn() {
		return column;
	}

	public void applyFilter() {
		System.out.println("ON HIDING");
		Predicate<ITEM> itemFilter = item -> getSelectedValues().stream()
				.filter(value -> column.get(item).equals(value)).findFirst().isPresent();
		Predicate<ITEM> textFilter;
		if (this.getRegex().isEmpty()) textFilter = item -> true;
		else {
			final Pattern p = Pattern.compile("(?i)" + this.getRegex());
			textFilter = item -> p.matcher(column.get(item).toString()).find();
		}
		Predicate<ITEM> filter = itemFilter.and(textFilter);
		column.setFilterPredicate(filter);

		Comparator<ITEM> comparator = null;
		if (itemAscending.getCheckBox().isSelected())
			comparator = column.getAscendingSortComparator();
		else if (itemDescending.getCheckBox().isSelected()) {
			Comparator<ITEM> ascendingSortComparator = column.getAscendingSortComparator();
			if (ascendingSortComparator != null) comparator = ascendingSortComparator.reversed();
		}

		if (comparator != null) column.setSortComparator(comparator);
		refreshImage();
		getRefresher().run();
	}

	private void addAction(CustomMenuItem menuItem, Runnable rc) {
		EventHandler<ActionEvent> eventHandler = menuItem.getOnAction();
		menuItem.setOnAction(event -> {
			if (eventHandler != null) eventHandler.handle(event);
			rc.run();
		});
	}

	public void setImage(Node node) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				if (sPane != null) {
					if (!sPane.getChildren().contains(node)) {
						sPane.getChildren().clear();
						if (node != null) sPane.getChildren().add(node);
					}
				}
			}
		});
	}

	private static Map<String, HBox> imageBoxMap;

	static {
		imageBoxMap = new HashMap<>();
	}

	public void refreshImage() {
		HBox hBox = new HBox();
		StringBuilder sb = new StringBuilder();
		if (isFilterSetted()) {
			hBox.getChildren().add(iconFilter);
			sb.append("F");
		}
		if (itemAscending.getCheckBox().isSelected()) {
			hBox.getChildren().add(iconAscending);
			sb.append("A");
		} else if (itemDescending.getCheckBox().isSelected()) {
			hBox.getChildren().add(iconDescending);
			sb.append("D");
		}
		if (hBox.getChildren().size() == 0) {
			hBox.getChildren().add(iconDefault);
			sb.append("S");
		}
		String key = sb.toString();
//		if (imageBoxMap.containsKey(key)) hBox = imageBoxMap.get(key);
//		else
			imageBoxMap.put(key, hBox);

		setImage(hBox);
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
				} catch (Exception e) {}
				try {
					Double d1 = Double.valueOf(s1);
					Double d2 = Double.valueOf(s2);
					return d1.compareTo(d2);
				} catch (Exception e) {}
				return s1.compareTo(s2);
			};
		}

		public CheckBoxItemSortAscending() {
			super(new CheckBox("Sort A-Z"));
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
				} catch (Exception e) {}
				try {
					Double d1 = Double.valueOf(s1);
					Double d2 = Double.valueOf(s2);
					return d2.compareTo(d1);
				} catch (Exception e) {}
				return s2.compareTo(s1);
			};
		}

		public CheckBoxItemSortDescending() {
			super(new CheckBox("Sort Z-A"));
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
		return streamCheckBoxes().filter(c -> c.isSelected()).map(c -> c.getText())
				.collect(Collectors.toList());
	}

	public boolean isSortComparatorSet() {
		if (itemAscending.getCheckBox().isSelected()) return true;
		if (itemDescending.getCheckBox().isSelected()) return true;
		return false;
	}

	public void updateNodes() {
		System.out.println("UPDATE NODES");

		List<String> distinctValues = column.getValues().stream().distinct().map(o -> o.toString())
				.sorted().collect(Collectors.toList());

		Logger.debug.println("Updating nodes, distinct values " + distinctValues.size());
		this.getItems().removeAll(itemsFilter);

		CollectionUtil.addIfAbsent(distinctValues, itemsFilter,
				(value, checkBox) -> checkBox.getCheckBox().getText().equals(value.toString()),
				(s, i) -> {
					CheckBoxItemFilter checkBoxItemFilter =
							new CheckBoxItemFilter(s.toString(), true);
					checkBoxItemFilter.getCheckBox().addEventHandler(MouseEvent.MOUSE_CLICKED,
							event -> {
								if (!checkBoxItemFilter.getCheckBox().isSelected())
									itemFilterAll.getCheckBox().setSelected(false);
								else if (!streamCheckBoxes().filter(c -> !c.isSelected())
										.findFirst().isPresent())
									itemFilterAll.getCheckBox().setSelected(true);

								refreshImage();
								getRefresher().run();
							});
					return checkBoxItemFilter;
				});

		CollectionUtil.removeIfAbsent(distinctValues, itemsFilter,
				(value, checkBox) -> checkBox.getCheckBox().getText().equals(value.toString()));

		Comparator<? super CheckBoxItemFilter> comparator =
				(c1, c2) -> c1.getCheckBox().getText().compareTo(c2.getCheckBox().getText());
		itemsFilter.sort(comparator);

		List<String> distinctFilteredValues = column.getFilteredValues().stream().distinct()
				.map(o -> o.toString()).sorted().collect(Collectors.toList());

		if (distinctValues.size() < distinctFilteredValues.size())
			throw new ArrayIndexOutOfBoundsException();

		for (int i = 0; i < itemsFilter.size(); i++) {
			if (distinctFilteredValues.contains(itemsFilter.get(i).getCheckBox().getText()))
				itemsFilter.get(i).setDisable(false);
			else
				itemsFilter.get(i).setDisable(true);
		}

		if (!itemsFilter.isEmpty()) {
			if (!(this.getItems().get(this.getItems().size() - 1) instanceof SeparatorMenuItem))
				this.getItems().add(new SeparatorMenuItem());
		} else if ((this.getItems().get(this.getItems().size() - 1) instanceof SeparatorMenuItem))
			this.getItems().remove(this.getItems().size() - 1);

		this.getItems().addAll(itemsFilter);
	}

	public List<FilterMenuButton<ITEM, ?>> getParentList() {
		return parentList;
	}

}