package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException.Mode;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PermissionDeniedException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.server.model.DbPersistent;

import huma.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ItemEditor<T> extends Creator<T> {
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private T createTemplate;
	private T item;
	private Class<? extends T> beanClass;

	{
		applyButton = new Button("Apply");
//		itemSupplier = () -> {
//			try {
//				return getItemClass().newInstance();
//			} catch (InstantiationException | IllegalAccessException e) {}
//			throw new RuntimeException("Declare itemSupplier for itemClass: " + getItemClass());
//		};
	}

	public ItemEditor(Class<T> itemClass) {
		super(itemClass);
		this.connector = (ICrudConnector<T>) Connectors.get(itemClass);
	}

	public void onShowingCreateMode(Class<? extends T> itemClass) {}

	public void onCreatingClient(T item) throws InvalidUIInputException {}

	public void onCreatingServer(T item) throws NoUserLoggedInException, PostException {}

	public void onCreatedServer(T item) {}

	public void onShowingEditMode(T item) {}

	public void onUpdatingClient(T item) throws InvalidUIInputException {}

	public void onUpdatingServer(T item)
			throws NoUserLoggedInException, PutException, PostException {}

	public void onUpdatedServer(T item) {}

	public void closeTitledSelectors() {
		List<TitledPropertyPane> propertyPanes = getTitledSelectorPanes();
		propertyPanes.forEach(pane -> pane.setExpanded(false));
	}

	public void addAutoCloseOnSelectors() {
		List<TitledPropertyPane> propertyPanes = getTitledSelectorPanes();
		propertyPanes.forEach(pane -> {
			pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) propertyPanes.stream().filter(pane2 -> pane2 != pane)
						.forEach(pane2 -> pane2.setExpanded(false));
			});
		});
	}

	public List<TitledPropertyPane> getTitledPropertyPanes() {
		List<IPropertyNode> nodes = flatToList();
		return nodes.stream().collect(StreamUtils.castFilter(node -> (TitledPropertyPane) node))
				.collect(Collectors.toList());
	}

	public List<TitledPropertyPane> getTitledSelectorPanes() {
		List<IPropertyNode> nodes = flatToList();
		return nodes.stream().collect(StreamUtils.castFilter(node -> (TitledPropertyPane) node))
				.filter(pane -> pane.getContent() instanceof TableViewSelector)
				.collect(Collectors.toList());
	}

	@Override
	public Node getNode() {
		return this;
	}

	public final void showCreateMode() {
		onShowingCreateMode(beanClass);
		applyButton.setOnAction(event -> {
			if (getChildrenProperties().isEmpty()) Logger.warn.println("Nodes empty");
			FxUtil.runNewSingleThreaded(() -> {
				try {
					createItem();
					setCreateTemplate(getItem());
					setItem(createInstance());
					Platform.runLater(() -> getAllAbstractEditors().stream().forEach(node -> {
						T template = getCreateTemplate();
						if (template == null || template.getClass() != beanClass) 
							template = createInstance();
						node.entityToNodeAdd(template);
					}));
				} catch (InvalidUIInputException e) {
					e.setMode(Mode.CREATE);
					Logger.warn.println(e.getMessage());
					e.showAlert();
				} catch (NoUserLoggedInException e) {
					Logger.warn.println(e.getMessage());
					e.showAlert();
				} catch (PostException e) {
					e.printStackTrace();
					Logger.warn.println(e.getMessage());
					e.showAlert();
				}
			});
		});
		applyButton.setText("Create");
		getAllAbstractEditors().stream().forEach(node -> {
			T template = getCreateTemplate();
			if (template == null) template = createInstance();
			node.entityToNodeAdd(template);
		});
	}

	public T createInstance() {
		try {
			return beanClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final void showEditMode(T item) {
		beanClass = (Class<? extends T>) item.getClass();
		setItem(item);
		onShowingEditMode(item);
		applyButton.setOnAction(event -> {
			if (getChildrenProperties().isEmpty()) return;
			try {
				updateItem();
			} catch (InvalidUIInputException e) {
				e.setMode(Mode.EDIT);
				Logger.warn.println(e.getMessage());
				e.showAlert();
			} catch (NoUserLoggedInException e) {
				Logger.warn.println(e.getMessage());
				e.showAlert();
			} catch (PutException e) {
				Logger.warn.println(e.getMessage());
				e.printStackTrace();
				e.showAlert();
			} catch (PostException e) {
				Logger.warn.println(e.getMessage());
				e.printStackTrace();
				e.showAlert();
			} catch (PermissionDeniedException e) {
				e.showAlert();
			}
		});
		applyButton.setText("Save");
		getAllAbstractEditors().stream().forEach(node -> node.entityToNodeEdit(item));
	}

	public final T createItem()
			throws PostException, InvalidUIInputException, NoUserLoggedInException {
		if (item == null) throw new IllegalArgumentException();
		setItem(createClient());
		return createServer();
	}

	public final T createClient()
			throws PostException, InvalidUIInputException, NoUserLoggedInException {
		onCreatingClient(item);
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(getItem()));
		return item;
	}

	public final T createServer()
			throws NoUserLoggedInException, PostException, InvalidUIInputException {
		onCreatingServer(item);
		if (getConnector().create(item)) {
			onCreatedServer(item);
			return item;
		}
		throw new PostException(getItemClass());
	}

	public final boolean updateItem() throws PutException, InvalidUIInputException, PostException,
			NoUserLoggedInException, PermissionDeniedException {
		updateClient();
		return updateServer();
	}

	public final void updateClient()
			throws InvalidUIInputException, NoUserLoggedInException, PostException {
		onUpdatingClient(item);
		getAllAbstractEditors().stream().forEach(node -> node.nodeToEntity(item));
	}

	public final boolean updateServer()
			throws NoUserLoggedInException, PutException, PostException, PermissionDeniedException {
		onUpdatingServer(item);
		if (getConnector().update(item)) {
			onUpdatedServer(item);
			return true;
		}
		return false;
	}

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

	public void addApplyButton() {
		getvBox().getChildren().add(createButtonPane(getApplyButton()));
	}

	public AnchorPane createButtonPane(Button button) {
		button.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(button, Priority.NEVER);
		AnchorPane buttonPane = new AnchorPane();
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

	private void setItem(T item) {
		this.item = item;
	}

	public T getCreateTemplate() {
		return createTemplate;
	}

	public void setCreateTemplate(T createTemplate) {
		this.createTemplate = createTemplate;
	}

	public Class<? extends T> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<? extends T> beanClass) {
		this.beanClass = beanClass;
		T item = createInstance();
		setItem(item);
		setCreateTemplate(item);
	}

}
