package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem;

import java.util.Comparator;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems.CheckBoxItemSortDescending;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public abstract class CheckBoxItemSort extends CheckBoxItem {
	protected Comparator<String> sortComparator;

	{
		this.sortComparator = (s1,s2) -> s1.compareTo(s2);
	}
	
	public CheckBoxItemSort() {
		this.setSelected(false);
		this.setText("Sort Ascending");

		CheckBoxItemSort reference = this;
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				reference.setSelected(chk.isSelected());

				if (reference.isSelected())
					CheckBoxItemSortDescending.stream(reference.getComboBoxParent())
					.forEach(c -> c.setSelected(!reference.isSelected()));
			}
		});

	}
	
	public static Stream<CheckBoxItemSort> streamParent(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream()
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
