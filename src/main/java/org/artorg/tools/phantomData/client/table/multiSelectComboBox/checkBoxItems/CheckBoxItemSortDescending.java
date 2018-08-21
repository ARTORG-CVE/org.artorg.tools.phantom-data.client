package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;

public class CheckBoxItemSortDescending extends CheckBoxItemSort {
	
	public CheckBoxItemSortDescending() {
		this.setText("Sort Descending");

		this.setSortComparator((s1,s2) -> s2.compareTo(s1));

	}

	public static Stream<CheckBoxItemSortDescending> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream()
				.filter(n -> n instanceof CheckBoxItemSortDescending)
				.map(n -> ((CheckBoxItemSortDescending) n));
	}
	
}
