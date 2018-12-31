package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException.Mode;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.client.util.FxUtil;

import huma.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public abstract class ItemEditor<T> extends PropertyNode<T> {
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private final Creator<T> creator;
	private T item;

	{
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		super(itemClass);
		this.connector = (ICrudConnector<T>) Connectors.get(itemClass);
		creator = new Creator<>(itemClass);
		createPropertyGridPanes(creator);
		createSelectors(creator);
	}

	public abstract void createPropertyGridPanes(Creator<T> creator);

	public abstract void createSelectors(Creator<T> creator);

	public void onInputCheck() throws InvalidUIInputException {}

	public void onCreateInit(T item) {}

	public void onCreateBeforeApplyChanges(T item)
			throws PostException, InvalidUIInputException, NoUserLoggedInException {}

	public void onCreateBeforePost(T item)
			throws NoUserLoggedInException, PostException, InvalidUIInputException {}

	public void onCreatePostSuccessful(T item) {}

	public void onEditInit(T item) {}

	public void onEditBeforeApplyChanges(T item)
			throws PostException, InvalidUIInputException, NoUserLoggedInException {}

	public void onEditBeforePut(T item) {}

	public void onEditPutSuccessful(T item) {}

	@Override
	public Node getControlNode() {
		return this;
	}
	
	public final void showCreateMode() {
		showCreateMode(null);
	}

	public final void showCreateMode(T item) {
		this.item = item;
		onCreateInit(item);
		applyButton.setOnAction(event -> {
			if (getPropertyChildren().isEmpty()) Logger.warn.println("Nodes empty");
			FxUtil.runNewSingleThreaded(() -> {
				try {
					createItem(item);
				} catch (PostException e) {
					e.printStackTrace();
					Logger.warn.println(e.getMessage());
					e.showAlert();
				} catch (InvalidUIInputException e) {
					e.setMode(Mode.CREATE);
					Logger.warn.println(e.getMessage());
					e.showAlert();
				} catch (NoUserLoggedInException e) {
					Logger.warn.println(e.getMessage());
					e.showAlert();
				}
			});
		});
		applyButton.setText("Create");
		getAllAbstractEditors().stream().forEach(node -> node.entityToNodeAdd(item));
	}

	public final void showEditMode(T item) {
		this.item = item;
		onEditInit(item);
		applyButton.setOnAction(event -> {
			if (getPropertyChildren().isEmpty()) return;
			try {
				editItem(item);
			} catch (PutException e) {
				Logger.warn.println(e.getMessage());
				e.printStackTrace();
				e.showAlert();
			} catch (InvalidUIInputException e) {
				e.setMode(Mode.EDIT);
				Logger.warn.println(e.getMessage());
				e.showAlert();
			} catch (PostException e) {
				Logger.warn.println(e.getMessage());
				e.printStackTrace();
				e.showAlert();
			} catch (NoUserLoggedInException e) {
				Logger.warn.println(e.getMessage());
				e.showAlert();
			}
		});
		applyButton.setText("Save");
		getAllAbstractEditors().stream().forEach(node -> node.entityToNodeEdit(item));
	}

	public final T createItem()
			throws PostException, InvalidUIInputException, NoUserLoggedInException {
		return createItem(null);
	}

	public final T createItem(T item)
			throws PostException, InvalidUIInputException, NoUserLoggedInException {
		if (item == null) {
			try {
				item = getItemClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				PostException e1 = new PostException(getItemClass());
				e1.addSuppressed(e);
				throw e1;
			}
		}
		final T item2 = item;
		onCreateBeforeApplyChanges(item2);
		onInputCheck();
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(item2));
		onCreateBeforePost(item2);
		if (getConnector().create(item2)) {
			this.item = item2;
			onCreatePostSuccessful(item2);
			Platform.runLater(
					() -> getAllAbstractEditors().stream().forEach(node -> node.entityToNodeAdd(item2)));
			return item2;
		}
		throw new PostException(getItemClass());
	}

	public final boolean editItem(T item)
			throws PutException, InvalidUIInputException, PostException, NoUserLoggedInException {
		onEditBeforeApplyChanges(item);
		onInputCheck();
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(item));
		onEditBeforePut(item);
		if (getConnector().update(item)) {
			this.item = item;
			onEditPutSuccessful(item);
			return true;
		}
		return false;
	}

//	public Collection<PropertyGridPane> getAllPropertyGridPanes() {
//		
//	}
	public List<PropertyGridPane<T>> getAllPropertyGridPanes() {
		return getPropertyChildren().stream().map(propertyNode -> getAllPropertyGridPanes(propertyNode))
				.flatMap(nodes -> nodes.stream()).collect(Collectors.toList());
	}

	private List<PropertyGridPane<T>> getAllPropertyGridPanes(IPropertyNode<T> propertyNode) {
		List<PropertyGridPane<T>> list = new ArrayList<>();
		if (propertyNode instanceof PropertyGridPane) {
			list.add((PropertyGridPane<T>) propertyNode);
			return list;
		}
		if (propertyNode.getPropertyChildren().isEmpty()) return list;
		for (IPropertyNode<T> child : propertyNode.getPropertyChildren()) {
			if (child instanceof PropertyGridPane) list.add((PropertyGridPane<T>) child);
			else if (!child.getPropertyChildren().isEmpty())
				list.addAll(getAllPropertyGridPanes(child));
		}
		return list;
	}

	public Collection<AbstractEditor<T, ?>> getAllAbstractEditors() {
		return getPropertyChildren().stream().map(propertyNode -> getAllSubEditors(propertyNode))
				.flatMap(nodes -> nodes.stream()).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Collection<AbstractEditor<T, ?>> getAllSubEditors(IPropertyNode<T> propertyNode) {
		List<AbstractEditor<T, ?>> list = new ArrayList<>();
		if (!propertyNode.getPropertyChildren().isEmpty()) {
			for (IPropertyNode<T> child : propertyNode.getPropertyChildren()) {
				if (child.getPropertyChildren().isEmpty() && child instanceof AbstractEditor)
					list.add((AbstractEditor<T, ?>) child);
				else
					list.addAll(getAllSubEditors(child));
			}
			return list;
		}
		if (propertyNode instanceof AbstractEditor) list.add((AbstractEditor<T, ?>) propertyNode);
		return list;
	}

//	public <U> void add(ItemEditor<U> subEditor) {
//		Collection<AbstractEditor<T, ?, ?>> list = getAllSubEditors(nodes).stream()
//				.map(propertyNode -> propertyNode.map(itemClass, item -> subEditor.getItem()))
//				.collect(Collectors.toList());
//		nodes.addAll(list);
//	}

//	public TitledPane createTitledPane(List<PropertyEntry> entries, String title) {
//		TitledPane titledPane = new TitledPane();
//		PropertyGridPane<T> gridPane = new PropertyGridPane<>(itemClass, entries);
//		titledPane.setText(title);
//		titledPane.setContent(gridPane);
//		return titledPane;
//	}
//
//	public PropertyGridPane createUntitledPane(List<PropertyEntry> entries) {
//		return new PropertyGridPane(entries);
//	}

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

	public ICrudConnector<T> getConnector() {
		return this.connector;
	}

	public Button getApplyButton() {
		return applyButton;
	}

	public T getItem() {
		return item;
	}

	public Creator<T> getCreator() {
		return creator;
	}

}
