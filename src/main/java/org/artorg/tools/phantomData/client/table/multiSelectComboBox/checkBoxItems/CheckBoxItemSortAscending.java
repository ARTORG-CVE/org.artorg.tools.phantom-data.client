package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemSortAscending extends CheckBoxItemSort {
	public CheckBoxItemSortAscending() {
		this.setText("Sort Ascending (a-z)");
		this.setSortComparator((s1,s2) -> {
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
		});
		
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
	
	public static Stream<CheckBoxItemSortAscending> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream()
				.filter(n -> n instanceof CheckBoxItemSortAscending)
				.map(n -> ((CheckBoxItemSortAscending) n));
	}

	@Override
	public void reset() {
		this.setSelected(false);
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public boolean isDefault() {
		return !this.isSelected();
	}
	
}
