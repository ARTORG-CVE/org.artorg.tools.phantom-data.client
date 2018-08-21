package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemFilterParent;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemFilterAll extends CheckBoxItemFilterParent {
	
	public CheckBoxItemFilterAll() {
		this.setSelected(true);
		this.setText("Select All");

		CheckBoxItemFilterAll reference = this;
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				reference.setSelected(chk.isSelected());

				CheckBoxItemFilter.stream(reference.getComboBoxParent())
				.forEach(c -> c.setSelected(reference.isSelected()));

				if (reference.isSelected())
					reference.getComboBoxParent().setImage(getImgnormal());
				else
					reference.getComboBoxParent().setImage(getImgfilter());
			}
		});

	}
	
	public static Stream<CheckBoxItemFilterAll> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream().filter(n -> n instanceof CheckBoxItemFilterAll)
				.map(n -> ((CheckBoxItemFilterAll) n));
	}

}
