package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItems;

import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;
import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class TextItemSearch extends TextField implements Item {
	private IMultiSelectComboBox parent;
	
	public TextItemSearch(Image imgFilter, Runnable imgRefresher) {
		
		TextItemSearch reference = this;
		this.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (!newValue.isEmpty())
					parent.setImage(imgFilter);
				else 
					imgRefresher.run();
			}
		});
		
	}
	
	public static Stream<TextItemSearch> stream(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getBoxItemStream().filter(n -> n instanceof TextItemSearch)
				.map(n -> ((TextItemSearch) n));
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
		this.clear();
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public boolean isDefault() {
		return this.getText().isEmpty();
	}

}
