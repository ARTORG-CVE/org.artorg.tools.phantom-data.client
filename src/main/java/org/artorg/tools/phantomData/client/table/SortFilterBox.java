package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class SortFilterBox<ITEM> extends MultiSelectComboBox<ITEM> {

	public SortFilterBox(String name, Supplier<List<String>> getters,
			Comparator<? super ITEM> comparator) {
		super.setPromptText(name);
		super.setGetters(getters);
		super.setComparator(comparator);
		
		super.getNodes().add(0, new ButtonSortAscending());
	}
	
	private class ButtonSortAscending extends Button {
		private ButtonSortAscending() {
			this.setText("Sort Ascending");
			this.setStyle("-fx-background-color: transparent;");
			
			this.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("ACTION :)");
//					isSortComparatorSet = true;
//					
//					sortComparator = 
				}
				
			});


		}
	}

}
