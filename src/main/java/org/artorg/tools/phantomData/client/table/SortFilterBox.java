package org.artorg.tools.phantomData.client.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.MultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.ButtonReset;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemFilter;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemFilterAll;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSeparator;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSortAscending;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSortDescending;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.TextItemSearch;

import javafx.scene.image.Image;

public class SortFilterBox {
	private Supplier<List<String>> getters;
	private MultiSelectComboBox comboBox;
	private List<Item> boxItems;
	
	private static final Image imgNormal, imgFilter;
	
	{
		this.comboBox = new MultiSelectComboBox();
	}
	
	
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
	
	public static Image getImgnormal() {
		return imgNormal;
	}

	public static Image getImgfilter() {
		return imgFilter;
	}
	
	
	public void setGetters(Supplier<List<String>> getters) {
		this.getters = getters;
		boxItems = new ArrayList<Item>();
		
		addNodeHelper(boxItems, new ButtonReset(this::imgRefresher));
		addNodeHelper(boxItems, new TextItemSearch(imgFilter,this::imgRefresher));
		addNodeHelper(boxItems, new CheckBoxItemSortAscending());
		addNodeHelper(boxItems, new CheckBoxItemSortDescending());
		addNodeHelper(boxItems, new CheckBoxItemSeparator());
		addNodeHelper(boxItems, new CheckBoxItemFilterAll(imgFilter, this::imgRefresher));
		addNodeHelper(boxItems, new CheckBoxItemSeparator());
		getters.get().stream().distinct().forEach(s -> {
			addNodeHelper(boxItems, new CheckBoxItemFilter(() -> s, imgFilter, this::imgRefresher));
		});
		
		comboBox.setBoxItems(boxItems);
		
		comboBox.setStyle("-fx-background-color: transparent;");
		
	}
	
	public MultiSelectComboBox getComboBox() {
		return comboBox;
	}
	
	public void refresh() {
		String name = comboBox.getPromptText();
		this.comboBox = new MultiSelectComboBox();
		this.comboBox.setPromptText(name);
		comboBox.setBoxItems(boxItems);
		
		comboBox.setStyle("-fx-background-color: transparent;");

	}
	
	private void imgRefresher() {
		Optional<Item> notDefaultItem = comboBox.getBoxItemStream().filter(i -> !i.isDefault()).findFirst();
		if (!notDefaultItem.isPresent())
			comboBox.setImage(imgNormal);
	}
	
	private void addNodeHelper(List<Item> nodes, Item box) {
		box.setComboBoxParent(comboBox);
		nodes.add(box);
	}
	
	public List<String> getFilterItemValues() {
		List<String> selectedValues = CheckBoxItemFilter.stream(comboBox).map(c -> c.getNameGetter().get())
				.filter(s -> !s.equals("")).collect(Collectors.toList());
		return selectedValues;
	}

	public List<String> getSelectedValues() {
		List<String> selectedValues = CheckBoxItemFilter.stream(comboBox).filter(c -> c.isSelected())
				.map(c -> c.getNameGetter().get()).filter(s -> !s.equals(""))
				.collect(Collectors.toList());
		return selectedValues;
	}
	
	public void updateNodes() {
		List<String> selectableValues = getters.get().stream().distinct().collect(Collectors.toList());
		comboBox.getNodes().removeAll(CheckBoxItemFilter.stream(comboBox)
				.filter(c -> {
					String name = c.getNameGetter().get();
					Optional<String> name2 =selectableValues.stream().filter(s -> s.equals(name)).findFirst();
					if (!name2.isPresent())
						return true;
					return false;
				}).collect(Collectors.toList()));
		
		comboBox.getNodes().addAll(selectableValues.stream().filter(tableItem -> 
			!CheckBoxItemFilter.stream(comboBox)
					.filter(boxItem -> boxItem.getNameGetter().get().equals(tableItem)).findFirst().isPresent())
				.map(tableItem -> new CheckBoxItemFilter(() -> tableItem, imgFilter, this::imgRefresher)).collect(Collectors.toList()));
	}
	
	public Comparator<String> getAndClearSortComparator() {
		return CheckBoxItemSort.streamParent(comboBox).filter(c -> c.isSelected())
				.peek(c -> c.setSelected(false))
				.findFirst().get().getSortComparator();
	}
	
	public boolean isSortComparatorSet() {
		return CheckBoxItemSort.streamParent(comboBox).filter(c -> c.isSelected())
				.findFirst().isPresent();
	}
	
	public void setComparatorAscending(Comparator<String> comparator) {
		CheckBoxItemSortAscending.stream(comboBox).findFirst().get()
				.setSortComparator(comparator);
		CheckBoxItemSortDescending.stream(comboBox).findFirst().get()
		.setSortComparator(comparator.reversed());
	}
	
	public String getSearchString() {
		return TextItemSearch.stream(comboBox).findFirst().get().getText();
	}

}
