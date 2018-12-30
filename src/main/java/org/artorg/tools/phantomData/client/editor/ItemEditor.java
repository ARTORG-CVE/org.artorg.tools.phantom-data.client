package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.util.FxUtil;

import huma.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public abstract class ItemEditor<T> extends AnchorPane {
	private final Class<T> itemClass;
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private final List<PropertyNode<T, ?>> nodes;
	private final List<PropertyNode<T, ?>> readOnlyNodes;
	private final Creator<T> creator;
	private final VBox vBox;
	private T item;

	{
		nodes = new ArrayList<>();
		readOnlyNodes = Collections.unmodifiableList(nodes);
		applyButton = new Button("Apply");
		vBox = new VBox();
		FxUtil.addToPane(this, vBox);
	}

	public ItemEditor(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.connector = (ICrudConnector<T>) Connectors.get(itemClass);
		creator = new Creator<>(itemClass, this);
		createPropertyGridPanes(creator);
		createSelectors(creator);
	}

	public abstract void createPropertyGridPanes(Creator<T> creator);

	public abstract void createSelectors(Creator<T> creator);

	public void onInputCheck() throws InvalidUIInputException {}

	public void onCreateInit(T item) {}

	public void onCreateBeforeApplyChanges(T item) throws PostException, InvalidUIInputException, NoUserLoggedInException {}

	public void onCreateBeforePost(T item) throws NoUserLoggedInException, PostException, InvalidUIInputException {}

	public void onCreatePostSuccessful(T item) {}

	public void onEditInit(T item) {}

	public void onEditBeforeApplyChanges(T item) throws PostException, InvalidUIInputException, NoUserLoggedInException {}

	public void onEditBeforePut(T item) {}

	public void onEditPutSuccessful(T item) {}

	public void addPropertyNode(PropertyNode<T, ?> propertyNode) {
		nodes.add(propertyNode);
	}

	public final void showCreateMode() {
		showCreateMode(null);
	}

	public final void showCreateMode(T item) {
		this.item = item;
		onCreateInit(item);
		applyButton.setOnAction(event -> {
			if (nodes.isEmpty()) Logger.warn.println("Nodes empty");
			FxUtil.runNewSingleThreaded(() -> {
				try {
					createItem(item);
				} catch (PostException e) {
					e.printStackTrace();
					Logger.warn.println(e.getMessage());
					e.showAlert();
				} catch (InvalidUIInputException e) {
					handleUIException(e, true);
				} catch (NoUserLoggedInException e) {
					Logger.warn.println(e.getMessage());
					e.showAlert();
				}
			});
		});
		applyButton.setText("Create");
		nodes.stream().forEach(node -> node.entityToNodeAdd(item));
	}

	public final void showEditMode(T item) {
		this.item = item;
		onEditInit(item);
		applyButton.setOnAction(event -> {
			if (nodes.isEmpty()) return;
			
			try {
				editItem(item);
			} catch (PutException e) {
				e.printStackTrace();
				Logger.warn.println(e.getMessage());
				e.showAlert();
			} catch (InvalidUIInputException e) {
				handleUIException(e, false);
			} catch (PostException e) {
				e.printStackTrace();
				Logger.warn.println(e.getMessage());
				e.showAlert();
			} catch (NoUserLoggedInException e) {
				Logger.warn.println(e.getMessage());
				e.showAlert();
			}
			
//			try {
//				onInputCheck();
//			} catch (InvalidUIInputException e) {
//				handleUIException(e, false);
//				return;
//			}
//			onEditBeforApplyChanges(item);
//			nodes.stream().forEach(node -> node.nodeToEntity(item));
//			onEditBeforePut(item);
//			if (getConnector().update(item)) {
//				this.item = item;
//				onEditPutSuccessful(item);
//			}
		});
		applyButton.setText("Save");
		nodes.stream().forEach(node -> node.entityToNodeEdit(item));
	}

	public final T createItem() throws PostException, InvalidUIInputException, NoUserLoggedInException {
		return createItem(null);
	}
	
	public final T createItem(T item) throws PostException, InvalidUIInputException, NoUserLoggedInException {
		
		if (item == null) {
			try {
				item = getItemClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				PostException e1 = new PostException(itemClass);
				e1.addSuppressed(e);
				throw e1;
			}
		}
		final T item2 = item;
		onCreateBeforeApplyChanges(item2);
		onInputCheck();
		nodes.stream().forEach(node -> node.nodeToEntity(item2));
		onCreateBeforePost(item2);
		if (getConnector().create(item2)) {
			this.item = item2;
			onCreatePostSuccessful(item2);
			Platform.runLater(
					() -> nodes.stream().forEach(node -> node.entityToNodeAdd(item2)));
			return item2;
		}
		throw new PostException(getItemClass());
	}

	public final boolean editItem(T item) throws PutException, InvalidUIInputException, PostException, NoUserLoggedInException {
		onEditBeforeApplyChanges(item);
		onInputCheck();
		nodes.stream().forEach(node -> node.nodeToEntity(item));
		onEditBeforePut(item);
		if (getConnector().update(item)) {
			this.item = item;
			onEditPutSuccessful(item);
			return true;
		}
		return false;
	}

	private boolean handleUIException(InvalidUIInputException e, boolean creating) {
		try {
			if (creating) Logger.warn.println(String.format("Can't create %s: %s",
					itemClass.getSimpleName(), e.getMessage()));
			else
				Logger.warn.println(String.format("Can't edit %s: %s", itemClass.getSimpleName(),
						e.getMessage()));
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.WARNING);
				if (creating) {
					alert.setTitle("Create " + itemClass.getSimpleName());
					alert.setContentText(String.format("Can't create %s:\n%s",
							itemClass.getSimpleName(), e.getMessage()));
				} else {
					alert.setTitle("Edit " + itemClass.getSimpleName());
					alert.setContentText(String.format("Can't edit %s:\n%s",
							itemClass.getSimpleName(), e.getMessage()));
				}
				alert.showAndWait();
			});
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	public <U> void addPropertyNodes(ItemEditor<U> subEditor) {
		Collection<PropertyNode<T, ?>> list = subEditor.getNodes().stream()
				.map(propertyNode -> propertyNode.map(itemClass, item -> subEditor.getItem()))
				.collect(Collectors.toList());
		nodes.addAll(list);
	}

	public TitledPane createTitledPane(List<PropertyEntry> entries, String title) {
		TitledPane titledPane = new TitledPane();
		PropertyGridPane gridPane = new PropertyGridPane(entries);
		titledPane.setText(title);
		titledPane.setContent(gridPane);
		return titledPane;
	}

	public PropertyGridPane createUntitledPane(List<PropertyEntry> entries) {
		return new PropertyGridPane(entries);
	}

	public void addApplyButton() {
		getvBox().getChildren().add(createButtonPane(getApplyButton()));
	}

	public AnchorPane createButtonPane(Button button) {
		button.setPrefHeight(25.0);
		button.setMaxWidth(Double.MAX_VALUE);
		AnchorPane buttonPane = new AnchorPane();
		buttonPane.setPrefHeight(button.getPrefHeight() + 20);
		buttonPane.setMaxHeight(buttonPane.getPrefHeight());
		buttonPane.setPadding(new Insets(5, 10, 5, 10));
		buttonPane.getChildren().add(button);
		FxUtil.setAnchorZero(button);
		return buttonPane;
	}

	public VBox getvBox() {
		return vBox;
	}

	public Class<T> getItemClass() {
		return itemClass;
	}

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public Button getApplyButton() {
		return applyButton;
	}

	public List<PropertyNode<T, ?>> getNodes() {
		return readOnlyNodes;
	}

	public T getItem() {
		return item;
	}

	public Creator<T> getCreator() {
		return creator;
	}

}
