package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem;

import java.util.Comparator;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;

public abstract class CheckBoxItemSort extends CheckBoxItem {
	protected Comparator<String> sortComparator;

	{
		this.sortComparator = (s1,s2) -> s1.compareTo(s2);
	}
	
	public CheckBoxItemSort() {
		this.setSelected(false);
	}
	
	public static Stream<CheckBoxItemSort> streamParent(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream()
				.filter(n -> n instanceof CheckBoxItemSort)
				.map(n -> ((CheckBoxItemSort) n));
	}

	public Comparator<String> getSortComparator() {
		return sortComparator;
	}

	public void setSortComparator(Comparator<String> sortComparatorAscending) {
		this.sortComparator = sortComparatorAscending;
	}
	
	
}
