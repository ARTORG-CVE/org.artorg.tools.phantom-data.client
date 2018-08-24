package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class CheckBoxItemFilterAll extends CheckBox implements Item {
	private IMultiSelectComboBox parent;
	
	public CheckBoxItemFilterAll(Image imgFilter, Runnable imgRefresher) {
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

				if (!reference.isSelected())
					reference.getComboBoxParent().setImage(imgFilter);
				else
					imgRefresher.run();
			}
		});

	}
	
	public static Stream<CheckBoxItemFilterAll> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream().filter(n -> n instanceof CheckBoxItemFilterAll)
				.map(n -> ((CheckBoxItemFilterAll) n));
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
