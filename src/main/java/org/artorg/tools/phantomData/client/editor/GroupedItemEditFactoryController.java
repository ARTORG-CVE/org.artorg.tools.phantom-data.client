package org.artorg.tools.phantomData.client.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.select.AbstractTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class GroupedItemEditFactoryController<T>
		extends ItemEditFactoryController<T> {
	private List<TitledPane> titledPanes;
	private Supplier<T> itemFactory;
	private Consumer<T> templateSetter;
	private Consumer<T> changeApplier;
	private List<PropertyEntry> entries;

	{
		titledPanes = new ArrayList<TitledPane>();
		entries = new ArrayList<PropertyEntry>();
	}

	@Override
	protected void addProperties(T item) {
		titledPanes.stream().filter(p -> p instanceof TitledPropertyPane)
				.forEach(p -> entries.addAll(((TitledPropertyPane) p).getEntries()));

		List<AbstractTableViewSelector<?>> selectors = this.getSelectors();
		titledPanes.addAll(selectors.stream().map(selector -> {
			if (selector.getGraphic() instanceof TitledPane)
				return (TitledPane) selector.getGraphic();
			TitledPane titledPane = new TitledPane();
			titledPane.setContent(selector.getGraphic());
			titledPane.setText(selector.getSubItemClass().getSimpleName());
			return titledPane;
		}).collect(Collectors.toList()));

	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		return entries;
	}

	@Override
	protected AnchorPane createRootPane() {
		AnchorPane rootPane = new AnchorPane();
		VBox vBox = new VBox();
		AnchorPane buttonPane = createButtonPane(applyButton);

		rootPane.getChildren().add(vBox);
		vBox.getChildren().addAll(titledPanes);
		vBox.getChildren().add(buttonPane);

		VBox.setVgrow(buttonPane, Priority.ALWAYS);
		FxUtil.setAnchorZero(vBox);

		return rootPane;
	}

	@Override
	public T createItem() {
		return itemFactory.get();
	}

	@Override
	protected void setEditTemplate(T item) {
		templateSetter.accept(item);
	}
	
	@Override
	protected void applyChanges(T item) {
		this.changeApplier.accept(item);
	}
	
	public List<TitledPane> getTitledPanes() {
		return titledPanes;
	}

	public void setTitledPanes(List<TitledPane> titledPanes) {
		this.titledPanes = titledPanes;
	}

	public Supplier<T> getItemFactory() {
		return itemFactory;
	}

	public void setItemFactory(Supplier<T> itemFactory) {
		this.itemFactory = itemFactory;
	}

	public Consumer<T> getTemplateSetter() {
		return templateSetter;
	}

	public void setTemplateSetter(Consumer<T> templateSetter) {
		this.templateSetter = templateSetter;
	}
	
	public Consumer<T> getChangeApplier() {
		return changeApplier;
	}

	public void setChangeApplier(Consumer<T> changeApplier) {
		this.changeApplier = changeApplier;
	}

	

}
