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

public class ItemEditor<T> extends Creator<T> {
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private T item;

	{
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		super(itemClass);
		this.connector = (ICrudConnector<T>) Connectors.get(itemClass);
	}

	public void onShowingCreateMode(T item) {}

	public void onCreatingClient(T item) throws InvalidUIInputException {}

	public void onCreatingServer(T item) throws NoUserLoggedInException, PostException {}

	public void onCreatedServer(T item) {}

	public void onShowingEditMode(T item) {}

	public void onUpdatingClient(T item) throws InvalidUIInputException {}

	public void onUpdatingServer(T item)
			throws NoUserLoggedInException, PutException, PostException {}

	public void onUpdatedServer(T item) {}

	@Override
	public Node getNode() {
		return this;
	}

	public final void showCreateMode() {
		showCreateMode(null);
	}

	public final void showCreateMode(T item) {
		this.item = item;
		onShowingCreateMode(item);
		applyButton.setOnAction(event -> {
			if (getChildrenProperties().isEmpty()) Logger.warn.println("Nodes empty");
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
		onShowingEditMode(item);
		applyButton.setOnAction(event -> {
			if (getChildrenProperties().isEmpty()) return;
			try {
				updateItem(item);
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
		if (item == null) item = createEmpty();
		item = createClient(item);
		return createServer(item);
	}

	public final T createClient(T item)
			throws PostException, InvalidUIInputException, NoUserLoggedInException {
		onCreatingClient(item);
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(item));
		return item;
	}

	public final T createEmpty() {
		try {
			return getItemClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	public final T createServer(T item)
			throws NoUserLoggedInException, PostException, InvalidUIInputException {
		onCreatingServer(item);
		if (getConnector().create(item)) {
			this.item = item;
			onCreatedServer(item);
			Platform.runLater(() -> getAllAbstractEditors().stream()
					.forEach(node -> node.entityToNodeAdd(item)));
			return item;
		}
		throw new PostException(getItemClass());
	}

	public final boolean updateItem(T item)
			throws PutException, InvalidUIInputException, PostException, NoUserLoggedInException {
		updateClient(item);
		return updateServer(item);
	}

	public final void updateClient(T item)
			throws InvalidUIInputException, NoUserLoggedInException, PostException {
		onUpdatingClient(item);
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(item));
	}

	public final boolean updateServer(T item)
			throws NoUserLoggedInException, PutException, PostException {
		onUpdatingServer(item);
		if (getConnector().update(item)) {
			this.item = item;
			onUpdatedServer(item);
			return true;
		}
		return false;
	}

//	public Collection<PropertyGridPane> getAllPropertyGridPanes() {
//		
//	}
	public List<PropertyGridPane> getAllPropertyGridPanes() {
		return getChildrenProperties().stream()
				.map(propertyNode -> getAllPropertyGridPanes(propertyNode))
				.flatMap(nodes -> nodes.stream()).collect(Collectors.toList());
	}

	private List<PropertyGridPane> getAllPropertyGridPanes(IPropertyNode propertyNode) {
		List<PropertyGridPane> list = new ArrayList<>();
		if (propertyNode instanceof PropertyGridPane) {
			list.add((PropertyGridPane) propertyNode);
			return list;
		}
		if (propertyNode.getChildrenProperties().isEmpty()) return list;
		for (IPropertyNode child : propertyNode.getChildrenProperties()) {
			if (child instanceof PropertyGridPane) list.add((PropertyGridPane) child);
			else if (!child.getChildrenProperties().isEmpty())
				list.addAll(getAllPropertyGridPanes(child));
		}
		return list;
	}

	public Collection<AbstractEditor<T, ?>> getAllAbstractEditors() {
		return getChildrenProperties().stream().map(propertyNode -> getAllSubEditors(propertyNode))
				.flatMap(nodes -> nodes.stream()).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Collection<AbstractEditor<T, ?>> getAllSubEditors(IPropertyNode propertyNode) {
		List<AbstractEditor<T, ?>> list = new ArrayList<>();
		if (!propertyNode.getChildrenProperties().isEmpty()) {
			for (IPropertyNode child : propertyNode.getChildrenProperties()) {
				if (child.getChildrenProperties().isEmpty() && child instanceof AbstractEditor)
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

}
