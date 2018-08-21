package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.MultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItem;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemFilter;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemFilterAll;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSortAscending;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSortDescending;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;

public class SortFilterBox extends MultiSelectComboBox {
	private Supplier<List<String>> getters;
	
	public void setGetters(Supplier<List<String>> getters) {
		this.getters = getters;
		List<Node> nodes = new ArrayList<Node>();
		
		
		addNodeHelper(nodes, new CheckBoxItemSortAscending());
		addNodeHelper(nodes, new CheckBoxItemSortDescending());
		nodes.add(new Separator(Orientation.HORIZONTAL));
		addNodeHelper(nodes, new CheckBoxItemFilterAll());
		nodes.add(new Separator(Orientation.HORIZONTAL));
		getters.get().stream().distinct().forEach(s -> {
			addNodeHelper(nodes, new CheckBoxItemFilter(() -> s));
		});
		
		setNodes(nodes);
		
		this.setStyle("-fx-background-color: transparent;");
		
	}
	
	private void addNodeHelper(List<Node> nodes, CheckBoxItem box) {
		box.setComboBoxParent(this);
		nodes.add(box);
	}
	
	public List<String> getFilterItemValues() {
		List<String> selectedValues = CheckBoxItemFilter.stream(this).map(c -> c.getNameGetter().get())
				.filter(s -> !s.equals("")).collect(Collectors.toList());
		return selectedValues;
	}

	public List<String> getSelectedValues() {
		List<String> selectedValues = CheckBoxItemFilter.stream(this).filter(c -> c.isSelected())
				.map(c -> c.getNameGetter().get()).filter(s -> !s.equals(""))
				.collect(Collectors.toList());
		return selectedValues;
	}
	
	public void updateNodes() {
		List<String> selectableValues = getters.get().stream().distinct().collect(Collectors.toList());
		getNodes().removeAll(CheckBoxItemFilter.stream(this)
				.filter(c -> {
					String name = c.getNameGetter().get();
					Optional<String> name2 =selectableValues.stream().filter(s -> s.equals(name)).findFirst();
					if (!name2.isPresent())
						return true;
					return false;
				}).collect(Collectors.toList()));
		
		getNodes().addAll(selectableValues.stream().filter(tableItem -> 
			!CheckBoxItemFilter.stream(this)
					.filter(boxItem -> boxItem.getNameGetter().get().equals(tableItem)).findFirst().isPresent())
				.map(tableItem -> new CheckBoxItemFilter(() -> tableItem)).collect(Collectors.toList()));
	}
	
	public Comparator<String> getAndClearSortComparator() {
		return CheckBoxItemSort.streamParent(this).filter(c -> c.isSelected())
				.peek(c -> c.setSelected(false))
				.findFirst().get().getSortComparator();
	}
	
	public boolean isSortComparatorSet() {
		return CheckBoxItemSort.streamParent(this).filter(c -> c.isSelected())
				.findFirst().isPresent();
	}
	
	public void setComparatorAscending(Comparator<String> comparator) {
		CheckBoxItemSortAscending.stream(this).findFirst().get()
				.setSortComparator(comparator);
		CheckBoxItemSortDescending.stream(this).findFirst().get()
		.setSortComparator(comparator.reversed());
	}

}
