package org.artorg.tools.phantomData.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GroupedItemEditFactoryController<ITEM extends DbPersistent<ITEM, ?>>
		extends ItemEditFactoryController<ITEM> {
	private List<TitledPane> titledPanes;
	private Supplier<ITEM> itemFactory;
	private Consumer<ITEM> templateSetter;
	private BiConsumer<ITEM,ITEM> itemCopier;
	private List<PropertyEntry> entries;

	{
		titledPanes = new ArrayList<TitledPane>();
		entries = new ArrayList<PropertyEntry>();
	}

	@Override
	protected void addProperties(ITEM item) {
		titledPanes.stream().filter(p -> p instanceof TitledPropertyPane)
				.forEach(p -> entries.addAll(((TitledPropertyPane) p).getEntries()));

		List<AbstractTableViewSelector<ITEM>> selectors = this.getSelectors();
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
	public ITEM createItem() {
		return itemFactory.get();
	}

	@Override
	protected void setTemplate(ITEM item) {
		templateSetter.accept(item);
	}

	@Override
	protected void copy(ITEM from, ITEM to) {
		itemCopier.accept(from, to);		
	}
	
	public List<TitledPane> getTitledPanes() {
		return titledPanes;
	}

	public void setTitledPanes(List<TitledPane> titledPanes) {
		this.titledPanes = titledPanes;
	}

	public Supplier<ITEM> getItemFactory() {
		return itemFactory;
	}

	public void setItemFactory(Supplier<ITEM> itemFactory) {
		this.itemFactory = itemFactory;
	}

	public Consumer<ITEM> getTemplateSetter() {
		return templateSetter;
	}

	public void setTemplateSetter(Consumer<ITEM> templateSetter) {
		this.templateSetter = templateSetter;
	}

	public BiConsumer<ITEM,ITEM> getItemCopier() {
		return itemCopier;
	}

	public void setItemCopier(BiConsumer<ITEM,ITEM> itemCopier) {
		this.itemCopier = itemCopier;
	}

}
