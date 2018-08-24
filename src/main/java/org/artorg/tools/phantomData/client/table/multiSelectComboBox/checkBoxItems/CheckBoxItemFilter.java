package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemFilter extends CheckBox implements Item {
	private Supplier<String> nameGetter;
	private IMultiSelectComboBox parent;
	
	public CheckBoxItemFilter(Supplier<String> nameGetter, Image imgFilter, Runnable imgRefresher) {
		this.setNameGetter(nameGetter);
		this.setText(nameGetter.get());
		this.setSelected(true);
		CheckBoxItemFilter reference = this;

		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				reference.setSelected(chk.isSelected());
				Optional<CheckBoxItemFilterAll> cba = CheckBoxItemFilterAll.stream(parent).findFirst(); 
				if (!reference.isSelected()) {
					parent.setImage(imgFilter);
					if (cba.isPresent())
						cba.get().setSelected(false);
				} else {
					Optional<CheckBoxItemFilter> notSelectedItem = CheckBoxItemFilter.stream(parent).filter(c -> !c.isSelected()).findFirst();
					if (!notSelectedItem.isPresent())
						if (cba.isPresent())
							cba.get().setSelected(true);
					imgRefresher.run();
				}
			}
		});

	}
	
	public static Stream<CheckBoxItemFilter> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream().filter(n -> n instanceof CheckBoxItemFilter)
				.map(n -> ((CheckBoxItemFilter) n));
	}
	
	public Supplier<String> getNameGetter() {
		return nameGetter;
	}

	public void setNameGetter(Supplier<String> nameGetter) {
		this.nameGetter = nameGetter;
	}

	@Override
	public void setComboBoxParent(IMultiSelectComboBox multiSelectComboBox) {
		this.parent = multiSelectComboBox;
	}

	@Override
	public IMultiSelectComboBox getComboBoxParent() {
		return parent;
	}

	@Override
	public void reset() {
		this.setSelected(true);
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public boolean isDefault() {
		return this.isSelected();
	}

}