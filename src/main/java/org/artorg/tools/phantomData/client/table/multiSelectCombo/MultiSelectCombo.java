package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.FilterItemListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class MultiSelectCombo extends ComboBox<Node> {
	private ObservableList<Node> nodes;
	private Image imgNormal, imgFilter;
	
	private List<FilterItemListener> listeners;
	
	{
		listeners = new ArrayList<FilterItemListener>();
	}
	
	public void addFilterChangedListener(FilterItemListener listener) {
		listeners.add(listener);
	}
	
	
	private class CheckBoxItem extends CheckBox {
		private Callable<String> nameGetter;

		private CheckBoxItem(Callable<String> nameGetter) {
			this.nameGetter = nameGetter;
			
			try {
				this.setText(nameGetter.call());
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setSelected(true);
			CheckBoxItem reference = this;
			this.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					reference.setSelected(newValue);

					List<String> newValues = nodes.stream().filter(n -> n instanceof CheckBoxItem).map(n -> ((CheckBoxItem) n))
							.filter(c -> c.isSelected())
							.map(c -> {
								try {
									return c.nameGetter.call();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return "";
							}).filter(s -> !s.equals("")).collect(Collectors.toList());
					
					listeners.stream().forEach(l -> l.changed(newValues));
					
					if (!reference.isSelected())
						setFilterImage();
					else {
						if (!nodes.stream().filter(n -> n instanceof CheckBoxItem).map(n -> ((CheckBoxItem) n))
								.filter(c -> !c.isSelected()).findFirst().isPresent())
							setArrowImage();
					}
				}
			});
		}

		public Callable<String> getNameGetter() {
			return nameGetter;
		}
	}

	private class CheckBoxAll extends CheckBox {
		private CheckBoxAll() {
			this.setSelected(true);
			this.setText("Select All");

			CheckBoxAll reference = this;
			this.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					reference.setSelected(newValue);

					nodes.stream().filter(n -> n instanceof CheckBoxItem).map(n -> ((CheckBoxItem) n))
							.forEach(c -> c.setSelected(newValue));

					if (newValue)
						setArrowImage();
					else
						setFilterImage();
				}
			});
		}
	}

	public CheckBoxItem createCheckBoxItem(Callable<String> nameGetter) {
		return new CheckBoxItem(nameGetter);
	}

	public CheckBoxAll createCheckBoxAll() {
		return new CheckBoxAll();
	}

	public void setNodes(List<Node> nodes) {
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
		return new MultiSelectComboSkin(this);
	}

	private void setFilterImage() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				sPane.getChildren().clear();
				ImageView imgView = new ImageView(imgFilter);
				sPane.getChildren().add(imgView);
			}
		});
	}

	private void setArrowImage() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				StackPane sPane = (StackPane) lookup(".arrow-button");
				sPane.getChildren().clear();
				ImageView imgView = new ImageView(imgNormal);
				sPane.getChildren().add(imgView);
			}
		});
	}

	// // Getters & Setters
	public Image getImgFilter() {
		return imgFilter;
	}

	public void setImgFilter(Image imgFilter) {
		this.imgFilter = imgFilter;
	}

	public Image getImgNormal() {
		return imgNormal;
	}

	public void setImgNormal(Image imgNormal) {
		this.imgNormal = imgNormal;
	}

	public List<Node> getListProvider() {
		return nodes;
	}

}
