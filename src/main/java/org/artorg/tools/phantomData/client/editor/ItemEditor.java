package org.artorg.tools.phantomData.client.editor;

import java.util.Collection;
import java.util.List;
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

import huma.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ItemEditor<T> extends Creator<T> {
	private final Button applyButton;
	private final ICrudConnector<T> connector;
	private T createTemplate;
	private T item;
	private Class<? extends T> beanClass;

	{
		applyButton = new Button("Apply");
	}

	public ItemEditor(Class<T> itemClass) {
		super(itemClass);
		this.beanClass = itemClass;
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
		T template = getCreateTemplate();
		if (template == null) template = createInstance();
		final T template2 = template;
		getAllAbstractEditors().stream().forEach(node -> {
			node.entityToNodeAdd(template2);
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
		item = createInstance();
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

	public void closeTitledNonGeneralPanes() {
		List<TitledPropertyPane> propertyPanes = getTitledNonPropertyPanes();
		propertyPanes.forEach(pane -> pane.setExpanded(false));
	}
	
//	public void closeTitledSelectors() {
//		List<TitledPropertyPane> propertyPanes = getTitledSelectorPanes();
//		propertyPanes.forEach(pane -> pane.setExpanded(false));
//	}

	public void addAutoCloseOnNonGeneral() {
		final List<TitledPropertyPane> propertyPanes = getTitledNonPropertyPanes();
		propertyPanes.forEach(pane -> {
			pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) propertyPanes.stream().filter(pane2 -> pane2 != pane)
						.forEach(pane2 -> pane2.setExpanded(false));
			});
		});
	}

//	public void addAutoCloseOnSelectors() {
//		List<TitledPropertyPane> propertyPanes = getTitledSelectorPanes();
//		propertyPanes.forEach(pane -> {
//			pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
//				if (newValue) propertyPanes.stream().filter(pane2 -> pane2 != pane)
//						.forEach(pane2 -> pane2.setExpanded(false));
//			});
//		});
//	}
	
	public List<TitledPropertyPane> getTitledNonPropertyPanes() {
		return getTitledPropertyPanes().stream()
				.filter(propertyPane -> !propertyPane.getText().equalsIgnoreCase("General"))
				.collect(Collectors.toList());
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

	@SuppressWarnings("unchecked")
	public Collection<AbstractEditor<T, ?>> getAllAbstractEditors() {
		List<IPropertyNode> nodes = flatToList();
		return nodes.stream().collect(StreamUtils.castFilter(node -> (AbstractEditor<T, ?>) node))
				.collect(Collectors.toList());
	}

	public void addApplyButton() {
		Button button = getApplyButton();
		Pane pane = createButtonPane(button);
		HBox.setHgrow(button, Priority.ALWAYS);
		pane.setPadding(new Insets(5, 10, 5, 10));
		getvBox().getChildren().add(pane);
	}

	public static Pane createButtonPane(Button... buttons) {
		StackPane buttonPane = new StackPane();
		HBox hBox = new HBox();
		hBox.setSpacing(10.0);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		FxUtil.addToPane(buttonPane, hBox);
		for (Button button : buttons) {
			button.setMaxWidth(Double.MAX_VALUE);
			VBox.setVgrow(button, Priority.NEVER);
			hBox.getChildren().add(button);
		}
		return buttonPane;
	}

	@Override
	public Node getNode() {
		return this;
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
