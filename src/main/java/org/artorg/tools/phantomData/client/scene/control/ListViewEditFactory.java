package org.artorg.tools.phantomData.client.scene.control;

import java.util.Set;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ListViewEditFactory<ITEM extends DbPersistent<ITEM,?>> extends ListView<ITEM> {

	private void initTable() {
		super.setCellFactory(new Callback<ListView<ITEM>, ListCell<ITEM>>() {
			@Override
			public ListCell<ITEM> call(ListView<ITEM> list) {
				return new ListCell<ITEM>() {
					@Override
					public void updateItem(ITEM item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							Label label = new Label();
							String s = item.toString();
							label.setText(s);
							this.setGraphic(label);
						}
					}
				};
			}
		});

	}

	public void setItems(Set<ITEM> set) {
		ObservableList<ITEM> items = FXCollections.observableArrayList();
		items.addAll(set);
		super.setItems(items);
		initTable();
	}
}
