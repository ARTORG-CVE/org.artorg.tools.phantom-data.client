package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.CheckBoxItemFilterParent;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemFilter extends CheckBoxItemFilterParent {
	private Supplier<String> nameGetter;
	
	public CheckBoxItemFilter(Supplier<String> nameGetter) {
		this.setNameGetter(nameGetter);
		this.setText(nameGetter.get());
		this.setSelected(true);
		CheckBoxItemFilter reference = this;

		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				reference.setSelected(chk.isSelected());
				IMultiSelectComboBox parent = reference.getComboBoxParent();
				if (!reference.isSelected()) {
					parent.setImage(getImgfilter());
					CheckBoxItemFilterAll.stream(parent).findFirst()
						.ifPresent(cba -> cba.setSelected(false));
				} else {
					if (!CheckBoxItemFilterAll.stream(parent).findFirst().isPresent())
						parent.setImage(getImgnormal());
				}
			}
		});

	}
	
	public static Stream<CheckBoxItemFilter> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream().filter(n -> n instanceof CheckBoxItemFilter)
				.map(n -> ((CheckBoxItemFilter) n));
	}
	
	public Supplier<String> getNameGetter() {
		return nameGetter;
	}

	public void setNameGetter(Supplier<String> nameGetter) {
		this.nameGetter = nameGetter;
	}
	
	
	

}