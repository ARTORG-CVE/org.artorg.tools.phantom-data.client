package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SmartTabPane extends SmartNode {
	private HeaderMode headerMode;
	private final TabPane tabPane;
	private Node contentNode;
	private boolean headerVisible = true;
	
	public SmartTabPane() {
		this.headerMode  = HeaderMode.SHOW;
		this.tabPane = new TabPane();
		getTabPane().getTabs()
		.addListener(createTabsListener());
		contentNode = null;
	}
	
	public enum HeaderMode {SHOW, REMOVE_IF_SINGLE}
	
	public boolean isHeaderVisible() {
		return headerVisible;
	}

	public void removeHeader(HeaderMode headerMode) {
		if (headerMode == HeaderMode.REMOVE_IF_SINGLE)
			if (tabPane.getTabs().size() == 1)
				removeHeader();
	}

	public void removeHeader() {
		ObservableList<Node> parentNodes = getParentItems();
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
		if (headerMode == HeaderMode.REMOVE_IF_SINGLE) {
			if (tabPane.getTabs().size() > 1)
				showHeader();
		} else if (headerMode == HeaderMode.SHOW)
			showHeader();
	}

	public void showHeader() {
		ObservableList<Node> parentNodes = getParentItems();
		int selectedTabIndex = getTabPane().getSelectionModel()
			.getSelectedIndex();
		for (int i = 0; i < parentNodes.size(); i++)
			if (parentNodes.get(i) == contentNode) {
				int index = parentNodes.indexOf(contentNode);
				
				parentNodes.set(index, tabPane);
				List<Tab> tabs = tabPane.getTabs();
				for (int j = 0; j < tabs.size(); j++)
					tabPane.getSelectionModel().select(j);
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
						ObservableList<Node> parentNodes = getParentItems();
						
						if (isHeaderVisible()) {
							if (!parentNodes.contains(tabPane)) {
								getNodeAddPolicy().accept(tabPane);
							}
						}
						else if (!parentNodes.contains(contentNode)) {
							getNodeAddPolicy().accept(contentNode);
						}
						
						if (!initialized)
							removeHeader(headerMode);
					} else if (c.wasRemoved()) {
						removeHeader(headerMode);
						if (tabPane.getTabs().size() == 0)  {
							getNodeRemovePolicy().accept(tabPane);
							getNodeRemovePolicy().accept(contentNode);
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
					ObservableList<Node> parentNodes = getParentItems();
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
