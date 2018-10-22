package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SmartTabPane {
	public static final HeaderMode SHOW;
	public static final HeaderMode REMOVE_IF_SINGLE;
	private HeaderMode headerMode;
	private TabPane tabPane;
	private Node contentNode;
	private Supplier<ObservableList<Node>> parentItemsSupplier;
	private boolean headerVisible = true;
	
	private Consumer<TabPane> addTabPanePolicy;
	private Consumer<TabPane> dividerPositionsPolicy;

	static {
		SHOW = new HeaderMode();
		REMOVE_IF_SINGLE = new HeaderMode();
	}
	
	public SmartTabPane(Supplier<ObservableList<Node>> parentItemsSupplier, Consumer<TabPane> addTabPanePolicy, Consumer<TabPane> dividerPositionsPolicy) {
		this.headerMode  = SHOW;
		this.parentItemsSupplier = parentItemsSupplier;
		this.addTabPanePolicy = addTabPanePolicy;
		this.dividerPositionsPolicy = dividerPositionsPolicy;
		this.tabPane = new TabPane();
		getTabPane().getTabs()
		.addListener(createTabsListener());
		contentNode = null;
		
	}
	
	public static class HeaderMode {}

	public boolean isHeaderVisible() {
		return headerVisible;
	}

	public void removeHeader(HeaderMode headerMode) {
		if (headerMode == REMOVE_IF_SINGLE)
			if (tabPane.getTabs().size() == 1)
				removeHeader();
	}

	public void removeHeader() {
		ObservableList<Node> parentNodes = parentItemsSupplier.get();
		for (int i = 0; i < parentNodes.size(); i++)
			if (parentNodes.get(i) == tabPane) {
				int index = parentNodes.indexOf(tabPane);
				parentNodes.remove(index);
				contentNode = tabPane.getTabs().get(0).getContent();
				parentNodes.add(index, contentNode);
				headerVisible = false;
			}
	}
	
	public void showHeader(HeaderMode headerMode) {
		if (headerMode == REMOVE_IF_SINGLE) {
			if (tabPane.getTabs().size() > 1)
				showHeader();
		} else if (headerMode == SHOW)
			showHeader();
	}

	public void showHeader() {
		ObservableList<Node> parentNodes = parentItemsSupplier.get();
		int selectedTabIndex = getTabPane().getSelectionModel()
			.getSelectedIndex();
		for (int i = 0; i < parentNodes.size(); i++)
			if (parentNodes.get(i) == contentNode) {
				int index = parentNodes.indexOf(contentNode);
				parentNodes.remove(index);

				TabPane oldTabPane = tabPane;
				tabPane = new TabPane();
				parentNodes.add(index, tabPane);
				List<Tab> tabs = oldTabPane.getTabs();
				for (int j = 0; j < tabs.size(); j++) {
					tabPane.getTabs().add(tabs.get(j));
					tabPane.getSelectionModel().select(j);
				}
				tabPane.getSelectionModel().select(selectedTabIndex);
				tabPane.getTabs()
					.addListener(createTabsListener());
				headerVisible = true;
			}
	}

	private ListChangeListener<Tab> createTabsListener() {
		ListChangeListener<Tab> changeListener = new ListChangeListener<Tab>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onChanged(Change<? extends Tab> c) {
				if (c.next()) {
					if (c.wasAdded()) {
						boolean initialized = !(contentNode == null);
						if (!initialized)
							contentNode = tabPane.getTabs().get(0).getContent();						
						
						showHeader(headerMode);
						List<Tab> tabs = (List<Tab>) c.getAddedSubList();
						tabs.forEach(addedTab -> addedTab.contentProperty().addListener(createContentListener()));
						getTabPane().getSelectionModel()
							.select(tabs.get(tabs.size() - 1));
						ObservableList<Node> parentNodes = parentItemsSupplier
							.get();
						
						if (isHeaderVisible())
							if (!parentNodes.contains(tabPane))
								addTabPanePolicy.accept(tabPane);
						
						if (isHeaderVisible()) {
//							if (!parentNodes.contains(tabPane))
//								parentNodes.add(tabPane);
						} else if (!parentNodes.contains(contentNode))
							parentNodes.add(contentNode);
						
						
						dividerPositionsPolicy.accept(tabPane);
						
						if (!initialized)
							removeHeader(headerMode);
					} else if (c.wasRemoved()) {
						removeHeader(headerMode);
						if (tabPane.getTabs().size() == 0)  {
							parentItemsSupplier
								.get().removeAll(tabPane, contentNode);
							contentNode = null;
						}						
					}
				}
				getTabPane().getSelectionModel()
					.select(getTabPane().getTabs().size() - 1);
			}
		};
		return changeListener;
	}
	
	private ChangeListener<Node> createContentListener() {
		ChangeListener<Node> contentListener = new ChangeListener<Node>() {
			@Override
			public void changed(
				ObservableValue<? extends Node> observable, Node oldValue,
				Node newValue) {
				if (!isHeaderVisible()) {
					ObservableList<Node> parentNodes = parentItemsSupplier
						.get();
					int index = parentNodes.indexOf(oldValue);
					parentNodes.set(index, newValue);
					contentNode = newValue;
				}
			}
		};
		return contentListener;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

}
