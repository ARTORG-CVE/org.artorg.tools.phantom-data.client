package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class ButtonReset extends Button implements Item {
	private IMultiSelectComboBox parent;
	
	public ButtonReset(Runnable imgRefresher) {
		this.setText("Reset Filtering");
		this.setStyle("-fx-background-color: transparent;");
		
		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				parent.getBoxItemStream().forEach(i -> i.reset());
				imgRefresher.run();;
			}
		});
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
	public void reset() {}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

}
