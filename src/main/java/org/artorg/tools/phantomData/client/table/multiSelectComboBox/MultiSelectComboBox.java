package org.artorg.tools.phantomData.client.table.multiSelectComboBox;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem.Item;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class MultiSelectComboBox extends ComboBox<Node> implements IMultiSelectComboBox {
	private List<Item> boxItems;
	private ObservableList<Node> nodes;
	
	public ObservableList<Node> getNodes() {
		return nodes;
	}

	public void setBoxItems(List<Item> boxItems) {
		this.boxItems = boxItems;
		List<Node> nodes = boxItems.stream().map(b -> b.getNode()).collect(Collectors.toList());
		setNodes(nodes);
	}
	
	private void setNodes(List<Node> nodes) {
		this.nodes = FXCollections.observableArrayList(nodes);
		setItems(this.nodes);

		Callback<ListView<Node>, ListCell<Node>> cb = new Callback<ListView<Node>, ListCell<Node>>() {
			@Override
			public ListCell<Node> call(ListView<Node> param) {
				ListCell<Node> cell = new ListCell<Node>() {
					@Override
					protected void updateItem(Node item, boolean empty) {
						super.updateItem(item, empty);
						setGraphic(item);
					}
				};
				return cell;
			}
		};

		setButtonCell(buttonCell);
		setCellFactory(cb);
	}

	private ListCell<Node> buttonCell = new ListCell<Node>() {
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
		}
	};
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new ComboBoxListViewSkin<Node>(this){
			@Override
		    protected boolean isHideOnClickEnabled() {
				return false;
		    }
		};
	}

	public List<Node> getListProvider() {
		return nodes;
	}

	@Override
	public Stream<Item> getBoxItemStream() {
		return boxItems.stream();
	}

	@Override
	public void setImage(Image image) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
//				Object o = sPane.getChildren().get(0);
//				System.out.println("Arrow Image: " +o.getClass());
				
				
//				sPane.getChildren().clear();
//				ImageView imgView = new ImageView(image);
//				sPane.getChildren().add(imgView);
			}
		});
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
