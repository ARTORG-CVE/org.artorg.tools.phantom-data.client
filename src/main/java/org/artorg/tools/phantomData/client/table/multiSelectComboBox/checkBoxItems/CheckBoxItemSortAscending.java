package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;

public class CheckBoxItemSortAscending extends CheckBoxItemSort {

	public CheckBoxItemSortAscending() {
		this.setText("Sort Ascending");
		this.setSortComparator((s1,s2) -> s1.compareTo(s2));

	}
	
	public static Stream<CheckBoxItemSortAscending> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream()
				.filter(n -> n instanceof CheckBoxItemSortAscending)
				.map(n -> ((CheckBoxItemSortAscending) n));
	}
	
}
