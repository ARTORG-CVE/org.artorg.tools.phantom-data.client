package org.artorg.tools.phantomData.client.scene.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.scene.CssGlyph;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.controlsfx.glyphfont.FontAwesome;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
import javafx.scene.layout.StackPane;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;

public class FilterMenuButton<ITEM, R> extends MenuButton {
	private final Supplier<Node> iconDefault, iconAscending, iconDescending, iconFilter;
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
		iconDefault = () -> new CssGlyph("FontAwesome", FontAwesome.Glyph.ANGLE_DOWN);
		iconAscending = () -> new CssGlyph("FontAwesome", FontAwesome.Glyph.SORT_ALPHA_ASC);
		iconDescending = () -> new CssGlyph("FontAwesome", FontAwesome.Glyph.SORT_ALPHA_DESC);
		iconFilter = () -> new CssGlyph("FontAwesome", FontAwesome.Glyph.FILTER);
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
//		Platform.runLater(() -> setImage(iconDefault.get()));
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
//			if (column != null) column.resetFilter();
			refreshImage();
			getRefresher().run();
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

	private void addAction(CustomMenuItem menuItem, Runnable rc) {
		EventHandler<ActionEvent> eventHandler = menuItem.getOnAction();
		menuItem.setOnAction(event -> {
			if (eventHandler != null) eventHandler.handle(event);
			rc.run();
		});
	}

	private void unsortOther() {
		getParentList().stream().filter(menuButton -> menuButton != FilterMenuButton.this)
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

	public AbstractFilterColumn<ITEM, ?> getColumn() {
		return column;
	}

	public void applyFilter() {
		Predicate<ITEM> itemFilter;
		if (itemFilterAll.getCheckBox().isSelected()) itemFilter = item -> true;
		else {
			itemFilter = item -> getSelectedValues().stream().filter(value -> {
				Object itemValue = column.get(item);
				return (itemValue == null || itemValue.equals("") || itemValue.equals(value));
			}).findFirst().isPresent();
		}
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

	public void setImage(Node node) {
		StackPane sPane = (StackPane) lookup(".arrow-button");
		if (sPane != null) {
			if (!sPane.getChildren().contains(node)) {
				sPane.getChildren().clear();
				if (node != null) sPane.getChildren().add(node);
			}
		}
	}

	public void refreshImage() {
		HBox hBox = new HBox();
		if (isFilterSetted()) {
			hBox.getChildren().add(iconFilter.get());
		}
		if (itemAscending.getCheckBox().isSelected()) {
			hBox.getChildren().add(iconAscending.get());
		} else if (itemDescending.getCheckBox().isSelected()) {
			hBox.getChildren().add(iconDescending.get());
		}
		if (hBox.getChildren().size() == 0) {
			hBox.getChildren().add(iconDefault.get());
		}
		hBox.setPrefWidth(10.0);
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
		if (!getColumn().isItemsFilter()) {
			super.getItems().removeAll(itemsFilter);
			itemsFilter.clear();
			MenuItem item = getItems().get(getItems().size() - 1);
			if (item instanceof SeparatorMenuItem) getItems().remove(item);
//			super.getItems().remove(itemFilterAll);
			
			Logger.debug.println("updateNodes remove1");
			return;
		}

		List<?> allValues = column.getValues();
		List<?> values = allValues.stream()
				.filter(value -> value != null && !(value.equals(""))).collect(Collectors.toList());
		List<String> distinctValues = values.stream().distinct().map(o -> o.toString()).sorted()
				.collect(Collectors.toList());

		if (distinctValues.size() > getColumn().getMaxFilterItems()) {
			super.getItems().removeAll(itemsFilter);
			itemsFilter.clear();
			MenuItem item = getItems().get(getItems().size() - 1);
			if (item instanceof SeparatorMenuItem) getItems().remove(item);
//			super.getItems().remove(itemFilterAll);
			Logger.debug.println("updateNodes remove2");
			return;
		}

		List<?> filteredValues = column.getFilteredValues().stream()
				.filter(value -> value != null && !(value.equals(""))).collect(Collectors.toList());
		List<String> distinctFilteredValues = filteredValues.stream().distinct()
				.map(o -> o.toString()).sorted().collect(Collectors.toList());

		List<Integer> addableIndexes = CollectionUtil.searchLeftNotInRight(distinctValues,
				itemsFilter,
				(value, checkBox) -> checkBox.getCheckBox().getText().equals(value.toString()));
		List<String> addableFilterItemNames =
				CollectionUtil.subList(distinctValues, addableIndexes);
		List<CheckBoxItemFilter> checkBoxItemsFilter = addableFilterItemNames.stream()
				.map(s -> createCheckBoxItemFilter(s)).collect(Collectors.toList());

		List<Integer> removableIndexes = CollectionUtil.searchRightNotInLeft(distinctValues,
				itemsFilter,
				(value, checkBox) -> checkBox.getCheckBox().getText().equals(value.toString()));
		List<CheckBoxItemFilter> nonMatchingItems =
				CollectionUtil.subList(itemsFilter, removableIndexes);
		List<String> removableFilterItemNames =
				nonMatchingItems.stream().map(c -> c.getText()).collect(Collectors.toList());

		if (checkBoxItemsFilter.isEmpty() && nonMatchingItems.isEmpty()) return;

		super.getItems().removeAll(itemsFilter);
		MenuItem item = getItems().get(getItems().size() - 1);
		if (item instanceof SeparatorMenuItem) getItems().remove(item);
//		super.getItems().remove(itemFilterAll);
		
		itemsFilter.addAll(checkBoxItemsFilter);
		itemsFilter.removeAll(nonMatchingItems);

		itemsFilter
				.sort((c1, c2) -> c1.getCheckBox().getText().compareTo(c2.getCheckBox().getText()));

		for (int i = 0; i < itemsFilter.size(); i++) {
			if (distinctFilteredValues.contains(itemsFilter.get(i).getCheckBox().getText()))
				itemsFilter.get(i).setDisable(false);
			else
				itemsFilter.get(i).setDisable(true);
		}
		
		if (!itemsFilter.isEmpty()) {
//			getItems().add(itemFilterAll);
			getItems().add(new SeparatorMenuItem());
			getItems().addAll(itemsFilter);
		}
		
		String addedNames =
				addableFilterItemNames.stream().collect(Collectors.joining(", ", "{", "}"));
		String removedNames =
				removableFilterItemNames.stream().collect(Collectors.joining(", ", "{", "}"));
		Logger.debug
				.println(String.format("%s - %s - added filter items: %s, removed filter items %s",
						getColumn().getItemClass().getSimpleName(), getColumn().getName(),
						addedNames, removedNames));
	}

	private CheckBoxItemFilter createCheckBoxItemFilter(String name) {
		CheckBoxItemFilter checkBoxItemFilter = new CheckBoxItemFilter(name, true);
		checkBoxItemFilter.getCheckBox().selectedProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (!newValue) itemFilterAll.getCheckBox().setSelected(false);
					else if (!streamCheckBoxes().filter(c -> !c.isSelected()).findFirst()
							.isPresent())
						itemFilterAll.getCheckBox().setSelected(true);
					refreshImage();
					getRefresher().run();
				});
		return checkBoxItemFilter;
	}

	public List<FilterMenuButton<ITEM, ?>> getParentList() {
		return parentList;
	}

	public String getRegex() {
		return regex;
	}

	private void setRegex(String regex) {
		this.regex = regex;
	}

}