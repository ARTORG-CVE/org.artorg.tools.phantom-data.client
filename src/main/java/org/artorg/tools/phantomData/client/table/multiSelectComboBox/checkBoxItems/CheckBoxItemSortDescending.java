package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemSort;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemSortDescending extends CheckBoxItemSort {
	
	public CheckBoxItemSortDescending() {
		this.setText("Sort Descending (z-a)");
		this.setSortComparator((s1,s2) -> {
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
		});
		
		CheckBoxItemSort reference = this;
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				reference.setSelected(chk.isSelected());

				if (reference.isSelected())
					CheckBoxItemSortAscending.stream(reference.getComboBoxParent())
					.forEach(c -> c.setSelected(!reference.isSelected()));
			}
		});

	}

	public static Stream<CheckBoxItemSortDescending> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream()
				.filter(n -> n instanceof CheckBoxItemSortDescending)
				.map(n -> ((CheckBoxItemSortDescending) n));
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
